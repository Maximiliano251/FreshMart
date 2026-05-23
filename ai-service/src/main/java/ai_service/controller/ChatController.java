package ai_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ai_service.dto.ChatRequest;
import ai_service.dto.ChatResponse;
import ai_service.service.ChatService;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(
        @RequestHeader("X-User-Id") String userId,
        @RequestHeader(value = "X-User-Name",
                       defaultValue = "Usuario") String userName,
        @RequestHeader(value = "X-User-Ciudad",
                       defaultValue = "Santiago") String ciudad,
        @RequestHeader(value = "X-User-Supermercado",
                       defaultValue = "simermart") String supermercadoFav,
        @RequestBody ChatRequest request) {

        return ResponseEntity.ok(
            chatService.chat(userId, userName,
                            ciudad, supermercadoFav, request)
        );
    }
}