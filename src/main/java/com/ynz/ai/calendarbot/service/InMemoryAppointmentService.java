package com.ynz.ai.calendarbot.service;


import com.ynz.ai.calendarbot.service.dto.AppointmentRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class InMemoryAppointmentService implements AppointmentService {
    private final Map<LocalDate, Set<LocalTime>> appointments = new HashMap<>();

    @Override
    public boolean isSlotAvailable(LocalDate date, LocalTime time) {
        return !appointments.getOrDefault(date, Collections.emptySet()).contains(time);
    }

    @Override
    public void bookAppointment(AppointmentRequest request) {
        appointments
            .computeIfAbsent(request.getDate(), d -> new HashSet<>())
            .add(request.getTime());
    }
}