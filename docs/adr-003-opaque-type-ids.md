# ADR-003: Opaque Type IDs

## Status

Accepted

## Context

Schnéileopard works with many distinct entity types: genes, samples, cohorts, variants, etc. These are typically represented as strings in practice (e.g., "ENSG00000000003" for a gene ID). However, mixing different ID types at the type level (passing a SampleId where a GeneId was expected) can lead to subtle bugs.

## Decision

We use Scala 3 opaque types to create distinct, distinct-at-compile-time types for each entity ID:

```scala
opaque type GeneId = String
object GeneId:
  def apply(s: String): GeneId = ...
  def unsafe(s: String): GeneId = ...
```

Each opaque type wraps String but is distinct in the type system. At runtime, they are erased to String (zero overhead).

## Rationale

Opaque types provide:

1. Type safety: GeneId and SampleId are different types, preventing mixing
2. Zero runtime cost: Erased at runtime, no wrapper objects
3. Clarity: The intent (this is a gene identifier, not a generic string) is explicit
4. Ease of use: Construction is simple (GeneId("ENSG...")), no clunky wrappers

## Alternatives Considered

1. Case class wrappers: Type safe but creates wrapper objects at runtime, adds memory pressure
2. String type aliases: Type safe in theory but erased, so cannot distinguish at runtime or in error messages
3. Value classes: Closer but still have some runtime overhead
4. Generic Id[A] parameterized type: More flexible but more verbose for users

## Consequences

1. Users get strong typing for identifiers
2. Accidental mixing of ID types is caught at compile time
3. All ID types follow the same pattern for consistency
4. Bridge functions needed for interop with legacy code using raw strings (none currently needed)

## Implementation Notes

- Opaque types are defined in the Identifiers.scala file
- Value extraction uses a .value extension method
- Both safe constructors (with validation) and unsafe constructors exist
- Empty string validation is performed in safe constructors

## Related Decisions

- See ADR-001 for the Validation pattern used with ID construction
- See ADR-002 for module structure that organizes these types
- See DESIGN.md for overall philosophy

## See Also

- [API.md](../API.md) for all available identifier types
- [docs/ARCHITECTURE.md](ARCHITECTURE.md) for architecture overview
- [TROUBLESHOOTING.md](../TROUBLESHOOTING.md) for ID-related errors

---

*ADR-003: Opaque Type IDs - Last Updated: March 2026*
*Next Review: June 2026*
