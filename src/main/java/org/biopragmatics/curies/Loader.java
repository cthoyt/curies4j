package org.biopragmatics.curies;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Loader {
    public static List<Record> getRecords(String url) throws IOException {
        JsonElement element = urlToJson(url);
        return getRecords(element);
    }

    public static List<Record> getRecords(JsonElement element) {
        return new ArrayList<Record>() {{
            for (JsonElement subelement : element.getAsJsonArray())
                add(recordFromJsonElement(subelement));
        }};
    }

    public static List<Record> getRecords(File file) throws FileNotFoundException {
        JsonReader reader = new JsonReader(new FileReader(file));
        JsonElement element = JsonParser.parseReader(reader);
        return getRecords(element);
    }

    private static Record recordFromJsonElement(JsonElement element) {
        JsonObject obj = element.getAsJsonObject();
        return new Record(
                obj.get("prefix").getAsString(),
                obj.get("uri_prefix").getAsString(),
                getList(obj, "prefix_synonyms"),
                getList(obj, "uri_prefix_synonyms")
        );
    }

    private static List<String> getList(JsonObject jsonObject, String key) {
        List<String> rv = new ArrayList<>();
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement == null)
            return rv;
        JsonArray jsonArray = jsonElement.getAsJsonArray();
        for (JsonElement element : jsonArray.asList())
            rv.add(element.getAsString());
        return rv;
    }

    private static JsonElement urlToJson(String url) throws IOException {
        // code inspired by james mclaughlin in OLS4
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .setSocketTimeout(5000).build();
        CloseableHttpClient client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        HttpEntity entity = response.getEntity();
        if (entity == null)
            throw new RuntimeException("response was null");
        return JsonParser.parseReader(new InputStreamReader(entity.getContent()));
    }
}
