package com.ynz.ai.calendarbot.shell;

import com.ynz.ai.calendarbot.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Scanner;
import java.util.UUID;

@Component
@Profile("cli")
@RequiredArgsConstructor
public class CliRunner implements ApplicationRunner {

    private final ChatService chatService;

    @Override
    public void run(ApplicationArguments args) {
        printBanner();
        Scanner scanner = new Scanner(System.in);
        String conversationId = UUID.randomUUID().toString();

        while (true) {
            System.out.print("calendarbot:> ");
            System.out.flush();

            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();

            if (input.isEmpty()) continue;

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye! 👋");
                break;
            }

            System.out.println("⏳ Thinking...");
            try {
                String response = chatService.handleUserMessage(conversationId, input);
                System.out.println(response);
            } catch (Exception e) {
                System.out.println("⚠️  Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private void printBanner() {
        System.out.println("""
                ╔══════════════════════════════════════════╗
                ║      🦷  CalendarBot  CLI  v1.0          ║
                ║  Type naturally to book appointments.    ║
                ║  You can also say: list my appointments. ║
                ║  Type 'exit' to quit.                    ║
                ╚══════════════════════════════════════════╝
                """);
    }
}
