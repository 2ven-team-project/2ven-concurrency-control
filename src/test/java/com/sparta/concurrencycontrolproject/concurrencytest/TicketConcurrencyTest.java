package com.sparta.concurrencycontrolproject.concurrencytest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;

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
public class TicketConcurrencyTest {
	@Autowired
	private TicketService ticketService;
	@Autowired
	private SeatRepository seatRepository;
	@Autowired
	private ConcertRepository concertRepository;
	@Autowired
	private MemberRepository memberRepository;
	private Long concertId;
	private String seatNumber = "A1";
	private String userName = "testUser";

	private List<String> successfulBookings;

	@BeforeEach
	void setUp() {
		//String concertName, int price, String description, String image,
		//         LocalDateTime date, LocalDateTime startAt
		Concert concert = new Concert("Test Concert", 100000, "this is Test Concert", "image",
			LocalDateTime.of(2024, 12, 25, 18, 0, 0),
			LocalDateTime.of(2024, 12, 25, 18, 0, 0));
		concertRepository.save(concert);
		concertId = concert.getId();

		Seat seat = new Seat(concert, seatNumber);
		seatRepository.save(seat);

		Member member = new Member("test@test.com", userName, "password123", MemberRole.USER);
		memberRepository.save(member);

		successfulBookings = new CopyOnWriteArrayList<>();
	}

	@Test
	@Rollback
	void testConcurrencySeatSelectionWithoutLock() throws InterruptedException {
		int numberOfThreads = 100;
		ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
		CountDownLatch readyLatch = new CountDownLatch(numberOfThreads);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

		for (int i = 0; i < numberOfThreads; i++) {
			int threadIndex = i;
			executorService.submit(() -> {
				readyLatch.countDown(); // 준비 완료
				try {
					startLatch.await(); // 모든 스레드가 동시에 실행되도록 대기
					String userName = "user" + threadIndex;
					ticketService.selectSeat(concertId, seatNumber, userName);
					successfulBookings.add(userName);
				} catch (Exception e) {
					System.out.println("Thread " + threadIndex + " failed: " + e.getMessage());
				} finally {
					doneLatch.countDown();
				}
			});
		}

		readyLatch.await(); // 모든 스레드 준비 완료 확인
		startLatch.countDown(); // 모든 스레드 시작
		doneLatch.await(); // 모든 스레드 종료 대기
		executorService.shutdown();

		// 결과 출력
		System.out.println("Successful bookings: " + successfulBookings);
		System.out.println("Total successful bookings: " + successfulBookings.size());

		Seat seat = seatRepository.findByConcertIdAndSeatNumber(concertId, seatNumber)
			.orElseThrow(() -> new IllegalStateException("좌석이 존재하지 않습니다."));

		System.out.println("Seat booked by: " + seat.getBookedBy());
		System.out.println("Seat booking status: " + (seat.isBooked() ? "Booked" : "Not Booked"));
	}

}