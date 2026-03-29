# Architecture Overview

## Design Philosophy

Schnéileopard is built on the principle that strong typing and clear domain modeling are more valuable than generic flexibility. Every public API surface is designed to be discoverable, self-documenting, and resistant to misuse.

### Core Principles

1. **Type Safety First**: Leverage Scala's type system to prevent errors at compile time. Opaque types, sealed traits, and ADTs make illegal states unrepresentable.

2. **Immutability by Default**: All public data types are immutable. Mutation exists only in internal implementation details.

3. **Composability**: Provide small, well-defined types and functions that compose well. Avoid magic or magical implicits.

4. **Explicit Over Implicit**: Users should understand what their code does. Prefer explicit parameters and clear method names.

5. **Domain Native**: The API is designed for biomedical researchers and engineers, using their terminology and patterns naturally.

## Module Architecture

```
schneileopard-core
    |
    +-- Domain types
    |   - Opaque ID types (GeneId, SampleId, etc.)
    |   - Error types (DomainError and subtypes)
    +-- Validation framework
    |   - Validation[A] enum
    |   - Error accumulation helpers

schneileopard-omics (depends on core)
    |
    +-- Data structures
    |   - ExpressionMatrix
    |   - Cohort
    |   - SampleMetadata
    +-- Transformations
        - Normalization (log2, zscore, quantile normalize)

schneileopard-graph (depends on core)
    |
    +-- Graph types
    |   - InteractionGraph
    |   - InteractionEdge
    +-- Graph algorithms
        - Connected components
        - Neighbor queries

schneileopard-ai (depends on core, omics, graph)
    |
    +-- Explainability types
    |   - Confidence
    |   - FeatureImportance
    |   - RankingExplanation
    |   - PredictionExplanation
    +-- Algorithms
        - FeatureRanker (variance-based, correlation-based)
        - StratificationResult

schneileopard-io (depends on core, omics)
    |
    +-- CSV/TSV codecs
    |   - ExpressionMatrixCodec
    |   - SampleMetadataCodec
```

## Data Flow Patterns

### Reading Expression Data

1. User calls `ExpressionMatrixCodec.readCSV(file)`
2. Codec parses CSV, validates structure
3. Returns `Validation[ExpressionMatrix]`
4. User pattern-matches to extract data or handle errors

### Analyzing Expression Patterns

1. Create or read ExpressionMatrix
2. Apply normalization (e.g., `Normalization.log2Transform`)
3. Create FeatureRanker
4. Call ranking method to get RankingExplanation
5. Extract top N features via `topN`

### Stratifying Samples

1. Create ExpressionMatrix
2. Create Cohort(s)
3. Use FeatureRanker within cohort context
4. Result types include StratificationResult with interpretable strata

## Error Handling

Schnéileopard uses a typesafe validation pattern:

- `Validation[A]` is an enum with two cases: `Valid(a: A)` and `Invalid(error: DomainError)`
- `DomainError` is sealed with subtypes: `ValidationError`, `ParseError`, `TypeMismatchError`, `MissingValueError`, `ConstraintError`
- All public APIs that may fail return `Validation[T]` rather than throwing
- Users handle errors via pattern matching or monadic operations (map, flatMap, fold)

Example:
```scala
ExpressionMatrix.validate(features, samples, values) match
  case Valid(matrix) => processMatrix(matrix)
  case Invalid(error) => println(s"Error: ${error.message}")
```

## Dependency Management

### Core Module Dependencies
- Scala standard library only
- Scala 3.6.1+
- Java 11 LTS or later

### Omics Module Dependencies
- Core module
- Scala standard library

### Graph Module Dependencies
- Core module
- Scala standard library

### AI Module Dependencies
- Core, Omics, Graph modules
- Scala standard library

### IO Module Dependencies
- Core, Omics modules
- scala-csv 1.4.1+
- Scala standard library

Transitive dependency counts are kept minimal to avoid bloat and simplify compatibility analysis.

## Stability and Evolution

### Stable APIs
- Domain types in core module (identifiers, error types, validation)
- Omics data structures (ExpressionMatrix, Cohort)
- Published types and public methods

### Internal APIs
- Anything in packages marked `internal`
- Implementation classes not part of published API surface
- Subject to change without notice

### Experimental APIs
- Still being validated with users
- Marked with `@Experimental` annotation
- May change or be removed in minor versions

## Design Patterns

### Validation Pattern
- Recoverable errors use `Validation[T]`
- Illegal construction prevented at type level
- All validation is eager (run with call, not lazy)

### Factory Pattern
- Each public type has a companion object
- Factory methods include both validated and unsafe versions
- `apply` is validated, `unsafe` bypasses validation for performance-critical code

### Extension Methods
- Limited and explicit (e.g., `.value` on opaque types)
- Used only when they genuinely improve ergonomics
- Documented in same file as the type

### Functional Composition
- Operations return new instances (immutable)
- Methods chain naturally: `matrix.map(...).filterFeatures(...)`
- No hidden state mutations

## Performance Considerations

- Expression matrices use Vector (immutable, structural sharing)
- Normalization is lazy where possible
- ID types are zero-overhead abstractions (opaque)
- Memory usage is proportional to data size, no hidden allocations
- Benchmarks tracked in schneileopard-bench module

---

*Architecture Overview - Last Updated: March 2026*
*Next Review: June 2026*
