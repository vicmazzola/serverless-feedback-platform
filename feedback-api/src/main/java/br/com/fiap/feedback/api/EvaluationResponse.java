package br.com.fiap.feedback.api;

import br.com.fiap.feedback.shared.domain.Evaluation;

public record EvaluationResponse(String id, String descricao, int nota, String urgencia, String dataSubmissao) {
    static EvaluationResponse from(Evaluation e) {
        return new EvaluationResponse(e.id(), e.description(), e.grade(), e.urgency().name(), e.createdAt().toString());
    }
}
