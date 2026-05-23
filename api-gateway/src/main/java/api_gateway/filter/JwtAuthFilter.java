package api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // rutas públicas que NO requieren JWT
    private static final List<String> PUBLIC_ROUTES = List.of(
        "/api/auth/register",   // registro y login son públicos
        "/api/auth/login",     // registro y login son públicos
        "/api/products",       // ver catálogo es público
        "/api/recipes",         // ver recetas es público
        "/api/locations"        // ver ubicaciones de tiendas es público
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();

        // si la ruta es pública, deja pasar sin validar
        if (isPublicRoute(path)) {
            return chain.filter(exchange);
        }

        // busca el header Authorization
        String authHeader = exchange.getRequest()
            .getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7); // quita "Bearer "

        try {
            // valida el token y extrae los claims
            Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(
                    jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

            // agrega los datos del usuario como headers
            // los microservicios los leen desde aquí
            ServerWebExchange modifiedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                    .header("X-User-Id",    claims.getSubject())
                    .header("X-User-Email", claims.get("email", String.class))
                    .header("X-User-Role",  claims.get("rol",   String.class))
                    .header("X-User-Name",     claims.get("nombre", String.class) != null
                                   ? claims.get("nombre", String.class)
                                   : "Usuario")
                    .build())
                .build();

            return chain.filter(modifiedExchange);

        } catch (Exception e) {
            // token inválido o expirado
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private boolean isPublicRoute(String path) {
        return PUBLIC_ROUTES.stream().anyMatch(path::startsWith);
    }

    @Override
    public int getOrder() {
        return -1; // se ejecuta antes que cualquier otro filtro
    }
}