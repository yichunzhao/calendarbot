package com.ynz.ai.calendarbot.shell;

import com.ynz.ai.calendarbot.service.ChatService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CliRunnerTest {

    @Test
    void naturalInputDelegatesToChatServiceThenExits() throws Exception {
        ChatService chatService = mock(ChatService.class);
        String input = "Book cleaning for Alice on 2026-04-10 at 10:00\nexit\n";
        String expected = "✅ Your appointment is scheduled for 2026-04-10 at 10:00";

        when(chatService.handleUserMessage("Book cleaning for Alice on 2026-04-10 at 10:00"))
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
        verify(chatService).handleUserMessage("Book cleaning for Alice on 2026-04-10 at 10:00");
    }
}



