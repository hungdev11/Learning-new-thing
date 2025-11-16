package com.pph.learn.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class GenerateRequest {
    private String apiKey; // optional

    @NotBlank(message = "topic is required")
    private String topic; // <CHỦ_ĐỀ>

    // Allowed values: BEGINNER, INTERMEDIATE, ADVANCED
    @Pattern(regexp = "^(BEGINNER|INTERMEDIATE|ADVANCED)$", message = "level must be BEGINNER|INTERMEDIATE|ADVANCED")
    private String level = "BEGINNER";

    // Language: việt|english|mix
    @Pattern(regexp = "^(việt|english|mix)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "language must be việt|english|mix")
    private String language = "việt";

    private String format; // desired sections format (optional)

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
}
