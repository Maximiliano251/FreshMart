package user_service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_memories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMemory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, length = 1000)
    private String contenido;

    @Enumerated(EnumType.STRING)
    private MemoryOrigin origen;

    // relevancia del 1 al 5 — determina cuáles se inyectan al prompt
    @Column(nullable = false)
    private Integer relevancia;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt  = LocalDateTime.now();
        if (relevancia == null) relevancia = 3;
        if (origen     == null) origen     = MemoryOrigin.AUTO_EXTRACTED;
    }

    public enum MemoryOrigin {
        AUTO_EXTRACTED,   // extraída por la IA al terminar la conversación
        USER_STATED       // el usuario la declaró explícitamente en su perfil
    }
}