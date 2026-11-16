package com.helpdesk.repository.jpa;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.helpdesk.entity.Ticket;
import com.helpdesk.enums.TicketStatus;

public interface TicketRepository extends
        JpaRepository<Ticket, Long>,
        JpaSpecificationExecutor<Ticket>  {

    List<Ticket> findByStatusInAndDueAtBefore(
            Collection<TicketStatus> statuses,
            LocalDateTime currentTime
    );
}