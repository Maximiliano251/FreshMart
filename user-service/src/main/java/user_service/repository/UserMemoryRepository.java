package user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import user_service.domain.UserMemory;
import java.util.List;

public interface UserMemoryRepository extends JpaRepository<UserMemory, String> {
    // top 10 memorias más relevantes del usuario — para inyectar al prompt
    List<UserMemory> findTop10ByUserIdOrderByRelevanciaDesc(String userId);
    List<UserMemory> findByUserIdOrderByCreatedAtDesc(String userId);
}