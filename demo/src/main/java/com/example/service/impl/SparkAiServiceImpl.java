package com.example.service.impl;

import com.example.config.SparkProperties;
import com.example.service.SparkAiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SparkAiServiceImpl implements SparkAiService {

    @Autowired
    private SparkProperties sparkProperties;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Override
    public boolean isAvailable() {
        return sparkProperties.isEnabled()
                && StringUtils.hasText(sparkProperties.getApiPassword());
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        if (!isAvailable()) {
            throw new IllegalStateException("未配置讯飞星火 APIPassword，请在 application.yaml 中设置 spark.api-password");
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", sparkProperties.getModel());
            body.put("stream", false);
            body.put("max_tokens", 2048);
            body.put("temperature", 0.3);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", userPrompt)
            ));

            String json = objectMapper.writeValueAsString(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(sparkProperties.getApiUrl()))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + sparkProperties.getApiPassword())
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() != 200) {
                throw new RuntimeException("星火 API 调用失败(" + response.statusCode() + "): " + response.body());
            }

            JsonNode root = objectMapper.readTree(response.body());
            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
            if (contentNode.isMissingNode() || !StringUtils.hasText(contentNode.asText())) {
                throw new RuntimeException("星火 API 返回内容为空");
            }
            return contentNode.asText().trim();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("调用讯飞星火失败: " + e.getMessage(), e);
        }
    }
}
