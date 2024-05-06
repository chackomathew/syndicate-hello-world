package com.task09;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.TracingMode;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

@LambdaHandler(lambdaName = "processor", roleName = "processor-role", tracingMode = TracingMode.Active)
@LambdaUrlConfig(authType = AuthType.NONE, invokeMode = InvokeMode.BUFFERED)
public class Processor implements RequestHandler<APIGatewayProxyRequestEvent, String> {

    private static final String DYNAMODB_TABLE = "cmtr-21c6166e-Weather-test";
    private static final String EMPTY_RESPONSE = "";

    private final AmazonDynamoDB dynamoDBClient;
    private final DynamoDB dynamoDB;

    public Processor() {
        dynamoDBClient = new AmazonDynamoDBClient();
        dynamoDBClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
        dynamoDB = new DynamoDB(dynamoDBClient);
    }

    @Override
    public String handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        Table weatherTable = dynamoDB.getTable(DYNAMODB_TABLE);

        try {
            String id = UUID.randomUUID().toString();

            URL url = new URL("https://api.open-meteo.com/v1/forecast?latitude=50.4547&longitude=30.5238&hourly=temperature_2m&timezone=Europe%2FKyiv");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();

                reader.lines().forEach(response::append);
                reader.close();

                Item item = new Item()
                        .withPrimaryKey("id", id)
                        .withJSON("forecast", String.valueOf(response));
                weatherTable.putItem(item);

                return String.valueOf(response);
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return EMPTY_RESPONSE;
    }
}
