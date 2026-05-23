package user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import user_service.domain.UserProfile;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    Optional<UserProfile> findByUserId(String userId);
}