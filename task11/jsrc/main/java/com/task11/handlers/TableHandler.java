package com.task11.handlers;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.dynamodbv2.document.Item;

import com.task11.constant.Constants;
import com.task11.entity.TableRequestModel;
import com.task11.utils.DynamoDBUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableHandler {

    private static final String DYNAMODB_TABLE = "cmtr-21c6166e-Tables-test";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            AmazonDynamoDB dynamoDBClient = new AmazonDynamoDBClient();
            dynamoDBClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
            DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);

            Table tables = dynamoDB.getTable(DYNAMODB_TABLE);

            switch (request.getHttpMethod()) {
                case Constants.HttpMethods.GET: {
                    String[] splitPath = request.getPath().split("/");

                    if (splitPath.length > 2) {
                        Item item = tables.getItem(Constants.TableParams.ID, Integer.parseInt(splitPath[splitPath.length - 1]));
                        Map<String, Object> body = convertToJSON(item);

                        response.setBody(objectMapper.writeValueAsString(body));
                    } else {
                        List<Map<String, AttributeValue>> items = DynamoDBUtils.getAllItemsFromTable(dynamoDBClient, DYNAMODB_TABLE);
                        Map<String, List<Map<String, Object>>> body = convertToJSON(items);

                        response.setBody(objectMapper.writeValueAsString(body));
                    }
                    break;
                }
                case Constants.HttpMethods.POST: {
                    Map<String, Object> body = objectMapper.readValue(request.getBody(), Map.class);
                    TableRequestModel tableRequestModel = new TableRequestModel(body);

                    Item item = new Item()
                            .withPrimaryKey(Constants.TableParams.ID, tableRequestModel.getId())
                            .withInt(Constants.TableParams.NUMBER, tableRequestModel.getNumber())
                            .withInt(Constants.TableParams.PLACES, tableRequestModel.getPlaces())
                            .withBoolean(Constants.TableParams.IS_VIP, tableRequestModel.isVip())
                            .withInt(Constants.TableParams.MIN_ORDER, tableRequestModel.getMinOrder());
                    tables.putItem(item);

                    response.setBody("{\"id\": " + tableRequestModel.getId() + "}");
                }
            }

            response.setStatusCode(Constants.StatusCodes.SUCCESS);
            return response;
        } catch (IOException exception) {
            context.getLogger().log("Exception: " + exception);

            response.setStatusCode(Constants.StatusCodes.BAD_REQUEST);
            return response;
        }
    }

    private static Map<String, Object> convertToJSON(Item item) {
        Map<String, Object> jItem = new HashMap<>();
        jItem.put(Constants.TableParams.ID, item.getNumber(Constants.TableParams.ID));
        jItem.put(Constants.TableParams.NUMBER, item.getNumber(Constants.TableParams.NUMBER));
        jItem.put(Constants.TableParams.PLACES, item.getNumber(Constants.TableParams.PLACES));
        jItem.put(Constants.TableParams.IS_VIP, item.getBOOL(Constants.TableParams.IS_VIP));
        jItem.put(Constants.TableParams.MIN_ORDER, item.getNumber(Constants.TableParams.MIN_ORDER));

        return jItem;
    }

    private static Map<String, List<Map<String, Object>>> convertToJSON(List<Map<String, AttributeValue>> items) {
        List<Map<String, Object>> jItems = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            Map<String, Object> jItem = new HashMap<>();
            jItem.put(Constants.TableParams.ID, Integer.parseInt(item.get(Constants.TableParams.ID).getN()));
            jItem.put(Constants.TableParams.NUMBER, Integer.parseInt(item.get(Constants.TableParams.NUMBER).getN()));
            jItem.put(Constants.TableParams.PLACES, Integer.parseInt(item.get(Constants.TableParams.PLACES).getN()));
            jItem.put(Constants.TableParams.IS_VIP, item.get(Constants.TableParams.IS_VIP).getBOOL());
            jItem.put(Constants.TableParams.MIN_ORDER, Integer.parseInt(item.get(Constants.TableParams.MIN_ORDER).getN()));

            jItems.add(jItem);
        }

        Map<String, List<Map<String, Object>>> jFinal = new HashMap<>();
        jFinal.put(Constants.TableParams.TABLES, jItems);

        return jFinal;
    }
}
