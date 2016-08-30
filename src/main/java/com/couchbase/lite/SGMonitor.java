package com.couchbase.lite;

import com.couchbase.lite.util.StreamUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.couchbase.lite.Runtime.mapper;

public class SGMonitor extends SwingWorker<Void, String> {
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.DAYS)
            .build();

    private final HttpUrl.Builder urlBuilder;
    private final JTextArea textArea;

    SGMonitor(String url, JTextArea textArea) {
        urlBuilder = HttpUrl.parse(url).newBuilder()
                .addPathSegment("_changes")
                .addQueryParameter("feed", "longpoll")
                .addQueryParameter("timeout", "0");

        this.textArea = textArea;
    }

    @Override
    protected Void doInBackground() throws Exception {
        while (true) {
            Request request = new Request.Builder()
                    .url(urlBuilder.build())
                    .build();

            Response response = client.newCall(request).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            /*
            Headers responseHeaders = response.headers();

            for (int nn = 0; nn < responseHeaders.size(); ++nn) {
                publish(responseHeaders.name(nn) + ": " + responseHeaders.value(nn));
            }
            */

            String body = response.body().string();

            JsonNode tree = mapper.readTree(body);

            urlBuilder.setQueryParameter("since", tree.get("last_seq").asText());

            publish(body);
        }
    }

    @Override
    protected void process(List<String> updates) {
        textArea.setText(null);
        updates.forEach((str) -> textArea.append(str));
    }
}