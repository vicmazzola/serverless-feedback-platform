package br.com.fiap.feedback.report;

import br.com.fiap.feedback.shared.domain.*;
import br.com.fiap.feedback.shared.repository.EvaluationRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.*;

final class ReportRepository implements EvaluationRepository {
    private final DynamoDbClient client;
    private final String table;

    ReportRepository(DynamoDbClient c, String t) {
        client = c;
        table = t;
    }

    public void save(Evaluation e) {
        throw new UnsupportedOperationException();
    }

    public List<Evaluation> findByReportWeek(String week) {
        var all = new ArrayList<Evaluation>();
        Map<String, AttributeValue> key = null;
        do {
            var p = client.query(QueryRequest.builder().tableName(table).indexName("ReportWeekIndex").keyConditionExpression("reportWeek = :week").expressionAttributeValues(Map.of(":week", AttributeValue.builder().s(week).build())).exclusiveStartKey(key).build());
            p.items().forEach(i -> all.add(new Evaluation(i.get("id").s(), i.get("descricao").s(), Integer.parseInt(i.get("nota").n()), Urgency.valueOf(i.get("urgency").s()), Instant.parse(i.get("createdAt").s()), i.get("reportWeek").s())));
            key = p.lastEvaluatedKey();
        } while (key != null && !key.isEmpty());
        return all;
    }
}
