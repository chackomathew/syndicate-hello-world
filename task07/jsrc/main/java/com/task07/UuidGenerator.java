package com.task07;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@LambdaHandler(lambdaName = "uuid_generator",
        roleName = "uuid_generator-role",
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@RuleEventSource(targetRule = "uuid_trigger")
public class UuidGenerator implements RequestHandler<Object, String> {

    public String handleRequest(Object request, Context context) {
        context.getLogger().log("Generate file in S3 using cloudwatch!");

        List<String> uuids = generateUUIDs(10);
        String fileName = generateFileName();
        String fileContent = generateFileContent(uuids);

        uploadFileToS3(fileName, fileContent);

        return "File saved to S3: " + fileName;
    }

    private List<String> generateUUIDs(int count) {
        List<String> uuids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            uuids.add(UUID.randomUUID().toString());
        }
        return uuids;
    }

    private String generateFileName() {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return currentTime.format(formatter);
    }

    private String generateFileContent(List<String> uuids) {
        JsonObject jsonObject = new JsonObject();
        JsonArray idsArray = new JsonArray();

        uuids.forEach(uuid -> idsArray.add(uuid));
        jsonObject.add("ids", idsArray);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    private void uploadFileToS3(String fileName, String fileContent) {
        AmazonS3 s3Client = new AmazonS3Client();
        s3Client.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));

        InputStream inputStream = new ByteArrayInputStream(fileContent.getBytes());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileContent.getBytes().length);
        s3Client.putObject("cmtr-21c6166e-uuid-storage-test", fileName, inputStream, metadata);
    }

}
