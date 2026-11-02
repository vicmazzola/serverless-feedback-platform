package br.com.fiap.feedback.report;

import com.amazonaws.services.lambda.runtime.*;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import br.com.fiap.feedback.shared.repository.EvaluationRepository;
import br.com.fiap.feedback.shared.service.*;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

import java.time.Clock;

public class WeeklyReportHandler implements RequestHandler<ScheduledEvent, Void> {
    private final EvaluationRepository repository;
    private final SnsClient sns;
    private final String topic;
    private final Clock clock;

    public WeeklyReportHandler() {
        this(new ReportRepository(DynamoDbClient.builder().build(), required("FEEDBACK_TABLE_NAME")), SnsClient.builder().build(), required("REPORT_TOPIC_ARN"), Clock.systemUTC());
    }

    WeeklyReportHandler(EvaluationRepository r, SnsClient s, String t, Clock c) {
        repository = r;
        sns = s;
        topic = t;
        clock = c;
    }

    public Void handleRequest(ScheduledEvent event, Context context) {
        var period = ReportPeriod.previousCompletedWeek(clock);
        var body = new WeeklyReportService().render(period, repository.findByReportWeek(period.start().toLocalDate().toString()));
        sns.publish(PublishRequest.builder().topicArn(topic).subject("Weekly student feedback report").message(body).build());
        context.getLogger().log("Published report for " + period.start() + "\n");
        return null;
    }

    private static String required(String key) {
        var value = System.getenv(key);
        if (value == null || value.isBlank()) throw new IllegalStateException(key + " is required");
        return value;
    }
}
