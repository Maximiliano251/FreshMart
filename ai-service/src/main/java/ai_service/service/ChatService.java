package ai_service.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ai_service.client.OpenRouterClient;
import ai_service.client.UserServiceClient;
import ai_service.domain.Conversation;
import ai_service.dto.ChatRequest;
import ai_service.dto.ChatResponse;
import ai_service.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final OpenRouterClient       openRouterClient;
    private final UserServiceClient      userServiceClient;
    private final PromptService          promptService;
    private final ConversationRepository conversationRepository;
    private final ObjectMapper           objectMapper = new ObjectMapper();

    public ChatResponse chat(String userId, String userName,
                             String ciudad, String supermercadoFav,
                             ChatRequest request) {

        // 1. historial de conversaciones
        List<Conversation> historial =
            conversationRepository
                .findTop10ByUserIdOrderByCreatedAtDesc(userId);

        // 2. memorias del usuario desde user-service via Feign
        List<Object> memorias = List.of();
        try {
            memorias = userServiceClient.getMemories(userId);
        } catch (Exception e) {
            log.warn("No se pudieron cargar memorias del usuario: {}",
                     e.getMessage());
        }

        // 3. construye el prompt personalizado
        String systemPrompt = promptService.buildSystemPrompt(
            userName, ciudad, supermercadoFav,
            promptService.buildHistorial(historial),
            promptService.buildMemorias(memorias)
        );

        // 4. llama a OpenRouter
        String rawResponse = openRouterClient.sendMessage(
            systemPrompt, request.getMensaje()
        );

        // 5. limpia posibles bloques markdown
        String cleanResponse = cleanJson(rawResponse);

        // 6. parsea el JSON de respuesta
        String intentType     = "chat";
        String respuestaTexto = cleanResponse;
        Object datos          = null;

        try {
            JsonNode json = objectMapper.readTree(cleanResponse);
            intentType    = json.get("tipo").asText("chat");

            if ("chat".equals(intentType)) {
                respuestaTexto = json.get("respuesta").asText();
            } else if ("recipe".equals(intentType)) {
                respuestaTexto = json.get("receta").asText();
                datos          = json;
            } else if ("search".equals(intentType)) {
                respuestaTexto = json.get("producto").asText();
                datos          = json;
            }

        } catch (Exception e) {
            log.warn("No se pudo parsear JSON: {}", cleanResponse);
            respuestaTexto = cleanResponse;
        }

        // 7. guarda la conversación en MySQL
        Conversation conv = Conversation.builder()
            .userId(userId)
            .userMessage(request.getMensaje())
            .aiResponse(respuestaTexto)
            .intentType(intentType)
            .build();
        conv = conversationRepository.save(conv);

        return ChatResponse.builder()
            .respuesta(respuestaTexto)
            .intentType(intentType)
            .conversacionId(conv.getId())
            .datos(datos)
            .build();
    }

    private String cleanJson(String raw) {
        if (raw == null) return "{}";
        return raw.replaceAll("```json", "")
                  .replaceAll("```", "")
                  .trim();
    }
}
