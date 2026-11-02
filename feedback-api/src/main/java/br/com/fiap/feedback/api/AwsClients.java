package br.com.fiap.feedback.api;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import br.com.fiap.feedback.shared.repository.EvaluationRepository;

@Singleton
public class AwsClients {
    @Produces
    @Singleton
    DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder().build();
    }

    @Produces
    @Singleton
    EvaluationRepository repository(DynamoDbClient client, @ConfigProperty(name = "feedback.table-name") String table) {
        return new DynamoEvaluationRepository(client, table);
    }
}
