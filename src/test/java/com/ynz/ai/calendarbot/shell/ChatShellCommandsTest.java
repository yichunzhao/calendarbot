package com.ynz.ai.calendarbot.shell;

import com.ynz.ai.calendarbot.service.ChatService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CliRunnerTest {

    @Test
    void naturalInputDelegatesToChatServiceWithOneConversationIdThenExits() {
        ChatService chatService = mock(ChatService.class);
        String input = "I am Yichun\nBook cleaning for Alice on 2026-04-10 at 10:00\nexit\n";
        String expected = "✅ Your appointment is scheduled for 2026-04-10 at 10:00";

        when(chatService.handleUserMessage(anyString(), anyString()))
                .thenReturn(expected);

        System.setIn(new ByteArrayInputStream(input.getBytes()));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        CliRunner runner = new CliRunner(chatService);
        runner.run(null);

        System.setIn(System.in);
        System.setOut(System.out);

        String output = out.toString();
        assertTrue(output.contains(expected), "Expected LLM response in output");

        ArgumentCaptor<String> conversationIdCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(chatService, org.mockito.Mockito.times(2))
                .handleUserMessage(conversationIdCaptor.capture(), messageCaptor.capture());

        List<String> conversationIds = conversationIdCaptor.getAllValues();
        List<String> messages = messageCaptor.getAllValues();

        assertEquals(2, conversationIds.size());
        assertEquals(conversationIds.get(0), conversationIds.get(1), "Expected one conversation id for the CLI session");
        assertEquals(List.of("I am Yichun", "Book cleaning for Alice on 2026-04-10 at 10:00"), messages);
    }
}



