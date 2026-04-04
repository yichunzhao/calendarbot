package com.ynz.ai.calendarbot.shell;

import com.ynz.ai.calendarbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@Profile("cli")
@RequiredArgsConstructor
public class ChatShellCommands {

    private final ChatService chatService;

    @ShellMethod(key = "chat", value = "Send a natural language request to CalendarBot")
    public String chat(@ShellOption(help = "Message in quotes") String message) {
        return chatService.handleUserMessage(message);
    }
}

