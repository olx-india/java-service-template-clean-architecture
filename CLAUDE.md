# Claude Code

Use **[AGENTS.md](AGENTS.md)** as the primary instruction set for this template.

## Claude-specific tips

- **Explore before editing:** Read [docs/clean-architecture.md](docs/clean-architecture.md) and the nearest existing feature (e.g. `usecase/users/`, `controller/UserController.java`) and mirror that structure.
- **Verify:** After changes, run `./mvnw -s settings.xml test -DskipIntegration=true` or `make test`. For integration work, run `make it` (Docker required; Testcontainers 1.21.4+ for Docker Engine 29).
- **Large tasks:** Split by layer (domain → use case → infrastructure → controller → tests) and land one vertical slice at a time.
- **Docs:** User-facing docs go in `docs/`. Template change log → [REPO_CHANGELOG.md](REPO_CHANGELOG.md) only.
- **Questions:** Prefer citing paths like `src/main/java/com/olx/boilerplate/usecase/users/CreateUser.java` over paraphrasing structure.

## Quick reference

Same commands and layer rules as [AGENTS.md](AGENTS.md). Extended workflows and prompt patterns: [docs/ai-agents.md](docs/ai-agents.md).
