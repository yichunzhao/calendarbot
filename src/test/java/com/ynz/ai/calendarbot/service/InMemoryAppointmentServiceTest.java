package com.ynz.ai.calendarbot.service;

import com.ynz.ai.calendarbot.service.dto.AppointmentRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryAppointmentServiceTest {

    @Test
    void listAppointmentsForUserReturnsOnlyMatchingUserSortedByDateAndTime() {
        InMemoryAppointmentService service = new InMemoryAppointmentService();

        service.bookAppointment(new AppointmentRequest("BOOK", "Alice", "alice@mail.com", "Cleaning",
                LocalDate.of(2026, 4, 10), LocalTime.of(10, 0)));
        service.bookAppointment(new AppointmentRequest("BOOK", "Alice", "alice@mail.com", "Checkup",
                LocalDate.of(2026, 4, 9), LocalTime.of(9, 30)));
        service.bookAppointment(new AppointmentRequest("BOOK", "Bob", "bob@mail.com", "Whitening",
                LocalDate.of(2026, 4, 8), LocalTime.of(11, 0)));

        List<AppointmentRequest> result = service.listAppointmentsForUser("Alice", "alice@mail.com");

        assertEquals(2, result.size());
        assertEquals("Checkup", result.get(0).getService());
        assertEquals("Cleaning", result.get(1).getService());
    }

    @Test
    void isSlotAvailableBecomesFalseAfterBooking() {
        InMemoryAppointmentService service = new InMemoryAppointmentService();
        LocalDate date = LocalDate.of(2026, 4, 10);
        LocalTime time = LocalTime.of(10, 0);

        assertTrue(service.isSlotAvailable(date, time));

        service.bookAppointment(new AppointmentRequest("BOOK", "Alice", "alice@mail.com", "Cleaning", date, time));

        assertFalse(service.isSlotAvailable(date, time));
    }
}

