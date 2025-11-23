package com.balievent.telegrambot.scrapper.client;

import com.balievent.telegrambot.scrapper.configuration.TheBeatBaliProperties;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
@RequiredArgsConstructor
public class TheBeatBaliClient {

    private final TheBeatBaliProperties properties;
    private final HttpClient httpClient = HttpClient.newBuilder()
        .build();

    private static String buildRequestBody(String nonce,
                                           String date,
                                           int page,
                                           boolean loadMore) {
        return "action=" + URLEncoder.encode("tbe_get_date_events", UTF_8)
            + "&nonce=" + URLEncoder.encode(nonce, UTF_8)
            + "&date=" + URLEncoder.encode(date, UTF_8)
            + "&show_past=yes"
            + "&page=" + page
            + "&load_more=" + loadMore
            + "&filters%5Bcategory%5D="
            + "&filters%5Bvenue%5D="
            + "&filters%5Blocation%5D="
            + "&filters%5Bshow_past%5D=yes";
    }

    @SneakyThrows
    public String loadEventsJson(String date,
                                 String nonce,
                                 int page,
                                 boolean loadMore) {
        final var body = buildRequestBody(nonce, date, page, loadMore);

        final var request = HttpRequest.newBuilder()
            .uri(URI.create(properties.getBaseUrl() + "/wp-admin/admin-ajax.php"))
            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .header("Accept", "application/json, */*")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();

        final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Bad status: " + response.statusCode());
        }

        return response.body();
    }
}


