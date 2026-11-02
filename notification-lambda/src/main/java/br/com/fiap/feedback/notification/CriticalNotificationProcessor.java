package br.com.fiap.feedback.notification;

import br.com.fiap.feedback.shared.domain.*;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.time.format.DateTimeFormatter;

public final class CriticalNotificationProcessor {
    private final SnsClient sns;
    private final String topicArn;

    public CriticalNotificationProcessor(SnsClient sns, String topicArn) {
        this.sns = sns;
        this.topicArn = topicArn;
    }

    public boolean notifyIfCritical(Evaluation e) {
        if (e.urgency() != Urgency.CRITICAL) return false;
        var body = "Critical student feedback\nDescription: " + e.description() + "\nUrgency: " + e.urgency() + "\nSubmission date: " + DateTimeFormatter.ISO_INSTANT.format(e.createdAt()) + "\nGrade: " + e.grade() + "\nEvaluation ID: " + e.id();
        sns.publish(PublishRequest.builder().topicArn(topicArn).subject("[CRITICAL] Student feedback").message(body).build());
        return true;
    }
}
