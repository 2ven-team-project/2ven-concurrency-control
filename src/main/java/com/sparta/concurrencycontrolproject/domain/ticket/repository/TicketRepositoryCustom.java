package com.sparta.concurrencycontrolproject.domain.ticket.repository;

import com.sparta.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TicketRepositoryCustom {
    Page<Ticket> findByMemberId(UserDetailsImpl authMember, Pageable pageable);
}
