package user_service.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // referencia al user por ID — sin FK cruzada entre servicios
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    private String ciudad;

    // lista de strings guardada como JSON en MySQL
    @ElementCollection
    @CollectionTable(
        name = "user_intolerancias",
        joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "intolerancia")
    private List<String> intolerancias;

    @ElementCollection
    @CollectionTable(
        name = "user_preferencias",
        joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "preferencia")
    private List<String> prefCocina;

    private String presupuesto;       // "bajo", "medio", "alto"

    @Column(name = "supermercado_fav")
    private String supermercadoFav;   // "simermart", "frescopro", "megacanasta"

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}