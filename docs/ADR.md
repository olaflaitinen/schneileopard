# Architecture Decisions

This directory contains Architecture Decision Records (ADRs) documenting significant technical choices and their rationale.

## Contents

- [ADR-001: Validation Pattern](adr-001-validation-pattern.md)
- [ADR-002: Module Structure](adr-002-module-structure.md)
- [ADR-003: Opaque Type IDs](adr-003-opaque-type-ids.md)

## How to Read ADRs

Each ADR follows the format:
- **Status**: Accepted, Proposed, Deprecated, or Superseded
- **Context**: The issue or requirement that prompted this decision
- **Decision**: The chosen approach and key aspects
- **Rationale**: Why this decision was made
- **Alternatives**: Other options that were considered
- **Consequences**: Impact on the codebase and users

## Adding New ADRs

To propose a new architectural decision:
1. Create a file named `adr-NNN-title.md`
2. Use the standard template below
3. Submit as a pull request for discussion
4. Once accepted, update this index

### ADR Template

```markdown
# ADR-NNN: Title

## Status
Proposed / Accepted / Deprecated

## Context
[Brief description of the issue or requirement]

## Decision
[Describe the chosen approach]

## Rationale
[Explain why this was chosen]

## Alternatives Considered
[Other options that were evaluated]

## Consequences
[Impacts on the system and users]
```

## Decision Timeline

- March 2026: ADR-001, ADR-002, ADR-003 established during initial design

---

*Architecture Decisions - Last Updated: March 2026*
*Next Review: June 2026*
