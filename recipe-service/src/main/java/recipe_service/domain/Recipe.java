package recipe_service.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.TextIndexed;
import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "recipes")   // ← colección de MongoDB
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

    @Id
    private String id;              // MongoDB usa String como ID

    @TextIndexed                    // permite búsqueda de texto
    private String nombre;

    private String categoria;       // "postres", "platos principales", etc.
    private String dificultad;      // "facil", "media", "dificil"
    private Integer tiempoMinutos;
    private Integer porciones;
    private String imagenUrl;

    private List<Ingredient> ingredientes;  // embebido — sin tabla separada
    private List<String> pasos;
    private List<String> tags;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}