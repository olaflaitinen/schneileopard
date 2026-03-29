# ADR-001: Validation Pattern

## Status

Accepted

## Context

Schnéileopard processes biomedical data that may be incomplete, malformed, or inconsistent. The library needs a consistent pattern for representing validation failures that allows callers to handle errors without exceptions.

## Decision

We use a custom `Validation[A]` enum that represents either a `Valid(A)` or `Invalid(DomainError)`. This is similar to `Either` but with domain-specific error semantics.

Key design points:

1. `Validation` is composable via `map`, `flatMap`, and `fold` operations
2. `DomainError` is a sealed trait with subtypes for different error categories (ValidationError, ParseError, TypeMismatchError, etc.)
3. Public APIs return `Validation[T]` rather than throwing exceptions for expected failures
4. The `Validation.sequence` function allows accumulating results across multiple validations

## Rationale

This approach provides several benefits:

- Type safety: Invalid states are represented in the type system
- Composability: Validation chains can be built using monadic operations
- Domain clarity: Error types reflect the actual domain problem, not generic exceptions
- Functional purity: No hidden side effects from exception throwing

## Alternatives Considered

1. Throwing exceptions: Simpler for users, but loses type safety and is more implicit
2. Using standard `Either[Throwable, A]`: Works but mixes technical and domain errors
3. Using validated accumulation libraries like Cats: Too heavyweight for core module

## Consequences

1. Users must handle `Validation` results explicitly
2. Public API documentation must explain the pattern clearly
3. Convenience helpers may be needed for common composition patterns
4. Error handling is predictable and testable

## Related Decisions

- See ADR-002 for module structure that uses this validation pattern
- See DESIGN.md for overall philosophy on error handling

## See Also

- [TROUBLESHOOTING.md](../TROUBLESHOOTING.md) for common errors
- [docs/GETTING_STARTED.md](GETTING_STARTED.md) for usage examples

---

*ADR-001: Validation Pattern - Last Updated: March 2026*
*Next Review: June 2026*
