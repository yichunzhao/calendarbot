package com.ynz.ai.calendarbot.service;

import com.ynz.ai.calendarbot.service.dto.AppointmentRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {
    boolean isSlotAvailable(LocalDate date, LocalTime time);

    void bookAppointment(AppointmentRequest request);

    List<AppointmentRequest> listAppointmentsForUser(String clientName, String clientContact);

    boolean cancelAppointment(String clientName, String clientContact, java.time.LocalDate date, java.time.LocalTime time);
}
