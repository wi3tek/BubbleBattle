package pl.pw.bubblebattle.infrastructure;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pw.bubblebattle.api.model.GameResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SseEmitterManager {
    private static final Logger logger = Logger.getLogger(SseEmitterManager.class.getName());
    private static final Map<String, SseEmitter> emitters = new HashMap<>();

    public static void addEmitter(String subscriberId, SseEmitter emitter) {
        emitters.putIfAbsent(subscriberId, emitter);
    }

    public static void removeEmitter(String subscriberId) {
        emitters.remove(subscriberId);
    }

    public static void sendSseEventToClients(String subscriberId, GameResponse gameResponse) {
        var emitter = emitters.get(subscriberId);
        if (emitter == null) {
            logger.warning("No client with subscriber Id " + subscriberId + " found!");
            return;
        }
        try {
            emitter.send(gameResponse);
        } catch (IOException e) {
            logger.warning("Error sending event to client: " + e.getMessage());
        }
    }
}