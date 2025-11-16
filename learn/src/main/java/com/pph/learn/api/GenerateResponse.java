package com.pph.learn.api;

public class GenerateResponse {
    private String markdown;
    private boolean usedLlm;
    private String provider;
    private String errorMessage;

    public GenerateResponse() {}

    public GenerateResponse(String markdown) {
        this.markdown = markdown;
    }

    public GenerateResponse(String markdown, boolean usedLlm, String provider) {
        this.markdown = markdown;
        this.usedLlm = usedLlm;
        this.provider = provider;
    }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getMarkdown() { return markdown; }
    public void setMarkdown(String markdown) { this.markdown = markdown; }
    public boolean isUsedLlm() { return usedLlm; }
    public void setUsedLlm(boolean usedLlm) { this.usedLlm = usedLlm; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}
