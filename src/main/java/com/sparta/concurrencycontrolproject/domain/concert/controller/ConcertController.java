package com.sparta.concurrencycontrolproject.domain.concert.controller;

import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertRequest;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertResponse;
import com.sparta.concurrencycontrolproject.domain.concert.dto.ConcertUpdateRequest;
import com.sparta.concurrencycontrolproject.domain.concert.service.ConcertService;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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
        // 권한 확인은 서비스에서 처리
        ConcertResponse response = concertService.registerConcert(userDetails, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> updateConcert(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long concertId,
            @RequestBody ConcertUpdateRequest request
    ) {
        // 권한 확인은 서비스에서 처리
        ConcertResponse response = concertService.updateConcert(userDetails, concertId, request);
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
