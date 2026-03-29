# Getting Started with Schnéileopard

This guide walks you through installing Schnéileopard and running your first analysis.

## Installation

### Using sbt

Add Schnéileopard to your project's build.sbt:

```scala
libraryDependencies ++= Seq(
  "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-omics" % "0.1.0"
)
```

For the full suite including AI and IO features:

```scala
libraryDependencies ++= Seq(
  "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-omics" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-graph" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-ai" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-io" % "0.1.0"
)
```

### Scala Version Support

Schnéileopard requires Scala 3.6.1 or later.

## Basic Concepts

### Identifiers

Every molecular entity has a strongly typed identifier:

```scala
import io.github.olaflaitinen.schneileopard.core.*

val geneId = GeneId("ENSG00000000003")
val sampleId = SampleId("sample_001")
val cohortId = CohortId("cohort_healthy")
```

Identifiers use opaque types: they are distinct at compile time but have no runtime overhead.

### Expression Matrices

An expression matrix holds measurements of features (genes, metabolites, etc.) across samples:

```scala
import io.github.olaflaitinen.schneileopard.omics.*

val features = Vector(GeneId("ENSG001"), GeneId("ENSG002"))
val samples = Vector(SampleId("S1"), SampleId("S2"), SampleId("S3"))
val values = Vector(
  Vector(2.5, 3.1, 2.8),
  Vector(1.0, 0.9, 1.1)
)

val matrix = ExpressionMatrix(features, samples, values)
```

### Validation

All potentially failing operations return `Validation[T]`:

```scala
// Returns Validation[ExpressionMatrix]
val validation = ExpressionMatrix.validate(features, samples, values)

validation match
  case Validation.Valid(matrix) =>
    println(s"Matrix: ${matrix.featureCount} features x ${matrix.sampleCount} samples")
  case Validation.Invalid(error) =>
    println(s"Error: ${error.message}")
```

## First Steps

### Step 1: Load and Process Data

```scala
import io.github.olaflaitinen.schneileopard.core.*
import io.github.olaflaitinen.schneileopard.omics.*

// Create data
val features = Vector(
  GeneId("ENSG00000000003"),
  GeneId("ENSG00000000005")
)
val samples = Vector(SampleId("S1"), SampleId("S2"), SampleId("S3"))
val values = Vector(
  Vector(1.0, 2.0, 1.5),
  Vector(3.0, 4.0, 3.5)
)

// Create matrix with validation
val result = ExpressionMatrix.validate(features, samples, values)

result match
  case Validation.Valid(matrix) =>
    // Apply log transformation
    val transformed = Normalization.log2Transform(matrix, pseudocount = 1.0)
    println(s"Transformed matrix value (0,0): ${transformed(0, 0)}")

  case Validation.Invalid(error) =>
    println(s"Failed: ${error.message}")
```

### Step 2: Create Cohorts

```scala
import io.github.olaflaitinen.schneileopard.core.*
import io.github.olaflaitinen.schneileopard.omics.*

val sampleSet = Set(SampleId("S1"), SampleId("S2"), SampleId("S3"))

Cohort.validate(CohortId("my_cohort"), sampleSet) match
  case Validation.Valid(cohort) =>
    val enriched = cohort
      .withMetadata("condition", "treated")
      .withMetadata("tissue", "liver")

    println(s"Cohort ${cohort.id.value}: ${cohort.sampleCount} samples")
    println(s"Condition: ${enriched.getMetadata("condition")}")

  case Validation.Invalid(error) =>
    println(s"Error: ${error.message}")
```

### Step 3: Rank Features

```scala
import io.github.olaflaitinen.schneileopard.ai.*

val ranker = FeatureRanker()
val ranking = ranker.rankByVariance(matrix)

println(s"Top 5 features by variance:")
ranking.topN(5).foreach { feature =>
  println(s"  ${feature.featureId.value}: ${feature.score}")
}
```

## Common Patterns

### Pattern: Filter and Transform

```scala
// Filter high-variance features, then log-transform
val filtered = matrix.filterFeatures { geneId =>
  // Keep only genes starting with "ENSG0000000"
  geneId.value.startsWith("ENSG0000000")
}

val result = Normalization.log2Transform(filtered)
```

### Pattern: Batch Operations

```scala
// Validate multiple items at once
val cohorts = Seq(
  Cohort.validate(CohortId("c1"), Set(SampleId("S1"))),
  Cohort.validate(CohortId("c2"), Set(SampleId("S2"))),
  Cohort.validate(CohortId("c3"), Set(SampleId("S3")))
)

Validation.sequence(cohorts.toList) match
  case Validation.Valid(validCohorts) =>
    println(s"Created ${validCohorts.length} cohorts")

  case Validation.Invalid(error) =>
    println(s"Failed to create a cohort: ${error.message}")
```

### Pattern: Network Analysis

```scala
import io.github.olaflaitinen.schneileopard.graph.*

val edges = Set(
  InteractionEdge(GeneId("G1"), GeneId("G2"), Some(0.8)),
  InteractionEdge(GeneId("G2"), GeneId("G3"), Some(0.6))
)

val graph = InteractionGraph.fromEdges(edges)
println(s"Graph: ${graph.nodeCount} nodes, ${graph.edgeCount} edges")

val components = graph.connectedComponents
println(s"Connected components: ${components.length}")
```

## Next Steps

- Read the [ARCHITECTURE](ARCHITECTURE.md) guide for deeper understanding of design decisions
- Check the examples module in the repository for complete working examples
- Consult the [API documentation](https://github.com/olaflaitinen/schneileopard/doc) for detailed method signatures
- Review the [CHANGELOG](CHANGELOG.md) for release notes and migration guides

## Common Issues

### Issue: "Type mismatch" with IDs

Make sure you are not mixing different ID types:

```scala
// This will not compile
val geneId: GeneId = SampleId("S1")  // ERROR: type mismatch

// Correct:
val geneId: GeneId = GeneId("ENSG001")
val sampleId: SampleId = SampleId("S1")
```

### Issue: Empty validation results

Always handle `Invalid` cases:

```scala
val validation = ExpressionMatrix.validate(features, samples, values)

// Good: handles both cases
validation match
  case Validation.Valid(m) => processMatrix(m)
  case Validation.Invalid(e) => handleError(e)

// Problem: crashes if Invalid
val matrix = validation.asInstanceOf[Validation.Valid].value
```

### Issue: CSV parsing failures

Ensure CSV format is correct:

```scala
// Headers must include "feature" as the first column
// feature,S1,S2,S3
// ENSG001,2.5,3.1,2.8
```

## Getting Help

- File an issue on GitHub: https://github.com/olaflaitinen/schneileopard/issues
- Start a discussion: https://github.com/olaflaitinen/schneileopard/discussions
- Email the maintainers: olaf.laitinen@uni.lu

---

*Getting Started with Schnéileopard - Last Updated: March 2026*
*Next Review: June 2026*
