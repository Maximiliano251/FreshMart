package ai_service.service;


import org.springframework.stereotype.Service;

import ai_service.domain.Conversation;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromptService {

    public String buildSystemPrompt(String userName, String ciudad,
                                    String supermercadoFav,
                                    String historial,
                                    String memorias) {
        return """
            Eres Kuanto, asistente chileno de cocina y compras inteligente.
            Hablas español chileno, cercano y directo.

            Usuario: %s | Ciudad: %s | Supermercado favorito: %s

            Lo que recuerdas de este usuario:
            %s

            Historial reciente de conversación:
            %s

            Responde SIEMPRE con JSON puro, sin markdown, sin bloques
            de código, sin texto antes ni después del JSON.

            Si piden RECETA (quiero hacer X / receta de X / cómo hago X):
            {"tipo":"recipe","receta":"nombre","ingredientes":[{"nombre":"...","cantidad":"...","unidad":"..."}],"pasos":["paso 1","paso 2"],"tiempo_minutos":30,"dificultad":"facil"}

            Si buscan PRODUCTO (precio de X / dónde compro X):
            {"tipo":"search","producto":"nombre","descripcion":"descripción breve"}

            Para TODO LO DEMÁS:
            {"tipo":"chat","respuesta":"tu respuesta aquí"}
            """.formatted(userName, ciudad, supermercadoFav,
                         memorias, historial);
    }

    public String buildHistorial(List<Conversation> convs) {
        if (convs.isEmpty()) return "Sin historial previo.";
        return convs.stream()
            .map(c -> "Usuario: " + c.getUserMessage()
                    + "\nKuanto: "  + c.getAiResponse())
            .collect(Collectors.joining("\n---\n"));
    }

    public String buildMemorias(List<Object> memorias) {
        if (memorias == null || memorias.isEmpty())
            return "Sin memorias previas.";
        return memorias.stream()
            .map(Object::toString)
            .collect(Collectors.joining("\n- ", "- ", ""));
    }
}