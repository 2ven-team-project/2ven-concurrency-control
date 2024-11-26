package com.sparta.concurrencycontrolproject.domain.concert.controller;

import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertRequest;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertResponse;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertUpdateRequest;
import com.sparta.concurrencycontrolproject.domain.concert.service.ConcertService;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> updateConcert(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long concertId,
            @RequestBody ConcertUpdateRequest request
    ) {
        // Admin 권한 확인
        if (!userDetails.isAdmin()) {
            throw new AccessDeniedException("해당 작업은 ADMIN 권한이 필요합니다.");
        }

        // 공연 수정 로직 실행
        ConcertResponse response = concertService.updateConcert(concertId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ConcertResponse>> getAllConcerts() {
        // 공연 전체 조회 로직 실행
        List<ConcertResponse> concerts = concertService.getAllConcerts();
        return ResponseEntity.ok(concerts);
    }
}
