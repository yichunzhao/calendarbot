package com.ynz.ai.calendarbot.service;


import com.ynz.ai.calendarbot.service.dto.AppointmentRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class InMemoryAppointmentService implements AppointmentService {

    private final List<AppointmentRequest> appointments = new ArrayList<>();

    @Override
    public boolean isSlotAvailable(LocalDate date, LocalTime time) {
        return appointments.stream()
                .noneMatch(a -> date.equals(a.getDate()) && time.equals(a.getTime()));
    }

    @Override
    public void bookAppointment(AppointmentRequest request) {
        appointments.add(request);
    }

    @Override
    public List<AppointmentRequest> listAppointmentsForUser(String clientName, String clientContact) {
        return appointments.stream()
                .filter(a -> matchesIdentity(a, clientName, clientContact))
                .sorted(Comparator.comparing(AppointmentRequest::getDate)
                        .thenComparing(AppointmentRequest::getTime))
                .toList();
    }

    @Override
    public boolean cancelAppointment(String clientName, String clientContact,
                                     LocalDate date, LocalTime time) {
        return appointments.removeIf(a ->
                matchesIdentity(a, clientName, clientContact)
                        && date.equals(a.getDate())
                        && time.equals(a.getTime()));
    }

    private boolean matchesIdentity(AppointmentRequest appointment, String clientName, String clientContact) {
        boolean hasContact = !isBlank(clientContact);
        if (hasContact) {
            return clientContact.equalsIgnoreCase(appointment.getClientContact());
        }

        if (!isBlank(clientName)) {
            return clientName.equalsIgnoreCase(appointment.getClientName());
        }

        return false;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}