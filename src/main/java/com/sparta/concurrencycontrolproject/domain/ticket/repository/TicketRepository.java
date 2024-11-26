package com.sparta.concurrencycontrolproject.domain.ticket.repository;

import com.sparta.concurrencycontrolproject.domain.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long>, TicketRepositoryCustom {
}
