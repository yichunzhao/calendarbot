package com.ynz.ai.calendarbot.controller;

import com.ynz.ai.calendarbot.service.ChatService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatControllerTest {

    @Test
    void chatUsesSessionIdWhenHeaderIsMissing() {
        ChatService chatService = mock(ChatService.class);
        HttpSession session = mock(HttpSession.class);
        ChatController controller = new ChatController(chatService);

        when(session.getId()).thenReturn("session-123");
        when(chatService.handleUserMessage("session-123", "list my appointments"))
                .thenReturn("No appointments found.");

        ResponseEntity<String> response = controller.chat("list my appointments", null, session);

        assertEquals("No appointments found.", response.getBody());
        assertEquals("session-123", response.getHeaders().getFirst("X-Conversation-Id"));
        verify(chatService).handleUserMessage("session-123", "list my appointments");
    }

    @Test
    void chatPrefersExplicitConversationHeader() {
        ChatService chatService = mock(ChatService.class);
        HttpSession session = mock(HttpSession.class);
        ChatController controller = new ChatController(chatService);

        when(session.getId()).thenReturn("session-123");
        when(chatService.handleUserMessage("conversation-abc", "I am Yichun"))
                .thenReturn("Thanks, Yichun. I'll remember you for this conversation.");

        ResponseEntity<String> response = controller.chat("I am Yichun", "conversation-abc", session);

        assertEquals("conversation-abc", response.getHeaders().getFirst("X-Conversation-Id"));
        verify(chatService).handleUserMessage("conversation-abc", "I am Yichun");
    }
}

