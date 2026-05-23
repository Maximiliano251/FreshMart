package ai_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class OpenRouterResponse {

    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }

    public String getText() {
        try {
            return choices.get(0).getMessage().getContent();
        } catch (Exception e) {
            return "";
        }
    }
}