package com.sparta.concurrencycontrolproject.domain.concert.service;

import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertRequest;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertResponse;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertUpdateRequest;
import com.sparta.concurrencycontrolproject.domain.concert.entity.Concert;
import com.sparta.concurrencycontrolproject.domain.concert.repository.ConcertRepository;
import com.sparta.concurrencycontrolproject.domain.seat.entity.Seat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    public ConcertResponse registerConcert(ConcertRequest request) {
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

    @Transactional
    public ConcertResponse updateConcert(Long concertId, ConcertUpdateRequest request) {
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

    @Transactional(readOnly = true)
    public Page<ConcertResponse> getAllConcerts(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        return concertRepository.findAll(pageable).map(concert ->
                new ConcertResponse(
                        concert.getId(),
                        concert.getConcertName(),
                        concert.getPrice(),
                        concert.getDescription(),
                        concert.getImage(),
                        concert.getDate(),
                        concert.getStartAt(),
                        concert.getSeats().size()
                )
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
