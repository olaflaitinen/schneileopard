# API Documentation

## Overview

Schnéileopard's API is organized around domain types first, algorithms second. All public interfaces are stable within major versions and documented via Scaladoc.

## Core Module API

### Identifiers

All identifiers are strongly typed using opaque types to prevent accidental mixing:

```scala
import io.github.olaflaitinen.schneileopard.core.*

// Create identifiers
val geneId: GeneId = GeneId("ENSG00000000003")
val sampleId: SampleId = SampleId("S001")
val cohortId: CohortId = CohortId("cohort_healthy")
val featureId: FeatureId = FeatureId("G1")

// Extract values
val geneValue: String = geneId.value
```

Available identifier types:
- `GeneId` - Unique gene identifier
- `TranscriptId` - RNA transcript identifier
- `ProteinId` - Protein identifier
- `VariantId` - Genomic variant identifier
- `SampleId` - Sample or specimen identifier
- `CohortId` - Group of samples identifier
- `StudyId` - Study or dataset identifier
- `FeatureId` - General feature identifier
- `DiseaseTerm` - Disease ontology term
- `PhenotypeTerm` - Phenotype ontology term

### Validation Framework

The `Validation[A]` type represents either success or failure:

```scala
// Create validations
val success: Validation[Int] = Validation.valid(42)
val failure: Validation[Int] = Validation.invalid(ValidationError("test error"))

// Compose validations
val result = for
  a <- Validation.valid(10)
  b <- Validation.valid(20)
yield a + b
// result.isValid == true

// Pattern matching
result match
  case Validation.Valid(value) => println(s"Success: $value")
  case Validation.Invalid(error) => println(s"Error: ${error.message}")

// Conversion
val either: Either[DomainError, Int] = result.toEither
val option: Option[Int] = result.toOption
```

### Error Types

Domain errors represent expected failures:

```scala
// Validation errors
val error1 = ValidationError("Matrix has wrong dimensions")

// Parse errors
val error2 = ParseError("Invalid number format", line = Some(5), column = Some(3))

// Type mismatches
val error3 = TypeMismatchError("Expected Double", expected = "Double", actual = "String")

// Missing values
val error4 = MissingValueError("Required field missing", field = "cohort_id")

// Constraint violations
val error5 = ConstraintError("Value out of range", constraint = "0 <= x <= 1", value = "1.5")
```

## Omics Module API

### Expression Matrices

Digital omics data represented as feature x sample matrices:

```scala
import io.github.olaflaitinen.schneileopard.omics.*

// Create matrix
val features = Vector(GeneId("G1"), GeneId("G2"))
val samples = Vector(SampleId("S1"), SampleId("S2"), SampleId("S3"))
val values = Vector(
  Vector(1.5, 2.0, 1.8),
  Vector(3.2, 2.8, 3.5)
)

val matrix = ExpressionMatrix(features, samples, values)

// Query matrix
val featureCount = matrix.featureCount
val sampleCount = matrix.sampleCount
val value = matrix(0, 1)  // Feature 0, Sample 1

// Process matrix
val normalized = Normalization.log2Transform(matrix, pseudocount = 1.0)
val zscore = Normalization.zscore(matrix)
val libNorm = Normalization.libraryNormalize(matrix, scale = 1e6)

// Filter matrix
val highVar = Normalization.filterLowVariance(matrix, threshold = 0.5)
val filtered = matrix.filterFeatures(f => !f.value.startsWith("ENSG"))
```

### Cohorts

Group samples with metadata:

```scala
// Create cohort
val samples = Set(SampleId("S1"), SampleId("S2"), SampleId("S3"))
val cohort = Cohort(
  CohortId("healthy"),
  samples,
  metadata = Map("condition" -> "control", "tissue" -> "liver")
)

// Query cohort
val count = cohort.sampleCount
val hasSample = cohort.contains(SampleId("S1"))
val tissue = cohort.getMetadata("tissue")

// Modify cohort
val updated = cohort
  .withMetadata("age_median", "45")
  .addSample(SampleId("S4"))
  .removeSample(SampleId("S3"))

// Validate during creation
val validation = Cohort.validate(CohortId("group"), Set(SampleId("S1")))
```

### Sample Metadata

Rich metadata for samples:

```scala
// Metadata values
val tissue = MetadataValue.StringValue("liver")
val age = MetadataValue.NumericValue(45.0)
val status = MetadataValue.CategoricalValue("healthy", 0)
val treated = MetadataValue.BooleanValue(true)

// Sample metadata
val metadata = SampleMetadata(
  SampleId("S1"),
  Map(
    "tissue" -> tissue,
    "age" -> age,
    "status" -> status
  )
)

// Query metadata
val tissueOpt = metadata.get("tissue")
val ageNum = metadata.getNumeric("age")
val category = metadata.getCategory("status")

// Metadata table
val table = SampleMetadataTable(
  metadata = Vector(metadata),
  columns = Vector("sample", "tissue", "age", "status")
)
```

### Normalization

Transformations for omics data:

```scala
// Log transformation
val log = Normalization.log2Transform(matrix, pseudocount = 1.0)

// Z-score normalization (per-feature)
val zscore = Normalization.zscore(matrix)

// Quantile normalization
val quantile = Normalization.quantileNormalize(matrix)

// Library size normalization
val cpm = Normalization.libraryNormalize(matrix, scale = 1e6)

// Variance filtering
val filtered = Normalization.filterLowVariance(matrix, threshold = 0.1)
```

## Graph Module API

### Interaction Networks

Pathways and molecular networks:

```scala
import io.github.olaflaitinen.schneileopard.graph.*

// Create edges
val edges = Set(
  InteractionEdge(GeneId("G1"), GeneId("G2"), Some(0.8), Some("binding")),
  InteractionEdge(GeneId("G2"), GeneId("G3"), Some(0.6), Some("regulation")),
  InteractionEdge(GeneId("G1"), GeneId("G3"), Some(0.7))
)

// Create graph
val graph = InteractionGraph.fromEdges(edges)

// Query graph
val nodeCount = graph.nodeCount
val edgeCount = graph.edgeCount
val neighbors = graph.neighbors(GeneId("G1"))
val incident = graph.incidentEdges(GeneId("G2"))
val hasEdge = graph.hasEdge(GeneId("G1"), GeneId("G3"))

// Topological analysis
val components = graph.connectedComponents
components.foreach { component =>
  println(s"Component with ${component.size} nodes")
}
```

## AI Module API

### Feature Ranking

Importance scoring for features:

```scala
import io.github.olaflaitinen.schneileopard.ai.*

val ranker = FeatureRanker()

// Rank by variance
val varRanking = ranker.rankByVariance(matrix)

// Rank by correlation with phenotype
val phenotype = Vector(0.0, 0.0, 0.0, 1.0, 1.0)
val corrRanking = ranker.rankByCorrelation(matrix, phenotype)

// Score features in cohort context
val ranking = ranker.scoreFeatures(matrix, cohort)

// Access results
val topFeatures = ranking.topN(10)
val aboveThreshold = ranking.aboveThreshold(0.5)
ranking.features.foreach { fi =>
  println(s"${fi.featureId.value}: ${fi.score}")
}
```

### Explainability

Typed representations of explanations:

```scala
// Confidence scores
val conf = Confidence(0.85)  // Must be 0-1
val isHigh = conf.isHigh    // >= 0.8
val isModerate = conf.isModerate
val isLow = conf.isLow

// Feature importance
val importance = FeatureImportance(
  FeatureId("G1"),
  score = 0.92,
  method = "correlation"
)

// Ranking explanations
val ranking = RankingExplanation(
  features = importances,
  method = "variance_ranking",
  parameters = Map("threshold" -> "0.1")
)

// Prediction explanations
val explanation = PredictionExplanation(
  prediction = "likely_diseased",
  confidence = Confidence(0.82),
  topFeatures = ranking.topN(5),
  method = "logistic_regression"
)
val report = explanation.toReport
```

### Stratification

Sample grouping and clustering:

```scala
// Individual stratum
val stratum = StratumResult(
  samples = Set(SampleId("S1"), SampleId("S2")),
  stratumId = "stratum_1",
  characterization = Some("healthy controls"),
  metadata = Map("phenotype" -> "normal")
)

// Stratification result
val strata = Vector(
  StratumResult(Set(SampleId("S1"), SampleId("S2")), "stratum_1"),
  StratumResult(Set(SampleId("S3"), SampleId("S4")), "stratum_2")
)

val result = StratificationResult(
  strata = strata,
  method = "kmeans",
  parameters = Map("k" -> "2")
)

// Query results
val stratumCount = result.stratumCount
val allSamples = result.allSamples
val sample1Stratum = result.findSampleStratum(SampleId("S1"))
```

## IO Module API

### CSV Codecs

Reading and writing tabular data:

```scala
import io.github.olaflaitinen.schneileopard.io.*
import java.io.File

val file = new File("expression.csv")

// Write matrix
ExpressionMatrixCodec.writeCSV(matrix, file)

// Read matrix
val matrixResult: Validation[ExpressionMatrix] = ExpressionMatrixCodec.readCSV(file)
matrixResult match
  case Validation.Valid(matrix) => println(s"Loaded ${matrix.featureCount} features")
  case Validation.Invalid(error) => println(s"Error: ${error.message}")

// Write metadata
val metadataFile = new File("metadata.csv")
SampleMetadataCodec.writeCSV(table, metadataFile)

// Read metadata
val metadataResult: Validation[SampleMetadataTable] = SampleMetadataCodec.readCSV(metadataFile)
```

## Extension Methods

Convenience methods added to types:

```scala
// All identifier types have .value extension
val s: String = geneId.value

// Matrices have filtering and mapping
val mapped = matrix.map(_ * 2)
val filtered = matrix.filterFeatures(id => id.value.startsWith("ENSG"))
```

## Common Workflows

### Workflow 1: Load and normalize expression data

```scala
import io.github.olaflaitinen.schneileopard.*

val matrix = ExpressionMatrixCodec.readCSV(new File("data.csv")).getOrElse(null)
val normalized = Normalization.log2Transform(matrix)
val zscore = Normalization.zscore(normalized)
```

### Workflow 2: Feature ranking in cohorts

```scala
val cohort = Cohort(CohortId("disease"), diseasesamples)
val ranker = FeatureRanker()
val ranking = ranker.scoreFeatures(matrix, cohort)
val topFeatures = ranking.topN(20)
```

### Workflow 3: Network analysis with stratification

```scala
val graph = InteractionGraph.fromEdges(edges)
val components = graph.connectedComponents

components.foreach { component =>
  val componentGenes = component
  // Analyze this connected component
}
```

## Performance Considerations

- `ExpressionMatrix` is immutable and copy-on-write
- `Normalization` functions return new matrices (no in-place modification)
- Large matrices (>100k x 1k) should be filtered before analysis
- CSV I/O loads entire file into memory

## API Stability

- Core APIs are stable within major versions
- Experimental APIs marked with `@Experimental` (future versions)
- Internal packages (io.github.olaflaitinen.schneileopard.internal.*) may change
- See COMPATIBILITY.md for deprecation policy

## Scaladoc

Full API documentation is available in Scaladoc:

```bash
sbt doc
# Then open target/scala-3.6.1/api/index.html
```

## Type Hierarchy

```
DomainError (sealed trait)
  - ValidationError
  - ParseError
  - TypeMismatchError
  - MissingValueError
  - ConstraintError

Validation[A] (enum)
  - Valid(A)
  - Invalid(DomainError)

MetadataValue (enum)
  - StringValue
  - NumericValue
  - CategoricalValue
  - BooleanValue
```

## See Also

- [README.md](README.md) for quick start
- [INSTALLATION.md](INSTALLATION.md) for setup
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for design
- [TROUBLESHOOTING.md](TROUBLESHOOTING.md) for common issues

---

*API Documentation - Last Updated: March 2026*
*Next Review: June 2026*
