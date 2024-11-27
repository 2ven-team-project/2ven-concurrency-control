package com.sparta.concurrencycontrolproject.concurrencytest2;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;

import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.sparta.concurrencycontrolproject.domain.member.entity.Member;
import com.sparta.concurrencycontrolproject.domain.member.entity.MemberRole;
import com.sparta.concurrencycontrolproject.domain.member.repository.MemberRepository;
import com.sparta.concurrencycontrolproject.domain.seat.entity.Seat;
import com.sparta.concurrencycontrolproject.domain.seat.repository.SeatRepository;
import com.sparta.concurrencycontrolproject.domain.ticket.service.TicketService;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TicketLockConcurrencyTest {
	@Autowired
	private TicketService ticketService;
	@Autowired
	private SeatRepository seatRepository;
	@Autowired
	private ConcertRepository concertRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private RedisTemplate<String, Long> redisTemplate;

	private Long concertId;
	private String seatNumber = "A1";

	@BeforeEach
	void setUp() {
		Concert concert = new Concert("TestConcert", 1000, "this is a test concert", "image",
			LocalDateTime.of(2024, 11, 30, 18, 0, 0),
			LocalDateTime.of(2024, 11, 30, 18, 0, 0));

		concertRepository.save(concert);
		concertId = concert.getId();

		Seat seat = new Seat(concert, seatNumber);
		seatRepository.save(seat);

		for (int i = 0; i < 30; i++) {
			Member member = new Member("test" + i + "@test.com", "user" + i, "password123", MemberRole.USER);
			memberRepository.save(member);
		}
	}

	@Test
	void testSelectSeatWithLock() throws InterruptedException {
		int numberOfThread = 30;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThread);
		CountDownLatch readyLatch = new CountDownLatch(numberOfThread);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(numberOfThread);

		List<String> successfulUsers = new CopyOnWriteArrayList<>();
		List<String> failedUsers = new CopyOnWriteArrayList<>();

		for (int i = 0; i < numberOfThread; i++) {
			int threadIndex = i;
			executorService.submit(() -> {
				try {
					readyLatch.countDown(); //스레드 준비 완료
					startLatch.await(); // 모든 스레드가 시작 신호를 기다렸다가

					Long memberId = memberRepository.findByName("user"+threadIndex)
						.orElseThrow(()-> new IllegalArgumentException("멤버가 존재하지 않습니다."))
						.getId();

					ticketService.selectSeatWithLock(concertId, seatNumber, memberId);
					successfulUsers.add("user" + threadIndex);
				} catch (Exception e) {
					failedUsers.add("user" + threadIndex);
				} finally {
					doneLatch.countDown(); // 작업 끝
				}
			});
		}

		readyLatch.await(); // 모든 스레드 준비 완
		startLatch.countDown(); // 모든 스레드에 시작신호 보내기
		doneLatch.await(); // 모든 작업 끝날때 까지 대기
		executorService.shutdown();

		System.out.println("성공한 유저: " + successfulUsers);
		System.out.println("실패한 유저: " + failedUsers);
		System.out.println("전체 예약 성공: " + successfulUsers.size());
		System.out.println("전체 예약 실패: " + failedUsers.size());

		Assertions.assertEquals(1, successfulUsers.size());

		Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)
			.orElseThrow(() -> new IllegalArgumentException("좌석이 존재하지 않습니다."));

		System.out.println("좌석에 예약된 유저이름: " + seat.getBookedBy());
		Assertions.assertTrue(seat.isBooked());
	}
}
