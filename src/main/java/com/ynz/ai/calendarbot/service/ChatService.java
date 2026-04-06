package com.ynz.ai.calendarbot.service;

import com.ynz.ai.calendarbot.service.dto.AppointmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatClient chatClient;
    private final AppointmentService appointmentService;

    public String handleUserMessage(String conversationId, String message) {
        String today = LocalDate.now().toString();
        String effectiveConversationId = isBlank(conversationId)
                ? ChatMemory.DEFAULT_CONVERSATION_ID
                : conversationId;

        String systemPrompt = String.format("""
                You are an intelligent scheduling assistant.
                Today is %s.
                When parsing relative dates like "next Monday", interpret them based on this date.

                Return JSON with this schema:
                {
                  "intent": "BOOK" | "LIST" | "DELETE" | "IDENTIFY",
                  "clientName": "",
                  "clientContact": "",
                  "service": "",
                  "date": "YYYY-MM-DD",
                  "time": "HH:MM"
                }

                Rules:
                - Use the conversation history to fill in clientName or clientContact if the current message omits them.
                - Use intent LIST for requests like "show/list my appointments".
                - Use intent BOOK for scheduling/booking requests.
                - Use intent DELETE for requests like "cancel/delete/remove my appointment".
                - Use intent IDENTIFY when the user is only introducing or correcting their name/contact.
                - For LIST, include clientName/clientContact when available from the user message.
                - For BOOK, fill all fields.
                - For DELETE, include clientName/clientContact, date, and time.
                - For IDENTIFY, include any provided clientName/clientContact and leave the other fields empty.
                """, today);

        AppointmentRequest request = chatClient.prompt()
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID, effectiveConversationId))
                .system(systemPrompt)
                .user(message)
                .call()
                .entity(AppointmentRequest.class);

        if (request == null) {
            return "I could not understand your request. Please try again.";
        }

        String intent = normalizeIntent(request.getIntent());
        return switch (intent) {
            case "LIST" -> handleListIntent(request);
            case "DELETE" -> handleDeleteIntent(request);
            case "IDENTIFY" -> handleIdentifyIntent(request);
            default -> handleBookIntent(request);
        };

    }

    private String handleIdentifyIntent(AppointmentRequest request) {
        if (isBlank(request.getClientName()) && isBlank(request.getClientContact())) {
            return "Thanks. I'll remember your details for this conversation.";
        }

        if (!isBlank(request.getClientName()) && !isBlank(request.getClientContact())) {
            return "Thanks, " + request.getClientName() + ". I'll remember your contact as "
                    + request.getClientContact() + " for this conversation.";
        }
        if (!isBlank(request.getClientName())) {
            return "Thanks, " + request.getClientName() + ". I'll remember you for this conversation.";
        }
        return "Thanks. I'll remember your contact as " + request.getClientContact() + " for this conversation.";
    }

    private String handleBookIntent(AppointmentRequest request) {
        if (request.getDate() == null || request.getTime() == null) {
            return "Please provide date and time for the appointment.";
        }

        boolean available = appointmentService.isSlotAvailable(
                Objects.requireNonNull(request.getDate()),
                request.getTime());

        if (!available) {
            return "That slot is not available. Please choose another time.";
        }

        request.setIntent("BOOK");
        appointmentService.bookAppointment(request);
        return "Your appointment is scheduled for " + request.getDate() + " at " + request.getTime();
    }

    private String handleListIntent(AppointmentRequest request) {
        if (isBlank(request.getClientName()) && isBlank(request.getClientContact())) {
            return "Please include your name or contact so I can find your appointments.";
        }

        List<AppointmentRequest> appointments = appointmentService
                .listAppointmentsForUser(request.getClientName(), request.getClientContact());

        if (appointments.isEmpty()) {
            return "No appointments found for " + identityLabel(request) + ".";
        }

        StringBuilder response = new StringBuilder("Appointments for " + identityLabel(request) + ":\n");
        for (int i = 0; i < appointments.size(); i++) {
            AppointmentRequest appointment = appointments.get(i);
            response.append(i + 1)
                    .append(") ")
                    .append(appointment.getDate())
                    .append(" ")
                    .append(appointment.getTime())
                    .append(" - ")
                    .append(appointment.getService())
                    .append("\n");
        }

        return response.toString().trim();
    }

    private String handleDeleteIntent(AppointmentRequest request) {
        if (isBlank(request.getClientName()) && isBlank(request.getClientContact())) {
            return "Please include your name or contact to cancel an appointment.";
        }
        if (request.getDate() == null || request.getTime() == null) {
            return "Please provide the date and time of the appointment to cancel.";
        }

        boolean removed = appointmentService.cancelAppointment(
                request.getClientName(), request.getClientContact(),
                request.getDate(), request.getTime());

        if (removed) {
            return "Your appointment on " + request.getDate() + " at " + request.getTime() + " has been cancelled.";
        }
        return "No matching appointment found for " + identityLabel(request)
                + " on " + request.getDate() + " at " + request.getTime() + ".";
    }

    private String normalizeIntent(String intent) {
        if (isBlank(intent)) {
            return "BOOK";
        }
        return intent.trim().toUpperCase(Locale.ROOT);
    }

    private String identityLabel(AppointmentRequest request) {
        if (!isBlank(request.getClientName())) {
            return request.getClientName();
        }
        return request.getClientContact();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}