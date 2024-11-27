package com.sparta.concurrencycontrolproject.domain.concert.controller;

import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertRequest;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertResponse;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertUpdateRequest;
import com.sparta.concurrencycontrolproject.domain.concert.service.ConcertService;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
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

        ConcertResponse response = concertService.registerConcert(request);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> updateConcert(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long concertId,
            @RequestBody ConcertUpdateRequest request
    ) {
        if (!userDetails.isAdmin()) {
            throw new AccessDeniedException("해당 작업은 ADMIN 권한이 필요합니다.");
        }
        ConcertResponse response = concertService.updateConcert(concertId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ConcertResponse>> getConcerts(
            @RequestParam(value = "name", required = false) String name, // 검색 조건
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ConcertResponse> concertResponses = concertService.getConcerts(name, page, size);
        return ResponseEntity.ok(concertResponses);
    }
}
