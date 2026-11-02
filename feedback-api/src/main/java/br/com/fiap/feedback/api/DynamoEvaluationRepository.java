package br.com.fiap.feedback.api;

import br.com.fiap.feedback.shared.domain.*;
import br.com.fiap.feedback.shared.repository.EvaluationRepository;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.*;

public final class DynamoEvaluationRepository implements EvaluationRepository {
    private final DynamoDbClient client;
    private final String table;

    public DynamoEvaluationRepository(DynamoDbClient c, String t) {
        client = c;
        table = t;
    }

    public void save(Evaluation e) {
        client.putItem(PutItemRequest.builder().tableName(table).item(Map.of("pk", s("EVALUATION"), "sk", s(e.createdAt() + "#" + e.id()), "id", s(e.id()), "descricao", s(e.description()), "nota", AttributeValue.builder().n(String.valueOf(e.grade())).build(), "urgency", s(e.urgency().name()), "createdAt", s(e.createdAt().toString()), "reportWeek", s(e.reportWeek()))).build());
    }

    public List<Evaluation> findByReportWeek(String week) {
        var all = new ArrayList<Evaluation>();
        Map<String, AttributeValue> key = null;
        do {
            var result = client.query(QueryRequest.builder().tableName(table).indexName("ReportWeekIndex").keyConditionExpression("reportWeek = :week").expressionAttributeValues(Map.of(":week", s(week))).exclusiveStartKey(key).build());
            result.items().forEach(i -> all.add(new Evaluation(i.get("id").s(), i.get("descricao").s(), Integer.parseInt(i.get("nota").n()), Urgency.valueOf(i.get("urgency").s()), Instant.parse(i.get("createdAt").s()), i.get("reportWeek").s())));
            key = result.lastEvaluatedKey();
        } while (key != null && !key.isEmpty());
        return all;
    }

    private static AttributeValue s(String v) {
        return AttributeValue.builder().s(v).build();
    }
}
