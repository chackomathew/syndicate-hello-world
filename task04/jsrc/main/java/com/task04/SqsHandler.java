package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

@LambdaHandler(lambdaName = "sqs_handler",
	roleName = "sqs_handler-role",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SqsTriggerEventSource(targetQueue = "async_queue", batchSize = 1)
public class SqsHandler implements RequestHandler<SQSEvent, Void> {

	public Void handleRequest(SQSEvent event, Context context) {
		System.out.println("Hello from lambda");
        event.getRecords().forEach(msg -> context.getLogger().log(msg.getBody()));
        return null;
    }
}
