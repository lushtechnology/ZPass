package com.lushtechnology.zpass.xrp;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class XrpHttpWrapper  {

    private static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private static final String XRP_URL = "https://s.altnet.rippletest.net:51234/";
    OkHttpClient client = new OkHttpClient();

    public long getAccountInfo(String address) {

        ObjectMapper mapper = new ObjectMapper();
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
            System.out.println(json);

            RequestBody body = RequestBody.create(json, JSON);
            Request request = new Request.Builder()
                    .url(XRP_URL)
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String res = response.body().string();
                ObjectMapper reader = new ObjectMapper();
                JsonNode rootResponse = reader.readTree(res);
                JsonNode account_data = rootResponse.path("result").path("account_data");
                long value = account_data.get("Balance").asLong();
                return value;
            }
        } catch(IOException iox) {
            iox.printStackTrace();

            return -1;
        }
    }
}
