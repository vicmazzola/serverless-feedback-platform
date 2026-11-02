package br.com.fiap.feedback.notification;

import br.com.fiap.feedback.shared.domain.*;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CriticalNotificationProcessorTest {
    private final SnsClient sns = mock(SnsClient.class);

    private Evaluation evaluation(Urgency u) {
        return new Evaluation("id", "Audio failing", 2, u, Instant.parse("2026-01-01T00:00:00Z"), "2025-12-29");
    }

    @Test
    void publishesCritical() {
        var p = new CriticalNotificationProcessor(sns, "topic");
        assertTrue(p.notifyIfCritical(evaluation(Urgency.CRITICAL)));
        var captor = ArgumentCaptor.forClass(PublishRequest.class);
        verify(sns).publish(captor.capture());
        assertTrue(captor.getValue().message().contains("Audio failing"));
        assertTrue(captor.getValue().message().contains("CRITICAL"));
    }

    @Test
    void ignoresNonCritical() {
        var p = new CriticalNotificationProcessor(sns, "topic");
        assertFalse(p.notifyIfCritical(evaluation(Urgency.MEDIUM)));
        assertFalse(p.notifyIfCritical(evaluation(Urgency.LOW)));
        verifyNoInteractions(sns);
    }
}
