package br.com.fiap.feedback.api;

import br.com.fiap.feedback.shared.domain.*;
import br.com.fiap.feedback.shared.repository.EvaluationRepository;
import br.com.fiap.feedback.shared.service.ReportPeriod;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.*;
import java.util.UUID;

@ApplicationScoped
public class FeedbackService {
    private final EvaluationRepository repository;
    private final Clock clock;

    @Inject
    public FeedbackService(EvaluationRepository repository) {
        this(repository, Clock.systemUTC());
    }

    FeedbackService(EvaluationRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public Evaluation create(EvaluationRequest request) {
        if (request == null || request.descricao() == null || request.descricao().isBlank())
            throw new InvalidEvaluationException("descricao is required and cannot be blank");
        if (request.nota() == null) throw new InvalidEvaluationException("nota is required");
        final Urgency urgency;
        try {
            urgency = UrgencyClassifier.classify(request.nota());
        } catch (IllegalArgumentException e) {
            throw new InvalidEvaluationException(e.getMessage());
        }
        var now = clock.instant();
        var e = new Evaluation(UUID.randomUUID().toString(), request.descricao().trim(), request.nota(), urgency, now, ReportPeriod.weekKey(now));
        repository.save(e);
        return e;
    }
}
