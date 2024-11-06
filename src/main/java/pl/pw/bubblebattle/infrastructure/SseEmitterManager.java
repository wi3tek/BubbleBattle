package pl.pw.bubblebattle.infrastructure;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import pl.pw.bubblebattle.api.model.GameResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
@Slf4j
public class SseEmitterManager {

    private static final Map<String, SseEmitter> emitters = new HashMap<>();

    public static void addEmitter(String subscriberId) {
        SseEmitter sseEmitter = emitters.get( subscriberId );
        if(sseEmitter != null) {
            return;
        }
        SseEmitter emitter = new SseEmitter(10800000L); // 3h
        emitters.put(subscriberId, emitter);
        log.info( "Emitter created with timeout {} for gameId {}", emitter.getTimeout(), subscriberId );
    }
    public static SseEmitter getEmitter(String subscriberId) {
        SseEmitter sseEmitter = emitters.get( subscriberId );
        if(sseEmitter == null) {
            log.warn( "Event emitter is null" );
        }
        return sseEmitter;

    }

    public static void removeEmitter(String subscriberId) {
        emitters.remove(subscriberId);
    }

    public static void sendSseEventToClients(String subscriberId, GameResponse gameResponse) {
        SseEmitter emitter = emitters.get(subscriberId);
        if (emitter == null) {
            log.warn("No client with subscriber Id " + subscriberId + " found!");
            addEmitter( subscriberId);
            emitter = getEmitter( subscriberId );
        }

        try {
            emitter.send(gameResponse);
        } catch (IOException e) {
            log.warn("Error sending event to client: " + e.getMessage());
        }
    }
}