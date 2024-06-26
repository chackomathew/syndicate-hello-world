package com.task11;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.task11.handlers.ReservationHandler;
import com.task11.handlers.TableHandler;
import com.task11.handlers.SignInHandler;
import com.task11.handlers.SingUpHandler;

@LambdaHandler(lambdaName = "api_handler", roleName = "api_handler-role")
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String path = request.getPath();

        switch (path) {
            case "/signup": {
                return SingUpHandler.execute(request, context);
            }
            case "/signin": {
                return SignInHandler.execute(request, context);
            }
            case "/tables": {
                return TableHandler.execute(request, context);
            }
            case "/reservations": {
                return ReservationHandler.execute(request, context);
            }
        }

        return TableHandler.execute(request, context);
    }
}
