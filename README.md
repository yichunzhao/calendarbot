# Calendar Bot

AI-powered appointment scheduling bot built with Spring Boot and Spring AI. Users send natural language messages (e.g., "Book a cleaning for John on 2025-11-20 at 14:30"), and the bot extracts structured details, checks availability, and books the slot using an in-memory store.

## Features
- Natural language appointment requests via POST /chat REST endpoint
- Interactive terminal mode with natural language input (no command syntax needed)
- LLM-powered extraction of client name, contact, service, date, and time
- Simple in-memory availability checking and booking
- OpenAPI/Swagger UI for easy exploration

## Tech Stack
- Java 21, Spring Boot 3.5
- Spring AI (OpenAI) 1.0.3
- Springdoc OpenAPI 2.x, Swagger UI
- Lombok

## Prerequisites
- JDK 21 (enforced by Maven Enforcer)
- Maven 3.9+
- OpenAI API key with access to the configured model

## Quick Start
1) Clone/open the project in IntelliJ IDEA or your preferred IDE.

2) Set your OpenAI API key as an environment variable.
   - Windows PowerShell:
     ```powershell
     $env:OPENAI_API_KEY="sk-your-key"
     ```

3) Run the application (choose one):
   - REST API mode (default):
     ```bash
     mvn spring-boot:run
     ```
   - CLI mode (interactive natural language):
     ```bash
     set SPRING_PROFILES_ACTIVE=cli
     mvn spring-boot:run
     ```
   - Packaged jar with CLI profile:
     ```bash
     mvn clean package -DskipTests
     java -jar target/calendarbot-1.0-SNAPSHOT.jar --spring.profiles.active=cli
     ```

4) In REST mode, the app starts on http://localhost:8080 by default.
   In CLI mode, you'll see an interactive prompt.

## Configuration
Configured in `src/main/resources/application.properties`:
- `server.port=8080` — change to customize port
- `spring.ai.openai.api-key=${OPENAI_API_KEY}` — set via env var
- `spring.ai.openai.base-url=https://api.openai.com`
- `spring.ai.openai.chat.options.model=gpt-4o` — change model if needed

Example: to use a different model
```properties
spring.ai.openai.chat.options.model=gpt-4o-mini
```

## API Reference
- POST `/chat`
  - Content-Type: `text/plain`
  - Body: natural language request
  - Response: `200 OK` with a plain-text confirmation or a prompt to choose another time

Example request:
```bash
curl -X POST http://localhost:8080/chat \
  -H "Content-Type: text/plain" \
  -d "Please book a dental cleaning for Alice Smith on 2025-12-01 at 10:30. Contact 555-0199."
```

Example response:
```
✅ Your appointment is scheduled for 2025-12-01 at 10:30
```

If the slot is taken:
```
⚠️ That slot is not available. Please choose another time.
```

## CLI Reference
Start the app in CLI mode and interact with natural language directly:

```bash
set SPRING_PROFILES_ACTIVE=cli
mvn spring-boot:run
```

Example interaction:
```
╔══════════════════════════════════════════╗
║      🦷  CalendarBot  CLI  v1.0          ║
║  Just type your request naturally.       ║
║  Type 'exit' to quit.                    ║
╚══════════════════════════════════════════╝

calendarbot:> book a dental cleaning for Alice on 2026-04-10 at 10:00
⏳ Thinking...
✅ Your appointment is scheduled for 2026-04-10 at 10:00

calendarbot:> make an appointment tomorrow at 15:00 for John
⏳ Thinking...
✅ Your appointment is scheduled for 2026-04-05 at 15:00

calendarbot:> exit
Goodbye! 👋
```

Commands:
- Type any natural language appointment request
- Type `exit` or `quit` to close

## Swagger/OpenAPI
After starting the app, open:
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## How it Works (Architecture)
- `AiConfig` — wires Spring AI `ChatClient` with a system prompt for a dental appointment assistant.
- `ChatController` — exposes POST `/chat` endpoint for REST API interaction.
- `CliRunner` — manages interactive terminal mode with natural language input in `cli` profile.
- `ChatService` — prompts the LLM to extract an `AppointmentRequest` (clientName, clientContact, service, date, time), checks availability, and books the slot.
- `AppointmentService` — abstraction for availability and booking.
- `InMemoryAppointmentService` — stores booked times per date in memory (resets on restart).
- `AppointmentRequest` — DTO with validation and date/time formats.
- `CalendarBot` — Spring Boot entry point.

## Development
- Build: `mvn clean package`
- Run dependency check: `mvn org.owasp:dependency-check-maven:check`
- IDE: Import as a Maven project; enable annotation processing for Lombok.

## Known Notes / Troubleshooting
- Java version: Build enforces Java 21. Ensure your IDE and Maven use JDK 21.
- OpenAI credentials: A 401 usually means an invalid or missing API key. Ensure `OPENAI_API_KEY` is set in your environment.
- Rate limits: A 429 indicates rate limiting by the model provider.
- Main class mismatch: The Maven plugin config references `com.ynz.ai.calendarbot.CalendarBotApplication`, but the actual main class is `com.ynz.ai.calendarbot.CalendarBot`. If a shaded jar fails to start, either run with `mvn spring-boot:run` or update the plugin `mainClass` to `com.ynz.ai.calendarbot.CalendarBot`.
- Persistence: Appointments are in-memory only and reset on application restart.

## Roadmap Ideas
- Persistent storage (e.g., Postgres)
- Conflict resolution and rescheduling suggestions
- Authentication/authorization
- Richer schema extraction and validation

## License

This project is licensed under the **MIT License** — see [LICENSE](LICENSE) file for details.

To define a license for your project:

1. **Add a LICENSE file** in the root directory (e.g., `LICENSE` or `LICENSE.txt`)
   - Common choices: MIT, Apache 2.0, GPL v3, BSD 3-Clause
   - See [choosealicense.com](https://choosealicense.com) for comparisons

2. **Reference in pom.xml** (optional but recommended):
   ```xml
   <licenses>
       <license>
           <name>MIT License</name>
           <url>https://opensource.org/licenses/MIT</url>
       </license>
   </licenses>
   ```

3. **Add to README.md** (this section) with a link to your LICENSE file
