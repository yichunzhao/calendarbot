package com.ynz.ai.calendarbot.controller;

import com.ynz.ai.calendarbot.service.ChatService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Profile("!cli")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<String> chat(@RequestBody String message,
                                       @RequestHeader(value = "X-Conversation-Id", required = false) String conversationId,
                                       HttpSession session) {
        String effectiveConversationId = StringUtils.hasText(conversationId)
                ? conversationId
                : session.getId();

        String reply = chatService.handleUserMessage(effectiveConversationId, message);
        return ResponseEntity.ok()
                .header("X-Conversation-Id", effectiveConversationId)
                .body(reply);
    }
}
