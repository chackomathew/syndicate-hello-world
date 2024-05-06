package com.task11.entity;

import java.util.Map;

public class TableRequestModel {

    private int id;
    private int number;
    private int places;
    private boolean isVip;
    private int minOrder;

    public TableRequestModel() {

    }

    public TableRequestModel(Map<String, Object> body) {
        setDataFromRequestBody(body);
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public int getPlaces() {
        return places;
    }

    public boolean isVip() {
        return isVip;
    }

    public int getMinOrder() {
        return minOrder;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setPlaces(int places) {
        this.places = places;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public void setMinOrder(int minOrder) {
        this.minOrder = minOrder;
    }

    public void setDataFromRequestBody(Map<String, Object> body) {
        this.id = (int) body.get("id");
        this.number = (int) body.get("number");
        this.places = (int) body.get("places");
        this.isVip = (boolean) body.get("isVip");
        this.minOrder = (int) body.get("minOrder");
    }
}
