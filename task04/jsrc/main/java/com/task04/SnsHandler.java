package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RegionScope;
import com.syndicate.deployment.model.RetentionSetting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@LambdaHandler(lambdaName = "sns_handler",
	roleName = "sns_handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SnsEventSource(regionScope = RegionScope.DEFAULT, targetTopic = "lambda_topic")
public class SnsHandler implements RequestHandler<SNSEvent, Boolean> {

    LambdaLogger logger;

	public Boolean handleRequest(SNSEvent event, Context context) {
        logger = context.getLogger();
        List<SNSEvent.SNSRecord> records = event.getRecords();
        if (!records.isEmpty()) {
            Iterator<SNSEvent.SNSRecord> recordsIter = records.iterator();
            while (recordsIter.hasNext()) {
                processRecord(recordsIter.next());
            }
        }
        return Boolean.TRUE;
	}

    public void processRecord(SNSEvent.SNSRecord record) {
        try {
            String message = record.getSNS().getMessage();
            logger.log("message: " + message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
