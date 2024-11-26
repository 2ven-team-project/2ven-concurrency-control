package com.sparta.concurrencycontrolproject.domain.concert.controller;

import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertRequest;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertResponse;
import com.sparta.concurrencycontrolproject.domain.concert.service.ConcertService;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/concerts")
public class ConcertController {

    private final ConcertService concertService;

    public ConcertController(ConcertService concertService) {
        this.concertService = concertService;
    }

    @PostMapping
    public ResponseEntity<ConcertResponse> registerConcert(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody ConcertRequest request
    ) {
        // Admin 권한 확인
        if (!userDetails.isAdmin()) {
            throw new AccessDeniedException("해당 작업은 ADMIN 권한이 필요합니다.");
        }

        // 공연 등록 로직 실행
        ConcertResponse response = concertService.registerConcert(request);
        return ResponseEntity.ok(response);
    }
}