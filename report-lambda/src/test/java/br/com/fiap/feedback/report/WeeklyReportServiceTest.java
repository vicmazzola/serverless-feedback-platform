package br.com.fiap.feedback.report;

import br.com.fiap.feedback.shared.domain.*;
import br.com.fiap.feedback.shared.service.*;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class WeeklyReportServiceTest {
    private final ReportPeriod period = new ReportPeriod(ZonedDateTime.parse("2026-01-05T00:00:00-03:00[America/Sao_Paulo]"), ZonedDateTime.parse("2026-01-12T00:00:00-03:00[America/Sao_Paulo]"));

    @Test
    void rendersEmptyPeriod() {
        var report = new WeeklyReportService().render(period, List.of());
        assertTrue(report.contains("Total evaluations: 0"));
        assertTrue(report.contains("Average grade: N/A"));
    }

    @Test
    void calculatesStats() {
        var items = List.of(new Evaluation("1", "One", 2, Urgency.CRITICAL, Instant.parse("2026-01-06T12:00:00Z"), "2026-01-05"), new Evaluation("2", "Two", 8, Urgency.LOW, Instant.parse("2026-01-06T14:00:00Z"), "2026-01-05"));
        var report = new WeeklyReportService().render(period, items);
        assertTrue(report.contains("Average grade: 5.00"));
        assertTrue(report.contains("CRITICAL: 1"));
        assertTrue(report.contains("LOW: 1"));
        assertTrue(report.contains("2026-01-06: 2"));
    }

    @Test
    void findsPreviousCalendarWeek() {
        var p = ReportPeriod.previousCompletedWeek(Clock.fixed(Instant.parse("2026-01-12T11:00:00Z"), ZoneOffset.UTC));
        assertEquals("2026-01-05", p.start().toLocalDate().toString());
        assertEquals("2026-01-12", p.end().toLocalDate().toString());
    }
}
