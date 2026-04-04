package com.ynz.ai.calendarbot.shell;

import com.ynz.ai.calendarbot.service.ChatService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatShellCommandsTest {

    @Test
    void chatDelegatesToChatService() {
        ChatService chatService = mock(ChatService.class);
        ChatShellCommands commands = new ChatShellCommands(chatService);

        String input = "Book cleaning for Alice on 2026-04-10 at 10:00";
        String expected = "Your appointment is scheduled";
        when(chatService.handleUserMessage(input)).thenReturn(expected);

        String actual = commands.chat(input);

        assertEquals(expected, actual);
        verify(chatService).handleUserMessage(input);
    }
}

