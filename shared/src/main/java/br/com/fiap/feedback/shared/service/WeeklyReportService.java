package br.com.fiap.feedback.shared.service;

import br.com.fiap.feedback.shared.domain.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public final class WeeklyReportService {
    public String render(ReportPeriod period, List<Evaluation> items) {
        var fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm z").withZone(ReportPeriod.ZONE);
        var days = items.stream().collect(Collectors.groupingBy(e -> e.createdAt().atZone(ReportPeriod.ZONE).toLocalDate(), TreeMap::new, Collectors.counting()));
        var average = items.stream().mapToInt(Evaluation::grade).average();
        var out = new StringBuilder("Weekly student feedback report\nPeriod: ").append(fmt.format(period.start())).append(" to ").append(fmt.format(period.end())).append(" (exclusive)\nTotal evaluations: ").append(items.size()).append("\nAverage grade: ").append(average.isPresent() ? String.format(Locale.ROOT, "%.2f", average.getAsDouble()) : "N/A").append("\n\nBy urgency:\n");
        for (var u : Urgency.values())
            out.append("- ").append(u).append(": ").append(items.stream().filter(e -> e.urgency() == u).count()).append('\n');
        out.append("\nEvaluations per day:\n");
        if (days.isEmpty()) out.append("- No evaluations\n");
        else days.forEach((d, c) -> out.append("- ").append(d).append(": ").append(c).append('\n'));
        out.append("\nEvaluation details:\n");
        if (items.isEmpty()) out.append("- No evaluations in this period\n");
        else
            items.stream().sorted(Comparator.comparing(Evaluation::createdAt)).forEach(e -> out.append("- [").append(e.urgency()).append("] grade ").append(e.grade()).append(" | ").append(fmt.format(e.createdAt())).append(" | ").append(e.description()).append('\n'));
        return out.toString();
    }
}
