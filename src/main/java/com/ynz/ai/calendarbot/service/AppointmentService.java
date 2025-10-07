package com.ynz.ai.calendarbot.service;

import com.ynz.ai.calendarbot.service.dto.AppointmentRequest;

import java.time.LocalDate;
import java.time.LocalTime;

public interface AppointmentService {
    boolean isSlotAvailable(LocalDate date, LocalTime time);
    void bookAppointment(AppointmentRequest request);
}
