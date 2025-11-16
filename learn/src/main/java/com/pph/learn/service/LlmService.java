package com.pph.learn.service;

import com.pph.learn.api.GenerateRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class LlmService {

    private final WebClient webClient;

    // Default to a valid Gemini 2.5 Pro endpoint. Users can override via application.properties.
    @Value("${llm.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent}")
    private String apiUrl;

    // Default model to Gemini 2.5 Pro. Can be overridden.
    @Value("${llm.model:gemini-2.5-pro}")
    private String model;

    // Provider: gemini (default) or openai
    @Value("${llm.provider:gemini}")
    private String provider;

    @Value("${llm.request.timeout-seconds:60}")
    private long timeoutSeconds;

    public LlmService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Build a prompt from the user's inputs using the sample SYSTEM + USER block described earlier.
     */
    public String buildPrompt(GenerateRequest req) {
        String topic = req.getTopic() == null ? "<CHỦ_ĐỀ>" : req.getTopic();
        String language = req.getLanguage() == null ? "việt" : req.getLanguage();
        String level = req.getLevel() == null ? "BEGINNER" : req.getLevel();

        String systemPrompt = """
        SYSTEM PROMPT:
        You are an expert meta-teacher and curriculum designer with decades of practical experience teaching beginners to become masters across disciplines (technical, artistic, language, soft skills). For any given <CHỦ_ĐỀ>, produce a single, self-contained learning package that takes a learner from zero to master level. The final output must be extremely detailed and at least 10,000 words long. Be concise where useful, exhaustive where necessary, and always practical: include actionable steps, measurable milestones, exercises, projects, tools, common pitfalls, assessment criteria, and references. Use plain language, short examples, and prioritize comprehension for learners with busy schedules. When giving timelines, provide multiple paced tracks (intensive, moderate, slow). When recommending resources, prefer up-to-date, reputable sources but do not paste long copyrighted text. Assume learner has internet access and basic computer literacy unless otherwise stated.
        """;

        String userPrompt = String.format("""
        USER PROMPT:
        Chủ đề/kỹ năng: %s
        Trình độ hiện tại: %s
        Ngôn ngữ giảng: %s

        YÊU CẦU CỤ THỂ TỪ MÔ HÌNH (bắt buộc):
        Trả về một learning plan có các phần rõ ràng:
        1.  Tóm tắt mục tiêu (1-2 câu)
        2.  Yêu cầu nhập môn (prereqs) — nếu không cần, ghi “Không cần”
        3.  Chuỗi chủ đề theo thứ tự. **Đối với mỗi chủ đề, hãy giải thích chi tiết theo cấu trúc "Cái gì?" (What), "Tại sao?" (Why), và "Làm thế nào?" (How).**
        4.  Bài tập ngắn hàng ngày/tuần + đề bài thực hành có đáp án hoặc hướng giải ngắn gọn
        5.  Ít nhất 2 dự án thực tế (một dự án nhỏ để thực hành, một dự án lớn để portfolio) với milestones và tiêu chí chấm điểm
        6.  Kế hoạch ôn tập & spaced repetition (gợi ý flashcards / SRS)
        7.  Cách tự đánh giá (quizzes, rubrics, chứng chỉ mô phỏng)
        8.  Danh sách tài nguyên (sách, khóa học, docs, công cụ) phân loại: miễn phí / trả phí / chính thức
        9.  Những lỗi phổ biến & cách né tránh
        10. Cheat sheet / công cụ tổng hợp (từ khóa, câu lệnh, công thức)
        11. Cho ít nhất 3 track tốc độ (INTENSIVE / MODERATE / SLOW) với thời gian cụ thể và lịch mẫu.
        12. Mỗi chủ đề phải kèm “Bài kiểm tra ngắn” (3 câu hỏi tự luận/trắc nghiệm) và tiêu chí đạt pass.
        13. **Sử dụng ngôn ngữ cực kỳ dễ hiểu, giải thích mọi thuật ngữ phức tạp như đang nói chuyện với người mới bắt đầu.**
        14. Nếu một phần cần hành động ngoài mạng, ghi rõ cách kiểm tra tiến độ offline.
        15. Cuối cùng, trả về một phiên bản rút gọn 1-page (checklist) để in.
        END
        """, topic, level, language);

        return systemPrompt + "\n" + userPrompt;
    }

    public String getProviderName(){
        return provider == null ? "openai" : provider;
    }

    /**
     * Sanitize model output: remove accidental echo of SYSTEM/USER prompt blocks and trim.
     */
    public String sanitizeModelOutput(String output) {
        if (output == null) return null;
        String s = output;
        // Remove SYSTEM: / USER: blocks if model echoed them
        s = s.replaceAll("(?s)SYSTEM:.*?USER:", "");
        // Remove leading role tags like 'User:' or 'System:'
        s = s.replaceAll("(?m)^(SYSTEM:|USER:).*$", "");
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    /**
     * Call an OpenAI-style chat completions API using the provided apiKey.
     * The method assumes the endpoint accepts the OpenAI Chat Completions JSON shape.
     * For Gemini users: set llm.api.url to the Gemini/Vertex endpoint and ensure the header requirements match.
     */
    public String callLlm(String apiKey, GenerateRequest req) throws Exception {
        String prompt = buildPrompt(req);

        // Support two provider shapes: openai-style chat completions and a generic Gemini/Vertex-like endpoint.
        if ("gemini".equalsIgnoreCase(provider)) {
            // Prefer a Vertex/Generative Language style request shape but allow other Gemini-compatible endpoints.
            var body = new java.util.HashMap<String, Object>();

            // The generateContent endpoint expects a `contents` array.
            var parts = new java.util.ArrayList<java.util.Map<String, String>>();
            parts.add(java.util.Map.of("text", prompt));
            var contents = new java.util.ArrayList<java.util.Map<String, Object>>();
            contents.add(java.util.Map.of("parts", parts));
            body.put("contents", contents);

            // Generation config - no token limit to get full response
            var generationConfig = new java.util.HashMap<String, Object>();
            generationConfig.put("temperature", 0.2);
            // Removing maxOutputTokens to get the full response from the model
            // generationConfig.put("maxOutputTokens", 8192);
            body.put("generationConfig", generationConfig);

            var responseMono = webClient.post()
                    .uri(apiUrl)
                    // Use x-goog-api-key header, which is common for Google Cloud API keys.
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(java.util.Map.class)
                    .timeout(Duration.ofSeconds(timeoutSeconds));

            var map = responseMono.block();
            if (map == null) throw new IllegalStateException("Empty response from LLM (gemini)");

            // Robust parsing for several Gemini/Vertex response shapes.
            // 1) { "candidates": [ { "content": "..." } ] }
            Object candidatesObj = map.get("candidates");
            if (candidatesObj instanceof java.util.List<?> candidatesList && !candidatesList.isEmpty()) {
                Object first = candidatesList.get(0);
                if (first instanceof java.util.Map<?, ?> fmap) {
                    // candidate.content might be a string
                    Object content = fmap.get("content");
                    if (content instanceof String) return (String) content;
                    // candidate may contain an 'output' or nested 'content' object
                    if (content instanceof java.util.Map<?, ?> contentMap) {
                        // Handle new structure: content -> parts -> text
                        Object partsObj = contentMap.get("parts");
                        if (partsObj instanceof java.util.List<?> partsList && !partsList.isEmpty()) {
                            Object firstPart = partsList.get(0);
                            if (firstPart instanceof java.util.Map<?, ?> partMap) {
                                Object text = partMap.get("text");
                                if (text instanceof String) return (String) text;
                            }
                        }
                        Object text = contentMap.get("text");
                        if (text instanceof String) return (String) text;
                    }
                    if (fmap.get("output") != null) return fmap.get("output").toString();
                    if (fmap.get("text") != null) return fmap.get("text").toString();
                }
            }

            // 2) { "output": { "content": "..." } } or { "output": "..." }
            if (map.containsKey("output")) {
                Object out = map.get("output");
                if (out instanceof String) return (String) out;
                if (out instanceof java.util.Map<?, ?> outMap) {
                    if (outMap.get("content") instanceof String) return outMap.get("content").toString();
                    if (outMap.get("text") instanceof String) return outMap.get("text").toString();
                    // Some shapes: output = { "candidates": [...] }
                    Object nestedCandidates = outMap.get("candidates");
                    if (nestedCandidates instanceof java.util.List<?> ncList && !ncList.isEmpty()) {
                        Object first = ncList.get(0);
                        if (first instanceof java.util.Map<?, ?> fmap && fmap.get("content") != null) return fmap.get("content").toString();
                    }
                }
            }

            // 3) Top-level 'candidates' already handled; fallback try 'text' or 'generated_text'
            if (map.containsKey("text")) return map.get("text").toString();
            if (map.containsKey("generated_text")) return map.get("generated_text").toString();

            throw new IllegalStateException("Could not parse Gemini response: " + map);
    }
    // If provider is not gemini, throw explicit error (should never happen with current config)
    throw new IllegalStateException("Provider not supported or failed to parse Gemini response.");
    }
}
