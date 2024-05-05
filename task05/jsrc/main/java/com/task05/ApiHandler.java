package com.task05;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(lambdaName = "api_handler",
	roleName = "api_handler-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AmazonDynamoDB dynamoDb = AmazonDynamoDBClientBuilder.defaultClient();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        Map<String, AttributeValue> item = new HashMap<>();
        String eventId = UUID.randomUUID().toString();
        item.put("id", new AttributeValue().withS(eventId));

        Map<String, String> body = null;
        Map<String, String> content = null;
        try {
            body = objectMapper.readValue(input.getBody(), Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        String principalId = body.get("principalId");
        try {
            content = objectMapper.readValue(body.get("content"), Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        item.put("principalId", new AttributeValue().withN(principalId));
        item.put("createdAt", new AttributeValue().withS(Instant.now().toString()));
        try {
            item.put("body", new AttributeValue().withS(objectMapper.writeValueAsString(content)));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        PutItemResult result = dynamoDb.putItem("cmtr-21c6166e-Events", item);

        Map<String, String> response = new HashMap<>();
        try {
            response.put("event", objectMapper.writeValueAsString(result.getAttributes()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(201)
                .withBody(response.toString());
	}
}
