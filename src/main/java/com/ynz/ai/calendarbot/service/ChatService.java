package com.ynz.ai.calendarbot.service;

    import org.springframework.ai.chat.client.ChatClient;
    import org.springframework.ai.chat.model.ChatResponse;
    import org.springframework.stereotype.Service;

    import java.time.LocalDate;
    import java.time.LocalTime;

@Service
    public class ChatService {

        private final ChatClient chatClient;
        private final AppointmentService appointmentService;

        public ChatService(ChatClient chatClient, AppointmentService appointmentService) {
            this.chatClient = chatClient;
            this.appointmentService = appointmentService;
        }

        public String handleUserMessage(String message) {
            // Step 1: Ask LLM to extract intent
            ChatResponse response = chatClient.prompt()
                    .user(message)
                    .call()
                    .entity(ChatResponse.class);

            String llmOutput = response.getResult().getOutput().getText();

            // Step 2: Parse LLM output to structured JSON
            AppointmentRequest request = parseLLMOutput(llmOutput);

            // Step 3: Check availability & book appointment via clinic system
            boolean available = appointmentService.isSlotAvailable(request.getDate(), request.getTime());

            if (available) {
                appointmentService.bookAppointment(request);
                return "✅ Your appointment is scheduled for " + request.getDate() + " at " + request.getTime();
            } else {
                return "⚠️ That slot is not available. Please choose another time.";
            }
        }

        private AppointmentRequest parseLLMOutput(String llmOutput) {
            // Implement JSON parsing logic (e.g., Jackson ObjectMapper)
            // For now, assume it's a simple mock
            return new AppointmentRequest(
                    "John Doe",
                    "Dentist",
                    "service", LocalDate.parse("2025-09-18"), LocalTime.parse("15:00"));
        }
    }