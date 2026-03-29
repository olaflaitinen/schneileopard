# Contributing to Schnéileopard

Thank you for considering contributing to Schnéileopard. This document provides guidelines and instructions for contributing to the project.

## Getting Started

1. Fork the repository on GitHub.
2. Clone your fork locally:
   ```bash
   git clone https://github.com/olaflaitinen/schneileopard.git
   cd schneileopard
   ```
3. Create a new branch for your work:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## Development Setup

Requirements:
- JDK 11 or later (JDK 17+ recommended)
- sbt 1.9.0 or later

Build and test:
```bash
sbt clean compile test
```

Code formatting:
```bash
sbt scalafmt
```

Running examples:
```bash
sbt "examples / run"
```

## Contribution Guidelines

### Code Style

- Follow Scala conventions and idioms. Prefer idiomatic Scala over Java-style imperative patterns.
- Format code using scalafmt. The build enforces this automatically.
- Keep lines to 100 characters maximum.
- Write meaningful variable and function names.
- Avoid abbreviations unless they are standard biomedical terms.

### Documentation

- Add Scaladoc comments to all public types, methods, and significant implementations.
- Scaladoc should be publication-ready and suitable for a scientific audience.
- Include usage examples in Scaladoc where helpful.
- Explain domain-specific terminology clearly.
- Update this file and CHANGELOG.md with your changes.

### Testing

- Write tests for all new functionality.
- Maintain or improve test coverage.
- Tests should be deterministic and not depend on external services.
- Use property-based tests where they add value.
- Include both positive and negative test cases.
- Test failure modes and error handling.

### API Design

- Prefer immutable data structures for public APIs.
- Use sealed trait hierarchies and case classes for sum types.
- Provide total functions where possible. For partial functions, document the precondition clearly.
- Use type parameters to encode constraints at compile time.
- Prefer explicit over implicit when it improves clarity.
- Keep the public API surface minimal and stable.

### Commits

- Write clear, descriptive commit messages.
- Reference related issues: "Fixes #123", "Addresses #456".
- Keep commits focused on a single concern.
- Avoid commits with significant unrelated changes.

### Pull Requests

- Write a clear description of the changes and motivation.
- Reference related issues.
- Include tests that demonstrate the fix or feature.
- Ensure all checks pass before requesting review.
- Keep pull requests focused. One feature or fix per PR when possible.

## Reporting Issues

- Use GitHub Issues to report bugs or suggest features.
- Check if the issue already exists before filing.
- For bugs, include reproducible steps and your environment (OS, JDK version, etc.).
- For features, explain the use case and motivation.
- Use clear, descriptive titles.

## Release Process

Releases are coordinated by project maintainers. The process follows semantic versioning as defined in VERSIONING.md.

## Code of Conduct

This project adheres to the Contributor Covenant code of conduct. By contributing, you are expected to uphold this code. See CODE_OF_CONDUCT.md.

## License

By contributing to Schnéileopard, you agree that your contributions will be licensed under the EUPL 1.2 license.

## Questions

For questions about contributing, feel free to open an issue or contact the maintainers.

Thank you for contributing to Schnéileopard.

---

*Contributing to Schnéileopard - Last Updated: March 2026*
*Next Review: June 2026*
