package user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String tipo;       // "Bearer"
    private String userId;
    private String nombre;
    private String email;
    private String rol;
    private long   expira;     // timestamp de expiración
}