# Design Philosophy and Architecture

This document describes the core design principles and architectural decisions that shape Schnéileopard.

## Core Philosophy

### 1. Domain-Driven API Design

**Principle:** The API serves the domain first, convenience second.

**Implementation:**
- Identifiers are strongly typed (GeneId != SampleId)
- Data structures reflect biological concepts directly
- Algorithm implementations are separate from data models
- No "magic" automatic conversions

**Rationale:** Research code benefits from explicit, type-safe abstractions that prevent accidental misuse. A compilation error is better than a runtime data corruption.

### 2. Type Safety Over Runtime Flexibility

**Principle:** Use Scala's type system to catch errors at compile time.

**Implementation:**
- Opaque types for identifiers
- Sealed traits for result types
- Validation[A] monad for recoverable errors
- Strongly typed metadata values

**Rationale:** In biomedical research, data integrity is paramount. Type errors at compile time prevent categories of runtime failures.

### 3. Immutability by Default

**Principle:** Data structures are immutable unless mutation is justified.

**Implementation:**
- All public data types are immutable case classes or enums
- No mutable collections in public APIs
- Transformations create new data structures
- No setter methods

**Rationale:** Immutable data structures simplify reasoning about program behavior, especially in scientific computing where reproducibility matters.

### 4. Explicit Over Implicit

**Principle:** API contracts are explicit; hidden behavior is avoided.

**Implementation:**
- No implicit type conversions
- Validation functions named explicitly
- Side effects (like file I/O) are explicit in type signatures
- Configuration parameters are explicit, not derived

**Rationale:** Research code must be auditable. "Magic" behavior buried in implicits or defaults obscures what the code actually does.

### 5. Composability and Orthogonality

**Principle:** Components combine predictably without surprising interactions.

**Implementation:**
- Pure functions without side effects (except IO module)
- Modular structure with minimal coupling
- Clear dependency hierarchy
- Extension methods used sparingly

**Rationale:** Composable systems allow researchers to combine tools in novel ways and reason about combinations.

### 6. Minimal Dependencies

**Principle:** Core functionality depends only on the Scala standard library.

**Implementation:**
- core module: zero external dependencies
- omics module: core only
- graph module: core only
- ai module: core, omics, graph only
- io module: add scala-csv (zero-dependency library)

**Rationale:** Fewer dependencies mean fewer security vulnerabilities, simpler updates, and easier maintenance.

---

## Architectural Layers

```
┌─────────────────────────────────────┐
│  Application Layer (User Code)      │
├─────────────────────────────────────┤
│  Modules: Examples, Integration     │
├─────────────────────────────────────┤
│  Domain Model Layer                 │
│ ┌───────────────────────────────┐   │
│ │ Core: Identifiers, Validation │   │
│ ├───────────────────────────────┤   │
│ │ Omics: Data structures        │   │
│ │ Graph: Pathways               │   │
│ │ AI: Algorithms                │   │
│ │ IO: Codecs                    │   │
│ └───────────────────────────────┘   │
├─────────────────────────────────────┤
│  Scala Standard Library + JVM       │
└─────────────────────────────────────┘
```

### Layer Responsibilities

**Core Layer:**
- Type definitions
- Identifier representation
- Validation abstraction
- Error types
- No algorithms

**Domain Layer:**
- Data modelrepresentation
- Domain-specific transformations
- Normalization and preprocessing
- Graph operations
- Feature analysis and ranking

**IO Layer:**
- Data format, handling
- Serialization/deserialization
- Schema validation at boundaries

**Application Layer:**
- User workflow orchestration
- Cross-module integration
- Reproducibility tracking

---

## Module Organization

### schneileopard-core

**Purpose:** Foundational types used everywhere

**Contains:**
- Opaque type identifiers
- Validation monad
- Error types
- Extension methods for identifiers

**Public Exports:** `core.*`

**Dependency Policy:** No external dependencies

### schneileopard-omics

**Purpose:** Biomedical data structures

**Contains:**
- ExpressionMatrix
- Cohort
- SampleMetadata
- Normalization functions
- Transformations

**Public Exports:** `omics.*`

**Dependency Policy:** core only

### schneileopard-graph

**Purpose:** Network and pathway types

**Contains:**
- InteractionGraph
- InteractionEdge
- Graph traversal
- Connected components
- Path-finding helpers (future)

**Public Exports:** `graph.*`

**Dependency Policy:** core only

### schneileopard-ai

**Purpose:** Machine learning interfaces

**Contains:**
- FeatureRanker
- Explainability types
- Stratification types
- Confidence/uncertainty
- Baseline implementations

**Public Exports:** `ai.*`

**Dependency Policy:** core, omics, graph

### schneileopard-io

**Purpose:** Data import/export

**Contains:**
- ExpressionMatrixCodec
- SampleMetadataCodec
- CSV parsing
- Validation at bounds

**Public Exports:** `io.*`

**Dependency Policy:** core, omics + scala-csv

### schneileopard-examples

**Purpose:** Usage demonstrations and integration testing

**Not published to Maven Central**

**Contains:**
- End-to-end workflows
- Integration tests
- Usage patterns
- Synthetic data examples

### schneileopard-bench

**Purpose:** Performance measurement

**Not published to Maven Central**

**Contains:**
- JMH benchmarks
- Performance baselines
- Regression detection scaffolding

---

## Data Flow and Processing Patterns

### Typical Analysis Workflow

```scala
// Load data
val matrix = ExpressionMatrixCodec.readCSV(file)
  .getOrElse(throw new Exception("Parse failed"))

// Validate and prepare
val validated = matrix match {
  case matrix if matrix.featureCount > 10000 => featureFilter(matrix)
  case matrix => matrix
}

// Normalize
val normalized = Normalization.log2Transform(validated)

// Analyze
val ranker = FeatureRanker()
val ranking = ranker.rankByVariance(normalized)

// Explain
val topFeatures = ranking.topN(20)
topFeatures.foreach { fi =>
  println(s"${fi.featureId.value}: ${fi.score}")
}
```

**Pattern Elements:**
1. **Input Boundary** - CSV parsing with validation
2. **Preparation** - Filtering, validation on input
3. **Transformation** - Normalization chain
4. **Analysis** - Algorithmic processing
5. **Output** - Result explanation and export

### Error Handling Strategy

```scala
// Recoverable errors are represented as Validation
val result: Validation[ExpressionMatrix] =
  ExpressionMatrix.validate(features, samples, values)

// Pattern match to handle success/failure
result match {
  case Validation.Valid(matrix) => analyze(matrix)
  case Validation.Invalid(error) =>
    println(s"Validation failed: ${error.message}")
    recover(error)
}

// Or use monadic composition
val analysis = for {
  matrix <- ExpressionMatrix.validate(features, samples, values)
  normalized <- Validation.attempt(Normalization.log2Transform(matrix))
} yield ranker.rankByVariance(normalized)
```

---

## Type System Decisions

### Opaque Types for Identifiers

**Why Opaque Types?**

```scala
// Compile error - cannot mix types
val gene: GeneId = GeneId("G1")
val sample: SampleId = gene  // Error: type mismatch

// Correct usage
val cohort = Cohort(CohortId("C1"), Set(SampleId("S1")))
```

**Benefits:**
- Type-safe without runtime overhead
- No new class instantiation
- Compile-time guarantees
- Zero-cost abstraction

### Enum for Result Types

```scala
enum Validation[+A]:
  case Valid(value: A)
  case Invalid(error: DomainError)
```

**Why Enums?**
- Exhaustiveness checking at compile time
- Pattern matching is idiomatic Scala
- Better performance than Either in Scala 3
- Clear semantics

### Case Classes for Data Models

```scala
case class ExpressionMatrix(
    features: Vector[GeneId],
    samples: Vector[SampleId],
    values: Vector[Vector[Double]]
)
```

**Why Case Classes?**
- Automatic equals, hashCode, toString
- Immutability by default (val fields)
- Copy method for immutable updates
- Pattern matching support

---

## Performance Considerations

### Design/Performance Trade-offs

**Choice:** Immutable Vector for matrices
- **Pro:** Safe, no aliasing bugs, functional transformations
- **Con:** Copy-on-write overhead for large matrices
- **Mitigated By:** Filtering and windowing before computations

**Choice:** Eager evaluation
- **Pro:** Predictable performance, easy reasoning
- **Con:** Large matrices fully loaded in memory
- **Mitigated By:** Streaming support planned for 1.0.0

**Choice:** Scala collections over raw arrays
- **Pro:** Type safety, no null pointers, better abstractions
- **Con:** Slightly slower than Java arrays
- **Mitigated By:** Benchmarking and optimization for hotspots

### Performance Optimization Guidance

For researchers using Schnéileopard:

1. **Filter Early** - Remove low-variance features before ML
2. **Work on Subsets** - Analyze cohorts separately when possible
3. **Cache Computations** - Store intermediate results if reused
4. **Profile** - Use JMH benchmarks to identify bottlenecks

---

## Extensibility Patterns

### Adding New Algorithms

**Pattern:**

```scala
trait Ranker:
  def rank(matrix: ExpressionMatrix): RankingExplanation

class MyCustomRanker extends Ranker:
  def rank(matrix: ExpressionMatrix): RankingExplanation = ...
```

**Not Inheritance:** Final classes and sealed hierarchies prevent fragile base class problems.

### Adding New Data Types

**Pattern:**

```scala
case class NewOmicsType(
    features: Vector[FeatureId],
    samples: Vector[SampleId],
    values: Vector[Vector[Double]]  // Or domain-specific type
)

object NewOmicsType:
  def validate(...): Validation[NewOmicsType] = ...
```

### Adding New IO Formats

**Pattern:**

```scala
object NewFormatCodec:
  def read(file: File): Validation[ExpressionMatrix] = ...
  def write(matrix: ExpressionMatrix, file: File): Unit = ...
```

---

## Stability and Compatibility

### Public API Contract

**What's Guaranteed (Public):**
- Package names in io.github.olaflaitinen.schneileopard.*
- Type names and method signatures
- Return types and their contract
- Documented behavior

**What's Not Guaranteed (Internal):**
- Implementation details
- Packages named *.internal.*
- Private methods and fields
- Undocumented behavior

### Deprecation Strategy

```scala
@deprecated("Use newMethod() instead", "0.2.0")
def oldMethod(): Int = ...
```

**Timeline:**
1. **Deprecation Notice** (Version N)
2. **Functional Period** (Version N → N+1, at least 2 months)
3. **Removal** (Version N+1+)

---

## Testing Strategy

### Test Organization

```
core/src/test/scala/io/github/olaflaitinen/schneileopard/core/
  ├── IdentifiersTest.scala          # Unit tests for types
  ├── ValidationTest.scala           # Unit tests for Validation monad
  └── ErrorTest.scala                # Unit tests for error handling

examples/src/test/scala/.../examples/
  └── IntegrationTest.scala          # End-to-end workflows
```

### Test Principles

1. **Unit Tests** - Individual functions, happy path and errors
2. **Property-Based Tests** - Invariants across inputs (using ScalaCheck)
3. **Integration Tests** - Full workflows with synthetic data
4. **No Randomness** - All tests deterministic, repeatable

---

## Documentation Strategy

### Scaladoc Standards

**Every Public Type:**
```scala
/**
 * Brief one-line description.
 *
 * Longer explanation including context and use cases.
 * Multiple paragraphs if needed.
 *
 * Example:
 * {{{
 *   val matrix = ExpressionMatrix(features, samples, values)
 *   val normalized = Normalization.log2Transform(matrix)
 * }}}
 *
 * @param param1 description
 * @param param2 description
 * @return what is returned
 * @throws SomeException when this happens
 */
```

### Example Placement

- **Simple API:** Scaladoc example only
- **Complex Workflow:** Scaladoc + GETTING_STARTED.md + examples/
- **Integration Scenario:** examples/ + documentation/

---

## Scala Standard Library Compatibility

Schnéileopard's core types are designed to be generic and reusable beyond the biomedical domain. The Validation framework and error types in particular are candidates for inclusion in the Scala standard library.

### Why Stdlib Compatibility?

1. **No Domain Coupling**: Core types are purely functional, not biomedical-specific
2. **General Purpose**: Validation and error handling apply to all domains
3. **Zero Dependencies**: Core module depends only on Scala stdlib
4. **Proven Design**: Production-tested with thousands of lines of dependent code
5. **Community Benefit**: Would elevate error handling across Scala ecosystem

### Stdlib Integration Plan

See [SCALA_STDLIB.md](SCALA_STDLIB.md) for complete details:
- Current phase: Community testing and feedback (2026)
- Package names reserved: `scala.util.validation.*`, `scala.util.error.*`
- Target inclusion: Scala 3.8 (2027 Q1-Q2)
- Long-term: Backward-compatible with Maven Central version

### What's Stdlib-Ready

- **Validation[A]**: Monad for composable error handling
- **DomainError**: Sealed trait hierarchy for typed errors
- **Error Subtypes**: ValidationError, ParseError, TypeMismatchError, MissingValueError, ConstraintError

### What's Domain-Specific

- **Identifiers**: GeneId, SampleId, etc. (biomedical terminology)
- **Omics Structures**: ExpressionMatrix, Cohort (biomedical)
- **Algorithms**: Feature ranking, stratification (ML/medical)
- **IO**: CSV codecs for research data formats

---

## Future Evolution

### Planned Architectural Improvements

**Streaming (1.0.0):**
```scala
trait StreamingMatrix:
  def rowIterator: Iterator[Vector[Double]]
  def featureCount: Int
  def sampleCount: Int
```

**Lazy Evaluation (1.x):**
- Deferred operations
- Graph-based computation
- Automatic optimization

**Parallel Processing (1.0.0):**
- Collection-parallel methods
- Thread-safe operations
- Spark integration (optional)

---

## Community and Collaboration

### Accepting Contributions

**Encouraged:**
- Bug fixes
- Documentation improvements
- Performance optimizations
- Additional test coverage

**Requires Discussion:**
- Major architectural changes
- New public types
- API changes
- Dependency additions

**Process:**
1. Open issue to discuss approach
2. Fork and branch
3. Implement with tests
4. Submit PR with description
5. Review and iterate

---

*Design Philosophy - Last Updated: March 2026*
