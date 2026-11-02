package br.com.fiap.feedback.api;

import br.com.fiap.feedback.shared.domain.*;
import br.com.fiap.feedback.shared.repository.EvaluationRepository;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FeedbackServiceTest {
    static class MemoryRepository implements EvaluationRepository {
        Evaluation value;

        public void save(Evaluation e) {
            value = e;
        }

        public List<Evaluation> findByReportWeek(String w) {
            return List.of();
        }
    }

    @Test
    void savesValidEvaluation() {
        var repo = new MemoryRepository();
        var service = new FeedbackService(repo, Clock.fixed(Instant.parse("2026-01-05T12:00:00Z"), ZoneOffset.UTC));
        var e = service.create(new EvaluationRequest(" Audio issue ", 2));
        assertEquals("Audio issue", e.description());
        assertEquals(Urgency.CRITICAL, e.urgency());
        assertSame(e, repo.value);
    }

    @Test
    void acceptsGradeZeroAndTen() {
        var r = new MemoryRepository();
        var s = new FeedbackService(r, Clock.systemUTC());
        assertEquals(Urgency.CRITICAL, s.create(new EvaluationRequest("x", 0)).urgency());
        assertEquals(Urgency.LOW, s.create(new EvaluationRequest("x", 10)).urgency());
    }

    @Test
    void rejectsInvalidInput() {
        var s = new FeedbackService(new MemoryRepository(), Clock.systemUTC());
        assertThrows(InvalidEvaluationException.class, () -> s.create(new EvaluationRequest(" ", 5)));
        assertThrows(InvalidEvaluationException.class, () -> s.create(new EvaluationRequest("x", null)));
        assertThrows(InvalidEvaluationException.class, () -> s.create(new EvaluationRequest("x", -1)));
        assertThrows(InvalidEvaluationException.class, () -> s.create(new EvaluationRequest("x", 11)));
    }
}
