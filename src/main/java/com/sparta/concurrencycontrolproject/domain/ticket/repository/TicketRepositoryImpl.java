package com.sparta.concurrencycontrolproject.domain.ticket.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.concurrencycontrolproject.domain.ticket.entity.QTicket;
import com.sparta.concurrencycontrolproject.domain.ticket.entity.Ticket;
import com.sparta.concurrencycontrolproject.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Ticket> findByMemberId(UserDetailsImpl authMember, Pageable pageable) {
        QTicket ticket = QTicket.ticket;

        // 티켓 목록 조회
        List<Ticket> content = queryFactory
                .selectFrom(ticket)
                .where(ticket.member.id.eq(authMember.getUser().getId()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 전체 카운트 조회
        long total = queryFactory
                .selectFrom(ticket)
                .where(ticket.member.id.eq(authMember.getUser().getId()))
                .fetchCount();

        return new PageImpl<>(content, pageable, total);
    }

}
