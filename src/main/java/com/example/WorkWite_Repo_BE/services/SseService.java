package com.example.WorkWite_Repo_BE.services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// SseService.java
@Service
public class SseService {
    private final Map<Long, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter registerEmitter(Long applicantId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.computeIfAbsent(applicantId, k -> new ArrayList<>()).add(emitter);

        emitter.onCompletion(() -> removeEmitter(applicantId, emitter));
        emitter.onTimeout(() -> removeEmitter(applicantId, emitter));
        return emitter;
    }

    public void sendEvent(Long applicantId, String eventName, Object data) {
        List<SseEmitter> emitterList = emitters.get(applicantId);
        if (emitterList != null) {
            emitterList.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event().name(eventName).data(data));
                } catch (Exception e) {
                    emitter.complete();
                }
            });
        }
    }

    private void removeEmitter(Long applicantId, SseEmitter emitter) {
        List<SseEmitter> emitterList = emitters.get(applicantId);
        if (emitterList != null) {
            emitterList.remove(emitter);
        }
    }
}
