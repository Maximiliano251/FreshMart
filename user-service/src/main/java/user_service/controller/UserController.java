package user_service.controller;


import lombok.RequiredArgsConstructor;
import user_service.domain.UserMemory;
import user_service.dto.UserResponse;
import user_service.repository.UserMemoryRepository;
import user_service.repository.UserRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository       userRepository;
    private final UserMemoryRepository memoryRepository;

    // el userId viene del header que pone el Gateway
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @RequestHeader("X-User-Id") String userId) {

        return userRepository.findById(userId)
            .map(user -> ResponseEntity.ok(UserResponse.builder()
                .id(user.getId())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .email(user.getEmail())
                .rol(user.getRol().name())
                .activo(user.getActivo())
                .build()))
            .orElse(ResponseEntity.notFound().build());
    }

    // ver memorias del usuario autenticado
    @GetMapping("/me/memories")
    public ResponseEntity<List<UserMemory>> getMemories(
            @RequestHeader("X-User-Id") String userId) {
        List<UserMemory> memories =
            memoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return ResponseEntity.ok(memories);
    }

    // guardar una memoria nueva (llamado por ai-service)
    @PostMapping("/me/memories")
    public ResponseEntity<UserMemory> saveMemory(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UserMemory memory) {
        memory.setUserId(userId);
        return ResponseEntity.ok(memoryRepository.save(memory));
    }
}