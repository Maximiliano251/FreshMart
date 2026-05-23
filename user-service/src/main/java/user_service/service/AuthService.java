package user_service.service;


import lombok.RequiredArgsConstructor;
import user_service.domain.User;
import user_service.domain.UserProfile;
import user_service.dto.LoginRequest;
import user_service.dto.LoginResponse;
import user_service.dto.RegisterRequest;
import user_service.dto.UserResponse;
import user_service.repository.UserProfileRepository;
import user_service.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository      userRepository;
    private final UserProfileRepository profileRepository;
    private final PasswordEncoder     passwordEncoder;
    private final JwtService          jwtService;

    @Transactional
    public UserResponse register(RegisterRequest request) {

        // verifica que el email no exista
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // crea el usuario
        User user = User.builder()
            .nombre(request.getNombre())
            .apellido(request.getApellido())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();

        user = userRepository.save(user);

        // crea el perfil asociado
        UserProfile profile = UserProfile.builder()
            .userId(user.getId())
            .ciudad(request.getCiudad())
            .presupuesto(request.getPresupuesto())
            .supermercadoFav(request.getSupermercadoFav())
            .build();

        profileRepository.save(profile);

        return toResponse(user);
    }

    public LoginResponse login(LoginRequest request) {

        // busca el usuario por email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        // verifica que esté activo
        if (!user.getActivo()) {
            throw new RuntimeException("Cuenta desactivada");
        }

        // verifica la contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales incorrectas");
        }

        // genera el JWT
        String token = jwtService.generateToken(
            user.getId(),
            user.getEmail(),
            user.getRol().name()
        );
        
        return LoginResponse.builder()
            .token(token)
            .tipo("Bearer")
            .userId(user.getId())
            .nombre(user.getNombre())
            .email(user.getEmail())
            .rol(user.getRol().name())
            .expira(System.currentTimeMillis() + jwtService.getExpiration())
            .build();
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .nombre(user.getNombre())
            .apellido(user.getApellido())
            .email(user.getEmail())
            .rol(user.getRol().name())
            .activo(user.getActivo())
            .build();
    }
}