package com.sparta.concurrencycontrolproject.domain.concert.service;

import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertRequest;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertResponse;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertUpdateRequest;
import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.sparta.concurrencycontrolproject.domain.seat.entity.Seat;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    // 공연 등록
    @Transactional
    public ConcertResponse registerConcert(UserDetailsImpl userDetails, ConcertRequest request) {
        validateAdminRole(userDetails); // 권한 확인 메서드 호출

        Concert concert = new Concert(
                request.getConcertName(),
                request.getPrice(),
                request.getDescription(),
                request.getImage(),
                request.getDate(),
                request.getStartAt()
        );
        //요청에 따른 Seat 리스트 생성
        List<Seat> seats = createSeats(concert, request.getSeatLetterRange(), request.getSeatNumberRange());
        concert.getSeats().addAll(seats);
        Concert savedConcert = concertRepository.save(concert);

        return new ConcertResponse(
                savedConcert.getId(),
                savedConcert.getConcertName(),
                savedConcert.getPrice(),
                savedConcert.getDescription(),
                savedConcert.getImage(),
                savedConcert.getDate(),
                savedConcert.getStartAt(),
                savedConcert.getSeats().size()
        );
    }

    // 공연 수정
    @Transactional
    public ConcertResponse updateConcert(UserDetailsImpl userDetails, Long concertId, ConcertUpdateRequest request) {
        validateAdminRole(userDetails); // 권한 확인 메서드 호출
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공연입니다."));

        concert.updateConcert(
                request.getConcertName(),
                request.getPrice(),
                request.getDescription(),
                request.getImage(),
                request.getDate(),
                request.getSeating()
        );

        return new ConcertResponse(
                concert.getId(),
                concert.getConcertName(),
                concert.getPrice(),
                concert.getDescription(),
                concert.getImage(),
                concert.getDate(),
                concert.getStartAt(),
                concert.getSeats().size()
        );
    }

    // 공연 전체 조회 및 검색
    @Transactional(readOnly = true)
    public Page<ConcertResponse> getConcerts(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        if (name != null && !name.isBlank()) {
            return concertRepository.findByConcertNameContaining(name, pageable).map(this::mapToConcertResponse);
        }

        return concertRepository.findAll(pageable).map(this::mapToConcertResponse);
    }

    // 권한 확인 메서드
    private void validateAdminRole(UserDetailsImpl userDetails) {
        if (!userDetails.isAdmin()) {
            throw new IllegalArgumentException("해당 작업은 ADMIN 권한이 필요합니다.");
        }
    }

    // Concert -> ConcertResponse 매핑 메서드
    private ConcertResponse mapToConcertResponse(Concert concert) {
        return new ConcertResponse(
                concert.getId(),
                concert.getConcertName(),
                concert.getPrice(),
                concert.getDescription(),
                concert.getImage(),
                concert.getDate(),
                concert.getStartAt(),
                concert.getSeats().size()
        );
    }

    //편의 메서드
    private List<Seat> createSeats(Concert concert, String letterRange, String numberRange) {
        List<Seat> seats = new ArrayList<>();

        // 문자 범위 파싱
        char startLetter = letterRange.charAt(0);
        char endLetter = letterRange.charAt(2);

        // 숫자 범위 파싱
        int startNumber = Integer.parseInt(numberRange.split("-")[0]);
        int endNumber = Integer.parseInt(numberRange.split("-")[1]);

        // 좌석 생성
        for (char letter = startLetter; letter <= endLetter; letter++) {
            for (int number = startNumber; number <= endNumber; number++) {
                String seatNumber = letter + String.valueOf(number);
                seats.add(new Seat(concert, seatNumber));
            }
        }

        return seats;
    }
}
