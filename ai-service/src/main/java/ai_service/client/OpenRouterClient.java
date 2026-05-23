package ai_service.client;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import ai_service.dto.OpenRouterRequest;
import ai_service.dto.OpenRouterResponse;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenRouterClient {

    @Value("${openrouter.api-key}")
    private String apiKey;

    @Value("${openrouter.model}")
    private String model;

    @Value("${openrouter.url}")
    private String url;

    private final RestTemplate restTemplate;

   public String sendMessage(String systemPrompt, String userMessage) {

    OpenRouterRequest request = OpenRouterRequest.builder()
        .model(model)
        .max_tokens(1024)
        .temperature(0.7)
        .messages(List.of(
            new OpenRouterRequest.Message("system", systemPrompt),
            new OpenRouterRequest.Message("user",   userMessage)
        ))
        .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + apiKey);
    headers.set("HTTP-Referer",  "http://localhost:3000");
    headers.set("X-Title",       "Kuanto App");

    HttpEntity<OpenRouterRequest> entity =
        new HttpEntity<>(request, headers);

    try {
        OpenRouterResponse response = restTemplate.postForObject(
            url, entity, OpenRouterResponse.class
        );
        String text = response != null ? response.getText() : "";
        log.debug("Respuesta OpenRouter raw: {}", text);

        // limpia el bloque <think>...</think> que usan modelos de razonamiento
        String cleaned = removeThinkingBlock(text);
        log.debug("Respuesta limpia: {}", cleaned);
        return cleaned;

    } catch (Exception e) {
        log.error("Error llamando a OpenRouter: {}", e.getMessage());
        return "{\"tipo\":\"chat\",\"respuesta\":\"Tuve un problema, intenta de nuevo.\"}";
    }
}

    // elimina bloques <think>...</think> del razonamiento interno
    private String removeThinkingBlock(String text) {
        if (text == null) return "{}";
        // remueve todo lo que esté entre <think> y </think>
        String cleaned = text.replaceAll("(?s)<think>.*?</think>", "").trim();
        // si quedó vacío devuelve un JSON por defecto
        return cleaned.isEmpty() ? "{\"tipo\":\"chat\",\"respuesta\":\"No pude procesar tu mensaje.\"}" : cleaned;
    }
}