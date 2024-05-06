package com.task10.utils;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.ListUserPoolClientsRequest;
import com.amazonaws.services.cognitoidp.model.ListUserPoolClientsResult;
import com.amazonaws.services.cognitoidp.model.ListUserPoolsRequest;
import com.amazonaws.services.cognitoidp.model.ListUserPoolsResult;
import com.amazonaws.services.cognitoidp.model.UserPoolDescriptionType;

public class UserPoolUtils {

    public static final int MAX_RESULTS = 10;

    public static String getUserPoolId(String userPoolName) {
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        ListUserPoolsRequest listUserPoolsRequest = new ListUserPoolsRequest()
                .withMaxResults(MAX_RESULTS);

        ListUserPoolsResult listUserPoolsResult = cognitoClient.listUserPools(listUserPoolsRequest);

        for (UserPoolDescriptionType userPool : listUserPoolsResult.getUserPools()) {
            if (userPoolName.equals(userPool.getName())) {
                return userPool.getId();
            }
        }

        throw new IllegalArgumentException("User pool with name " + userPoolName + " not found");
    }

    public static String getUserClientId(String userPoolName) {
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();
        ListUserPoolClientsRequest request = new ListUserPoolClientsRequest()
                .withUserPoolId(getUserPoolId(userPoolName));

        ListUserPoolClientsResult result = cognitoClient.listUserPoolClients(request);
        return result.getUserPoolClients().get(0).getClientId();
    }
}
