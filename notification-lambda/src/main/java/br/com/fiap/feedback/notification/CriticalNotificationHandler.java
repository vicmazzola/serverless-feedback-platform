package br.com.fiap.feedback.notification;

import com.amazonaws.services.lambda.runtime.*;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import br.com.fiap.feedback.shared.domain.*;
import software.amazon.awssdk.services.sns.SnsClient;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CriticalNotificationHandler implements RequestHandler<DynamodbEvent, Void> {
    private final CriticalNotificationProcessor processor;
    private final Set<String> processed = new ConcurrentHashMap<String, Boolean>().keySet(Boolean.TRUE);

    public CriticalNotificationHandler() {
        this(new CriticalNotificationProcessor(SnsClient.builder().build(), required("NOTIFICATION_TOPIC_ARN")));
    }

    CriticalNotificationHandler(CriticalNotificationProcessor p) {
        processor = p;
    }

    public Void handleRequest(DynamodbEvent event, Context context) {
        if (event == null || event.getRecords() == null) return null;
        for (var record : event.getRecords()) {
            if (!"INSERT".equals(record.getEventName())) continue;
            String id = record.getEventID();
            if (id != null && !processed.add(id)) continue;
            try {
                var image = record.getDynamodb().getNewImage();
                var e = new Evaluation(s(image, "id"), s(image, "descricao"), Integer.parseInt(n(image, "nota")), Urgency.valueOf(s(image, "urgency")), Instant.parse(s(image, "createdAt")), s(image, "reportWeek"));
                processor.notifyIfCritical(e);
                context.getLogger().log("Processed evaluation " + e.id() + "\n");
            } catch (RuntimeException ex) {
                if (id != null) processed.remove(id);
                throw ex;
            }
        }
        return null;
    }

    private static String s(Map<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> i, String key) {
        return i.get(key).getS();
    }

    private static String n(Map<String, com.amazonaws.services.lambda.runtime.events.models.dynamodb.AttributeValue> i, String key) {
        return i.get(key).getN();
    }

    private static String required(String key) {
        var value = System.getenv(key);
        if (value == null || value.isBlank()) throw new IllegalStateException(key + " is required");
        return value;
    }
}
