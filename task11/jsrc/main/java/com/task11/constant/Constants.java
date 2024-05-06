package com.task11.constant;

public interface Constants {

    String INVALID_EMAIL = "invalid_user@test.com";

    interface SignUpParams {
        String FIRST_NAME = "given_name";
        String LAST_NAME = "family_name";
        String EMAIL = "email";
    }

    interface SignInParams {
        String USERNAME = "USERNAME";
        String PASSWORD = "PASSWORD";
    }

    interface TableParams {
        String TABLES = "tables";
        String ID = "id";
        String NUMBER = "number";
        String PLACES = "places";
        String IS_VIP = "isVip";
        String MIN_ORDER = "minOrder";
    }

    interface ReservationParams {
        String RESERVATIONS = "reservations";
        String ID = "id";
        String TABLE_NUMBER = "tableNumber";
        String CLIENT_NAME = "clientName";
        String PHONE_NUMBER = "phoneNumber";
        String DATE = "date";
        String SLOT_TIME_START = "slotTimeStart";
        String SLOT_TIME_END = "slotTimeEnd";
    }

    interface HttpMethods {
        String GET = "GET";
        String POST = "POST";
    }

    interface StatusCodes {
        int SUCCESS = 200;
        int BAD_REQUEST = 400;
    }
}
