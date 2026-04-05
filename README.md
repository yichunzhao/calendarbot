# Calendar Bot

AI-powered appointment assistant built with Spring Boot and Spring AI.
You can type natural language to:
- book appointments,
- list a user's appointments,
- cancel appointments.

## Features
- Natural language chat via REST `POST /chat`
- Natural language interactive CLI mode (`cli` profile)
- LLM intent extraction for `BOOK`, `LIST`, and `DELETE`
- In-memory appointment storage (resets on restart)
- Swagger/OpenAPI documentation

## Tech Stack
- Java 21
- Spring Boot 3.5.x
- Spring AI 1.0.3 (OpenAI starter)
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
  - provide name/contact + service + date + time
- `LIST`: list current user's appointments
  - provide name and/or contact
- `DELETE`: cancel one appointment
  - provide name/contact + date + time

## CLI Examples
```text
calendarbot:> book a cleaning for Alice (alice@mail.com) on 2026-04-10 at 10:00
Your appointment is scheduled for 2026-04-10 at 10:00

calendarbot:> list appointments for alice@mail.com
Appointments for alice@mail.com:
1) 2026-04-10 10:00 - Cleaning

calendarbot:> cancel my appointment for alice@mail.com on 2026-04-10 at 10:00
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

Example:
```powershell
curl -X POST http://localhost:8080/chat -H "Content-Type: text/plain" -d "list appointments for alice@mail.com"
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

## Swagger/OpenAPI
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Architecture
- `CalendarBot` - Spring Boot entry point
- `ChatController` - REST endpoint for `/chat` (non-`cli` profile)
- `CliRunner` - terminal REPL in `cli` profile
- `ChatService` - LLM prompt + intent routing (`BOOK`/`LIST`/`DELETE`)
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
- Ensure Maven and IDE both use Java 21.

## License
This project is licensed under the MIT License. See `LICENSE`.
