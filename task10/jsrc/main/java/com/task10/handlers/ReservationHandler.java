package com.task10.handlers;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task10.constant.Constants;
import com.task10.entity.ReservationRequestModel;
import com.task10.utils.DynamoDBUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReservationHandler {

    private static final String DYNAMODB_RESERVATION = "cmtr-914927e6-Reservations-test";
    private static final String DYNAMODB_TABLE = "cmtr-914927e6-Tables-test";

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        AmazonDynamoDB dynamoDBClient = new AmazonDynamoDBClient();
        dynamoDBClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
        DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);

        Table reservations = dynamoDB.getTable(DYNAMODB_RESERVATION);

        try {
            switch (request.getHttpMethod()) {
                case Constants.HttpMethods.GET: {
                    List<Map<String, AttributeValue>> items = DynamoDBUtils.getAllItemsFromTable(dynamoDBClient, DYNAMODB_RESERVATION);
                    Map<String, List<Map<String, Object>>> body = convertToJSON(items);
                    response.setBody(objectMapper.writeValueAsString(body));

                    break;
                }
                case Constants.HttpMethods.POST: {
                    Map<String, Object> body = objectMapper.readValue(request.getBody(), Map.class);
                    ReservationRequestModel reservationRequestModel = new ReservationRequestModel(body);

                    List<Map<String, AttributeValue>> reservationRecords = DynamoDBUtils.getAllItemsFromTable(dynamoDBClient, DYNAMODB_RESERVATION);

                    for (Map<String, AttributeValue> reservationRecord : reservationRecords) {
                        int tableNumber = Integer.parseInt(reservationRecord.get(Constants.ReservationParams.TABLE_NUMBER).getN());

                        if (tableNumber == reservationRequestModel.getTableNumber()) {
                            response.setStatusCode(Constants.StatusCodes.BAD_REQUEST);
                            return response;
                        }
                    }

                    List<Map<String, AttributeValue>> tableRecords = DynamoDBUtils.getAllItemsFromTable(dynamoDBClient, DYNAMODB_TABLE);
                    List<Map<String, AttributeValue>> findTable = tableRecords.stream().filter(tableRecord ->
                                    Integer.parseInt(tableRecord.get(Constants.TableParams.NUMBER).getN()) == reservationRequestModel.getTableNumber())
                            .collect(Collectors.toList());

                    if (findTable.isEmpty()) {
                        response.setStatusCode(Constants.StatusCodes.BAD_REQUEST);
                        return response;
                    }

                    Item item = new Item()
                            .withPrimaryKey(Constants.ReservationParams.ID, reservationRequestModel.getId())
                            .withInt(Constants.ReservationParams.TABLE_NUMBER, reservationRequestModel.getTableNumber())
                            .withString(Constants.ReservationParams.CLIENT_NAME, reservationRequestModel.getClientName())
                            .withString(Constants.ReservationParams.PHONE_NUMBER, reservationRequestModel.getPhoneNumber())
                            .withString(Constants.ReservationParams.DATE, reservationRequestModel.getDate())
                            .withString(Constants.ReservationParams.SLOT_TIME_START, reservationRequestModel.getSlotTimeStart())
                            .withString(Constants.ReservationParams.SLOT_TIME_END, reservationRequestModel.getSlotTimeEnd());

                    reservations.putItem(item);
                    response.setBody("{\"reservationId\": \"" + reservationRequestModel.getId() + "\"}");
                }
            }

            response.setStatusCode(Constants.StatusCodes.SUCCESS);
            return response;
        } catch (Exception exception) {
            context.getLogger().log("Exception: " + exception);

            response.setStatusCode(Constants.StatusCodes.BAD_REQUEST);
            return response;
        }
    }

    private static Map<String, List<Map<String, Object>>> convertToJSON(List<Map<String, AttributeValue>> items) {
        List<Map<String, Object>> jItems = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            Map<String, Object> jItem = new HashMap<>();
            jItem.put(Constants.ReservationParams.TABLE_NUMBER, Integer.parseInt(item.get(Constants.ReservationParams.TABLE_NUMBER).getN()));
            jItem.put(Constants.ReservationParams.CLIENT_NAME, item.get(Constants.ReservationParams.CLIENT_NAME).getS());
            jItem.put(Constants.ReservationParams.PHONE_NUMBER, item.get(Constants.ReservationParams.PHONE_NUMBER).getS());
            jItem.put(Constants.ReservationParams.DATE, item.get(Constants.ReservationParams.DATE).getS());
            jItem.put(Constants.ReservationParams.SLOT_TIME_START, item.get(Constants.ReservationParams.SLOT_TIME_START).getS());
            jItem.put(Constants.ReservationParams.SLOT_TIME_END, item.get(Constants.ReservationParams.SLOT_TIME_END).getS());

            jItems.add(jItem);
        }

        Map<String, List<Map<String, Object>>> jFinal = new HashMap<>();
        jFinal.put(Constants.ReservationParams.RESERVATIONS, jItems);

        return jFinal;
    }
}

