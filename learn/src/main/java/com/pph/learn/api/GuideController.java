package com.pph.learn.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pph.learn.service.ExportService;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class GuideController {

    @Autowired
    private ExportService exportService;

    @Autowired
    private com.pph.learn.service.LlmService llmService;

    @PostMapping("/generate")
    public GenerateResponse generate(@jakarta.validation.Valid @RequestBody GenerateRequest req) {
        // If apiKey provided, try calling external LLM (OpenAI-style / Gemini if endpoint compatible).
        String md;
        boolean usedLlm = false;
        String provider = null;
        String errorMessage = null;
        md = null;

        if (req.getApiKey() == null || req.getApiKey().isBlank()) {
            // Per requirement: do NOT return fallback generator when API key missing. Inform client.
            errorMessage = "No API key provided. To receive AI-generated content, supply a valid API key.";
            GenerateResponse resp = new GenerateResponse(null, false, null);
            resp.setErrorMessage(errorMessage);
            return resp;
        }

        try {
            String raw = llmService.callLlm(req.getApiKey(), req);
            String sanitized = llmService.sanitizeModelOutput(raw);
            if (sanitized == null || sanitized.length() < 50) {
                errorMessage = "LLM returned no usable content.";
            } else {
                md = sanitized;
                usedLlm = true;
                provider = llmService.getProviderName();
            }
        } catch (Exception ex) {
            errorMessage = "LLM call failed: " + ex.getMessage();
        }

        GenerateResponse resp = new GenerateResponse(md, usedLlm, provider);
        resp.setErrorMessage(errorMessage);
        return resp;
    }

    @PostMapping("/export/docx")
    public ResponseEntity<byte[]> exportDocx(@RequestBody GenerateResponse body) throws Exception {
        byte[] docx = exportService.markdownToDocx(body.getMarkdown());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guide.docx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(docx);
    }
}
