# Security Policy

## Supported Versions

| Version | Supported |
| ------- | --------- |
| 0.1.x   | Yes       |

## Reporting a Vulnerability

Please report security vulnerabilities privately via GitHub Security Advisories on this repository, or by opening a minimal reproduction issue tagged `security` if advisories are unavailable.

Do not disclose critical vulnerabilities in public issues before a fix is available.

## Security Features in This Template

- Optional JWT authentication (`spring.security.enabled=true`)
- OWASP dependency-check in Maven verify phase
- SpotBugs static analysis in verify phase
- Non-root Docker user in `Dockerfile`
