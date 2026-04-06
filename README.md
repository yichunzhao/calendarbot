# Calendar Bot

AI-powered appointment assistant built with Spring Boot and Spring AI.
You can type natural language to:
- book appointments,
- list a user's appointments,
- cancel appointments,
- introduce yourself once and let the assistant remember your name/contact during the current conversation.

## Features
- Natural language chat via REST `POST /chat`
- Natural language interactive CLI mode (`cli` profile)
- LLM intent extraction for `BOOK`, `LIST`, `DELETE`, and `IDENTIFY`
- Spring AI conversation memory via `MessageChatMemoryAdvisor`
- BOM-based dependency alignment for Spring AI and Jackson
- One conversation context per CLI session
- Stable web conversation context via `X-Conversation-Id` or HTTP session fallback
- In-memory appointment storage (resets on restart)
- In-memory conversation memory (resets on restart)
- Swagger/OpenAPI documentation

## Tech Stack
- Java 21
- Spring Boot 3.5.x
- Spring AI 1.1.4 (OpenAI starter, managed via Spring AI BOM)
- Springdoc OpenAPI + Swagger UI
- Lombok

## Prerequisites
- JDK 21
- Maven 3.9+
- OpenAI API key

## Quick Start
1) Open the project.

2) Set API key (PowerShell):
```powershell
$env:OPENAI_API_KEY="sk-your-key"
```

3) Run the app.

REST mode (default):
```powershell
mvn spring-boot:run
```

CLI mode from PowerShell:
```powershell
$env:SPRING_PROFILES_ACTIVE="cli"
mvn spring-boot:run
```

CLI mode from Command Prompt (cmd.exe):
```bat
set SPRING_PROFILES_ACTIVE=cli
mvn spring-boot:run
```

Packaged jar in CLI mode:
```powershell
mvn clean package -DskipTests
java -jar target/calendarbot-1.0-SNAPSHOT.jar --spring.profiles.active=cli
```

## Natural Language Intents
The app supports these intents through both REST and CLI:

- `BOOK`: create an appointment
  - provide service + date + time
  - name/contact can come from the current message or remembered conversation context
- `LIST`: list current user's appointments
  - provide name and/or contact, either directly or from remembered context
- `DELETE`: cancel one appointment
  - provide date + time and enough identity information to match the booking
- `IDENTIFY`: introduce or correct your name/contact for the current conversation
  - examples: `I am Yichun`, `My email is yichun@mail.com`

Conversation memory is scoped to the current conversation only:

- **CLI**: one memory window per REPL session
- **REST**: one memory window per `X-Conversation-Id` value, or per HTTP session if that header is omitted
- Memory is **not persisted** across application restarts

## CLI Examples
```text
calendarbot:> I am Yichun
Thanks, Yichun. I'll remember you for this conversation.

calendarbot:> book a cleaning for me on 2026-04-10 at 10:00
Your appointment is scheduled for 2026-04-10 at 10:00

calendarbot:> list my appointments
Appointments for Yichun:
1) 2026-04-10 10:00 - Cleaning

calendarbot:> cancel my appointment on 2026-04-10 at 10:00
Your appointment on 2026-04-10 at 10:00 has been cancelled.
```

Exit CLI with:
- `exit`
- `quit`

## REST API
### Endpoint
- `POST /chat`
- `Content-Type: text/plain`
- Body: any natural language message

### Conversation context
- Optional request header: `X-Conversation-Id`
- If provided, the same header value should be reused on follow-up requests so the assistant remembers earlier messages
- If omitted, the app falls back to the current HTTP session id
- The response echoes the effective `X-Conversation-Id` header

Single request example:
```powershell
curl -X POST http://localhost:8080/chat -H "Content-Type: text/plain" -d "list appointments for alice@mail.com"
```

Multi-turn example with remembered identity:
```powershell
$conversationId = "demo-yichun"

curl -X POST http://localhost:8080/chat `
  -H "Content-Type: text/plain" `
  -H "X-Conversation-Id: $conversationId" `
  -d "I am Yichun"

curl -X POST http://localhost:8080/chat `
  -H "Content-Type: text/plain" `
  -H "X-Conversation-Id: $conversationId" `
  -d "list my appointments"
```

## Configuration
`src/main/resources/application.properties`:
- `server.port=8080`
- `spring.ai.openai.api-key=${OPENAI_API_KEY}`
- `spring.ai.openai.base-url=https://api.openai.com`
- `spring.ai.openai.chat.options.model=gpt-4o`

Change model example:
```properties
spring.ai.openai.chat.options.model=gpt-4o-mini
```

### Dependency Version Management
- `org.springframework.ai:spring-ai-bom` is imported in `pom.xml` and controlled by `spring-ai.version` (`1.1.4`).
- `com.fasterxml.jackson:jackson-bom` is also imported and controlled by `jackson.version` (`2.21.1`).
- `spring-ai-starter-model-openai` has no explicit `<version>` in dependencies because it is managed by the Spring AI BOM.

## Swagger/OpenAPI
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Architecture
- `CalendarBot` - Spring Boot entry point
- `AiConfig` - configures `ChatMemory` and the memory-enabled `ChatClient`
- `ChatController` - REST endpoint for `/chat` (non-`cli` profile), including conversation-id handling
- `CliRunner` - terminal REPL in `cli` profile; reuses one conversation id for the full session
- `ChatService` - LLM prompt + intent routing (`BOOK`/`LIST`/`DELETE`/`IDENTIFY`)
- `AppointmentService` - booking/listing/cancel contract
- `InMemoryAppointmentService` - in-memory implementation
- `AppointmentRequest` - DTO used for extraction and scheduling data

## Development
```powershell
mvn clean package
mvn test
mvn org.owasp:dependency-check-maven:check
```

## Troubleshooting
- If you see `No command found for '--spring.profiles.active=cli'`, set profile via environment variable before `mvn spring-boot:run`.
- Appointments are in-memory only and reset after restart.
- Conversation memory is also in-memory only and resets after restart or when you start a new CLI process with a fresh conversation id.
- For REST clients, reuse the same `X-Conversation-Id` across related requests if you want the assistant to remember prior turns.
- Ensure Maven and IDE both use Java 21.

## License
This project is licensed under the MIT License. See `LICENSE`.
