package com.ynz.ai.calendarbot.service;

import com.ynz.ai.calendarbot.service.dto.AppointmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final AppointmentService appointmentService;

    public String handleUserMessage(String message) {
        // Step 1: Ask LLM to extract intent
        String today = LocalDate.now().toString(); // e.g. "2025-10-12"

        String systemPrompt = String.format("""
                You are an intelligent scheduling assistant.
                Today is %s.
                When parsing relative dates like "next Monday", interpret them based on this date.
                Extract appointment details in JSON format:
                {
                  "clientName": "",
                  "clientContact": "",
                  "service": "",
                  "date": "YYYY-MM-DD",
                  "time": "HH:MM"
                }
                """, today);

        AppointmentRequest appointmentRequest = chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .call()
                .entity(AppointmentRequest.class);             // RETURNS AppointmentRequest

        // Step 2: Check availability & book appointment via clinic system
        boolean available = appointmentService.isSlotAvailable(appointmentRequest.getDate(), appointmentRequest.getTime());

        if (available) {
            appointmentService.bookAppointment(appointmentRequest);
            return "✅ Your appointment is scheduled for " + appointmentRequest.getDate() + " at " + appointmentRequest.getTime();
        } else {
            return "⚠️ That slot is not available. Please choose another time.";
        }
    }
}