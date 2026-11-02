package br.com.fiap.feedback.shared.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UrgencyClassifierTest {
    @Test
    void classifiesBoundaries() {
        assertEquals(Urgency.CRITICAL, UrgencyClassifier.classify(0));
        assertEquals(Urgency.CRITICAL, UrgencyClassifier.classify(3));
        assertEquals(Urgency.MEDIUM, UrgencyClassifier.classify(4));
        assertEquals(Urgency.MEDIUM, UrgencyClassifier.classify(6));
        assertEquals(Urgency.LOW, UrgencyClassifier.classify(7));
        assertEquals(Urgency.LOW, UrgencyClassifier.classify(10));
    }

    @Test
    void rejectsOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> UrgencyClassifier.classify(-1));
        assertThrows(IllegalArgumentException.class, () -> UrgencyClassifier.classify(11));
    }
}
