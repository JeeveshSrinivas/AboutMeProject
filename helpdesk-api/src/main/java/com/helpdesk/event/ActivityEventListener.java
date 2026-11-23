package com.helpdesk.event;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.helpdesk.document.ActivityDocument;
import com.helpdesk.repository.mongo.ActivityRepository;

@Component
public class ActivityEventListener {

    private static final Logger logger = LoggerFactory.getLogger(ActivityEventListener.class);

    private final ActivityRepository activityRepository;

    public ActivityEventListener(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTicketCreated(TicketCreatedEvent event) {

        try {
            ActivityDocument activity = new ActivityDocument();

            activity.setTicketId(event.ticketId());
            activity.setEmployeeId(event.employeeId());
            activity.setEmployeeRole(event.employeeRole());
            activity.setEventOrigin("USER");
            activity.setAction("TICKET_CREATED");
            activity.setDetails("Ticket created: " + event.title());
            activity.setCreatedAt(LocalDateTime.now());

            activityRepository.save(activity);

        } catch (Exception exception) {
            logger.error(
                    "Failed to record creation activity for ticket ID: {}",
                    event.ticketId(),
                    exception);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTicketActivity(TicketActivityEvent event) {

        try {
            ActivityDocument activity = new ActivityDocument();

            activity.setTicketId(event.ticketId());
            activity.setEmployeeId(event.employeeId());
            activity.setEmployeeRole(event.employeeRole());
            activity.setEventOrigin(event.eventOrigin());
            activity.setAction(event.action());
            activity.setDetails(event.details());
            activity.setCreatedAt(LocalDateTime.now());

            activityRepository.save(activity);

        } catch (Exception exception) {
            logger.error(
                    "Failed to record activity for ticket ID: {}",
                    event.ticketId(),
                    exception);
        }
    }
}