package com.example.service;

public interface SparkAiService {
    String chat(String systemPrompt, String userPrompt);
    boolean isAvailable();
}
