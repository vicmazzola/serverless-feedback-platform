package br.com.fiap.feedback.shared.service;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

public record ReportPeriod(ZonedDateTime start, ZonedDateTime end) {
    public static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    public static String weekKey(Instant instant) {
        return weekKey(instant.atZone(ZONE).toLocalDate());
    }

    public static String weekKey(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString();
    }

    public static ReportPeriod previousCompletedWeek(Clock clock) {
        var monday = LocalDate.now(clock.withZone(ZONE)).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return new ReportPeriod(monday.minusWeeks(1).atStartOfDay(ZONE), monday.atStartOfDay(ZONE));
    }
}
