package com.lushtechnology.zpass.xrp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.xrpl.xrpl4j.model.transactions.Address;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class XrpHttpWrapper {

    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private static final String XRP_URL = "https://s.altnet.rippletest.net:51234/";
    OkHttpClient client = new OkHttpClient();
    ObjectMapper mapper = new ObjectMapper();

    public XrpHttpWrapper() {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    public long getAccountInfo(String address) {

        System.out.println();
        ObjectNode rootNode = mapper.createObjectNode();

        rootNode.put("method", "account_info");

        ArrayNode params = mapper.createArrayNode();
        params.addObject().
                put("account", address).
                put("strict", true).
                put("ledger_index", "current");
        rootNode.put("params", params);

        String json = null;
        try {
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

            String res = executeRequest(json);
            JsonNode rootResponse = mapper.readTree(res);
            JsonNode account_data = rootResponse.path("result").path("account_data");
            long value = account_data.get("Balance").asLong();
            return value;

        } catch (JsonProcessingException jpex) {
            jpex.printStackTrace();
        }

        return 0;
    }

    private String executeRequest(String json) {

        try {
            //String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(XRP_URL)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();

            return response.body().string();

        } catch (JsonProcessingException jpex) {
            jpex.printStackTrace();
        } catch (IOException iox) {
            iox.printStackTrace();
        }

        return null;
    }

    public void pay(String seed, String receiver, long amount) {
        SendXrp xrp = new SendXrp();

        try {
            xrp.send(seed, receiver, amount);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
