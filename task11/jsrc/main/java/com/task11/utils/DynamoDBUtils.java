package com.task11.utils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.List;
import java.util.Map;

public class DynamoDBUtils {

    public static List<Map<String, AttributeValue>> getAllItemsFromTable(AmazonDynamoDB dynamoDBClient, String dynamoDbTable) {
        ScanRequest scanRequest = new ScanRequest().withTableName(dynamoDbTable);
        ScanResult result;

        do {
            result = dynamoDBClient.scan(scanRequest);
            scanRequest.setExclusiveStartKey(result.getLastEvaluatedKey());
        } while (result.getLastEvaluatedKey() != null);

        return result.getItems();
    }
}
