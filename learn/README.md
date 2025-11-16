# Learning Guide Generator

This project is a Spring Boot application that uses the Google Gemini API to generate comprehensive learning guides on any topic.

## Features

-   **AI-Powered Content:** Generates detailed, structured learning guides using the Gemini 2.5 Pro model.
-   **Customizable:** Specify the topic, language, learner level, and desired pacing.
-   **Export to DOCX:** Export the generated guide to a formatted DOCX file.
-   **REST API:** Simple endpoints for easy integration.

## Getting Started

### 1. Obtain a Google Gemini API Key

To use this application, you need a valid API key from Google.

1.  **Visit Google AI Studio:** Go to [https://aistudio.google.com/](https://aistudio.google.com/).
2.  **Create an API Key:**
    *   Sign in with your Google account.
    *   Click on the "**Get API key**" button.
    *   Click "**Create API key in new project**".
    *   Copy the generated API key. **Treat this key like a password.**

### 2. Running the Application

1.  **Build the Project:**
    ```bash
    mvn clean package -DskipTests
    ```

2.  **Run the Application:**
    The application will start on port `8081`.
    ```bash
    java -jar target/learn-0.0.1-SNAPSHOT.jar --server.port=8081
    ```

### 3. Making an API Request

Use a tool like `curl` or the provided UI to interact with the API. The UI will automatically save your API key in your browser's local storage for future use.

**Endpoint:** `POST /api/generate`
**URL:** `http://localhost:8081/api/generate`

**Example `curl` command:**

Replace `YOUR_GEMINI_API_KEY` with the key you obtained from Google AI Studio.

```bash
curl -X POST http://localhost:8081/api/generate \
-H "Content-Type: application/json" \
-d '{
  "apiKey": "YOUR_GEMINI_API_KEY",
  "topic": "Learning Docker",
  "language": "English",
  "level": "BEGINNER",
  "speed": "MODERATE"
}'
```

## Configuration

You can modify the default LLM settings in `src/main/resources/application.properties`:

-   `llm.api.url`: The Gemini API endpoint.
-   `llm.model`: The specific Gemini model to use (defaults to `gemini-2.5-pro`).
-   `llm.provider`: The AI provider (defaults to `gemini`).
