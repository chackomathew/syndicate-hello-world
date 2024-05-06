package com.task10.entity;

import java.util.Map;
import java.util.UUID;

public class ReservationRequestModel {

    private String id;
    private int tableNumber;
    private String clientName;
    private String phoneNumber;
    private String date;
    private String slotTimeStart;
    private String slotTimeEnd;

    public ReservationRequestModel(Map<String, Object> body) {
        setDataFromRequestBody(body);
    }

    public String getId() {
        return id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public String getSlotTimeStart() {
        return slotTimeStart;
    }

    public String getSlotTimeEnd() {
        return slotTimeEnd;
    }

    public void setDataFromRequestBody(Map<String, Object> body) {
        this.id = UUID.randomUUID().toString();
        this.tableNumber = (int) body.get("tableNumber");
        this.clientName = (String) body.get("clientName");
        this.phoneNumber = (String) body.get("phoneNumber");
        this.date = (String) body.get("date");
        this.slotTimeStart = (String) body.get("slotTimeStart");
        this.slotTimeEnd = (String) body.get("slotTimeEnd");
    }
}
