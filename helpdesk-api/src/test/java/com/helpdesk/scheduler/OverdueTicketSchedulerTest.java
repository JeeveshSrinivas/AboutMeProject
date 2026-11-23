
package com.helpdesk.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.helpdesk.document.ActivityDocument;
import com.helpdesk.entity.Ticket;
import com.helpdesk.repository.jpa.TicketRepository;
import com.helpdesk.repository.mongo.ActivityRepository;

import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
class OverdueTicketSchedulerTest {

        @Mock
        private TicketRepository ticketRepository;

        @Mock
        private ActivityRepository activityRepository;

        @InjectMocks
        private OverdueTicketScheduler scheduler;

        @Test
        void shouldSaveWarningForOverdueTicket() {

                // Arrange: create a sample overdue ticket
                Ticket ticket = new Ticket();
                ticket.setId(101L);
                ticket.setDueAt(LocalDateTime.now().minusHours(2));

                when(ticketRepository.findByStatusInAndDueAtBefore(
                                anyList(), any(LocalDateTime.class)))
                                .thenReturn(List.of(ticket));

                when(activityRepository.existsByTicketIdAndAction(101L, "WARNING"))
                                .thenReturn(false);

                // Act: execute the scheduler
                scheduler.checkOverdueTickets();

                // Assert: capture and inspect the saved activity
                ArgumentCaptor<ActivityDocument> captor = ArgumentCaptor.forClass(ActivityDocument.class);

                verify(activityRepository).save(captor.capture());

                ActivityDocument savedActivity = captor.getValue();

                assertEquals(101L, savedActivity.getTicketId());
                assertEquals("WARNING", savedActivity.getAction());
                assertNull(savedActivity.getEmployeeId());
                assertNull(savedActivity.getEmployeeRole());
                assertEquals("SYSTEM", savedActivity.getEventOrigin());
                assertNotNull(savedActivity.getCreatedAt());
                assertTrue(savedActivity.getDetails().contains("passed its due time"));
        }

        @Test
        void shouldSaveWarningsForMultipleOverdueTickets() {

                // Arrange: create two overdue tickets
                Ticket firstTicket = new Ticket();
                firstTicket.setId(105L);
                firstTicket.setDueAt(LocalDateTime.now().minusHours(2));

                Ticket secondTicket = new Ticket();
                secondTicket.setId(106L);
                secondTicket.setDueAt(LocalDateTime.now().minusHours(3));

                when(ticketRepository.findByStatusInAndDueAtBefore(
                                anyList(), any(LocalDateTime.class)))
                                .thenReturn(List.of(firstTicket, secondTicket));

                when(activityRepository.existsByTicketIdAndAction(any(Long.class), eq("WARNING")))
                                .thenReturn(false);

                // Act: execute the scheduler
                scheduler.checkOverdueTickets();

                // Assert: verify that two warning activities were saved
                ArgumentCaptor<ActivityDocument> captor = ArgumentCaptor.forClass(ActivityDocument.class);

                verify(activityRepository, times(2)).save(captor.capture());

                List<ActivityDocument> savedActivities = captor.getAllValues();

                assertEquals(2, savedActivities.size());
                assertEquals(105L, savedActivities.get(0).getTicketId());
                assertEquals(106L, savedActivities.get(1).getTicketId());

                assertEquals("WARNING", savedActivities.get(0).getAction());
                assertEquals("WARNING", savedActivities.get(1).getAction());

                assertEquals("SYSTEM", savedActivities.get(0).getEventOrigin());
                assertEquals("SYSTEM", savedActivities.get(1).getEventOrigin());

                assertNull(savedActivities.get(0).getEmployeeId());
                assertNull(savedActivities.get(1).getEmployeeId());

                assertNull(savedActivities.get(0).getEmployeeRole());
                assertNull(savedActivities.get(1).getEmployeeRole());
        }

        @Test
        void shouldNotSaveDuplicateWarning() {

                // Arrange
                Ticket ticket = new Ticket();
                ticket.setId(102L);
                ticket.setDueAt(LocalDateTime.now().minusHours(1));

                when(ticketRepository.findByStatusInAndDueAtBefore(
                                anyList(), any(LocalDateTime.class)))
                                .thenReturn(List.of(ticket));

                when(activityRepository.existsByTicketIdAndAction(102L, "WARNING"))
                                .thenReturn(true);

                // Act
                scheduler.checkOverdueTickets();

                // Assert
                verify(activityRepository, never()).save(any(ActivityDocument.class));
        }

        @Test
        void shouldHandleMongoFailureWithoutCrashing() {

                // Arrange
                Ticket ticket = new Ticket();
                ticket.setId(103L);
                ticket.setDueAt(LocalDateTime.now().minusHours(1));

                when(ticketRepository.findByStatusInAndDueAtBefore(
                                anyList(), any(LocalDateTime.class)))
                                .thenReturn(List.of(ticket));

                when(activityRepository.existsByTicketIdAndAction(103L, "WARNING"))
                                .thenReturn(false);

                when(activityRepository.save(any(ActivityDocument.class)))
                                .thenThrow(new RuntimeException("MongoDB unavailable"));

                // Act and Assert: scheduler should not throw an exception
                // First execution: MongoDB save fails
                assertDoesNotThrow(() -> scheduler.checkOverdueTickets());

                // Second execution: MongoDB save succeeds
                when(activityRepository.save(any(ActivityDocument.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                assertDoesNotThrow(() -> scheduler.checkOverdueTickets());

                verify(activityRepository, times(2))
                                .save(any(ActivityDocument.class));
        }

        @Test
        void shouldHandleDuplicateKeyExceptionWithoutCrashing() {

                // Arrange
                Ticket ticket = new Ticket();
                ticket.setId(104L);
                ticket.setDueAt(LocalDateTime.now().minusHours(1));

                when(ticketRepository.findByStatusInAndDueAtBefore(
                                anyList(), any(LocalDateTime.class)))
                                .thenReturn(List.of(ticket));

                when(activityRepository.existsByTicketIdAndAction(104L, "WARNING"))
                                .thenReturn(false);

                when(activityRepository.save(any(ActivityDocument.class)))
                                .thenThrow(new DuplicateKeyException("Duplicate warning"));

                // Act and Assert
                assertDoesNotThrow(() -> scheduler.checkOverdueTickets());

                verify(activityRepository).save(any(ActivityDocument.class));
        }

}
