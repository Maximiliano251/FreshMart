package ai_service.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import ai_service.domain.Conversation;
import java.util.List;

public interface ConversationRepository
        extends JpaRepository<Conversation, String> {

    List<Conversation> findTop10ByUserIdOrderByCreatedAtDesc(String userId);
}