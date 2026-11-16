package com.helpdesk.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.helpdesk.document.ActivityDocument;
import com.helpdesk.entity.Ticket;
import com.helpdesk.enums.TicketStatus;
import com.helpdesk.repository.jpa.TicketRepository;
import com.helpdesk.repository.mongo.ActivityRepository;

import org.springframework.dao.DuplicateKeyException;

@Component
public class OverdueTicketScheduler {

    private static final Logger logger = LoggerFactory.getLogger(OverdueTicketScheduler.class);

    private final TicketRepository ticketRepository;
    private final ActivityRepository activityRepository;

    public OverdueTicketScheduler(
            TicketRepository ticketRepository,
            ActivityRepository activityRepository) {

        this.ticketRepository = ticketRepository;
        this.activityRepository = activityRepository;
    }

    @Scheduled(fixedDelayString = "${app.scheduler.overdue-ticket.fixed-delay:60000}")
    public void checkOverdueTickets() {

        LocalDateTime currentTime = LocalDateTime.now();

        try {
            List<Ticket> overdueTickets = ticketRepository.findByStatusInAndDueAtBefore(
                    List.of(
                            TicketStatus.OPEN,
                            TicketStatus.IN_PROGRESS),
                    currentTime);

            for (Ticket ticket : overdueTickets) {
                try {
                    boolean warningExists = activityRepository.existsByTicketIdAndAction(
                            ticket.getId(),
                            "WARNING");

                    if (warningExists) {
                        continue;
                    }

                    ActivityDocument warning = new ActivityDocument();

                    warning.setTicketId(ticket.getId());
                    warning.setActorId(null);
                    warning.setAction("WARNING");
                    warning.setDetails(
                            "Ticket has passed its due time: "
                                    + ticket.getDueAt());
                    warning.setCreatedAt(LocalDateTime.now());

                    activityRepository.save(warning);

                    logger.info(
                            "Overdue warning recorded for ticket ID: {}",
                            ticket.getId());

                } catch (DuplicateKeyException exception) {

                    logger.debug(
                            "Warning already recorded for ticket ID: {}",
                            ticket.getId());

                } catch (Exception exception) {

                    logger.error(
                            "Failed to record overdue warning for ticket ID: {}",
                            ticket.getId(),
                            exception);
                }
            }

        } catch (Exception exception) {
            logger.error(
                    "Failed to retrieve overdue tickets",
                    exception);
        }
    }
}