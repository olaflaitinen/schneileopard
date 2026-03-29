# Schnéileopard

A type-safe Scala toolkit for molecular entities, omics-aware data structures, cohort modeling, pathway-aware analytics, and explainable AI workflows in systems biomedicine.

## Overview

Schnéileopard is a production-grade library designed for researchers and engineers working in computational biology and systems medicine. It provides strongly typed domain models for molecular biology data, structured representations of omics datasets, pathway and interaction networks, and practical AI interfaces for phenotype prediction, sample stratification, and model explanation.

The library is architectured for extensibility without sacrificing type safety or clarity. The core domain layer has minimal dependencies, while specialized modules for graph operations, AI workflows, and IO handling are available for adoption as needed.

## Project Motivation

Systems and molecular biomedicine increasingly relies on computational methods that integrate molecular measurements, pathway knowledge, and statistical learning. Existing tools often treat biological domains as generic data, losing opportunities for compile time verification, type-driven API design, and domain-aware error handling.

Schnéileopard brings Scala's strengths in type safety, functional programming, and ecosystem maturity to bear on real biomedical problems. Users can build analyses confident that type errors are impossible, that transformations maintain semantic meaning, and that results are reproducible and explainable.

## Design Goals

1. **Scala-first API design**: Emit natural, idiomatic Scala, not Java classes with functional wrappers.
2. **Strong typing and domain modeling**: Make invalid bioinformatic states unrepresentable.
3. **Minimal surprises**: Follow Scala ecosystem conventions and well-known patterns.
4. **Composability**: Core domain types have no transitive dependencies beyond the standard library.
5. **Explainability**: AI and statistical results must be typed values, not black boxes.
6. **Research reproducibility**: Built-in support for deterministic computation and audit trails.

## Installation

Add Schnéileopard to your project dependencies:

```scala
// In build.sbt
libraryDependencies ++= Seq(
  "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-omics" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-graph" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-ai" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-io" % "0.1.0"
)
```

Scala 3.6.1 or later is required.

## Quick Start

```scala
import io.github.olaflaitinen.schneileopard.core.*
import io.github.olaflaitinen.schneileopard.omics.*
import io.github.olaflaitinen.schneileopard.ai.*

// Create strongly typed identifiers
val studyId = StudyId("study_2026_003")
val sampleIds = Vector(
  SampleId("S001"),
  SampleId("S002"),
  SampleId("S003")
)

// Build a cohort with type safety
val cohort = Cohort(
  id = CohortId("cohort_discovery"),
  samples = sampleIds.toSet,
  metadata = Map("tissue" -> "liver", "condition" -> "healthy")
)

// Construct expression data with validation
val geneIds = Vector(
  GeneId("ENSG00000000003"),
  GeneId("ENSG00000000005"),
  GeneId("ENSG00000000419")
)

val expressionValues = Vector(
  Vector(2.5, 3.1, 2.8),
  Vector(4.2, 3.9, 4.1),
  Vector(1.2, 1.5, 1.3)
)

val matrix = ExpressionMatrix(
  features = geneIds,
  samples = sampleIds,
  values = expressionValues
)

// Analyze and explain
val ranker = new FeatureRanker()
val scoring = ranker.scoreFeatures(matrix, cohort)
val explanation = scoring.explanation
```

## Module Overview

### schneileopard-core

Pure domain models for molecular biology without external dependencies. Contains strongly typed identifiers, validation primitives, and shared error types.

- **Gene, Transcr, Protein**: Molecular entity types with identifier namespacing.
- **Variant, DiseaseTerm, PhenotypeTerm**: Clinical and genomic classification types.
- **SampleId, CohortId, FeatureId**: Explicitly typed identifiers for tracking.
- **Validation**: Schema validation and type-aware error handling.

### schneileopard-omics

Data structures for omics datasets and cohort management. Includes expression matrices, sample metadata, and normalization helpers.

- **ExpressionMatrix**: Genes, samples, and expression measurements with type safety.
- **FeatureMatrix**: Generic feature-sample matrices for flexibility.
- **SampleMetadata**: Type-checked sample annotations and covariates.
- **Cohort**: Cohort definitions with membership and metadata.
- **Normalization**: Standard omics transformations (TMM, DESeq scaling, log transformations).

### schneileopard-graph

Pathway and interaction network types. Includes typed nodes, edges, membership tracking, and graph traversal helpers.

- **PathwayNode, InteractionEdge**: Domain-aware graph primitives.
- **InteractionGraph**: Undirected knowledge graphs with efficient traversal.
- **PathwayScorer**: Pathway-aware feature importance calculations.
- **Graph traversal**: BFS, DFS, and connectivity analysis.

### schneileopard-ai

Domain-aware AI interfaces and implementations. Lightweight, interpretable algorithms without remote API dependencies.

- **FeatureRanker**: Supervised and unsupervised feature scoring with explanations.
- **SampleStratifier**: Clustering and stratification with uncertainty quantification.
- **PhenotypePredictor**: Classification for phenotypes and binary outcomes.
- **Explainability**: Typed representations of feature importance, prediction confidence, and model interpretations.

### schneileopard-io

CSV and TSV parsing and generation for research-shaped tabular data.

- **ExpressionMatrixCodec**: Read and write expression matrices in standard formats.
- **SampleMetadataCodec**: Parse sample annotations and metadata tables.
- **CohortCodec**: Serialize and deserialize cohort definitions.
- **Validation**: Type-aware parsing errors and missingness checks.

## Architecture

Schnéileopard follows a layered architecture:

**Layer 1: Core Domain (no dependencies)**
- Foundational types: GeneId, SampleId, Variant, etc.
- Error and validation primitives.
- Algebraic interfaces for extensibility.

**Layer 2: Omics Data (core dependency)**
- Matrices, cohorts, metadata.
- Normalization and transformations.

**Layer 3: Graph and AI (core + omics dependencies)**
- Pathway networks.
- Feature ranking and sample stratification.
- Explainability types and baseline implementations.

**Layer 4: IO (core + omics dependencies)**
- CSV, TSV, and tabular format support.
- Parsing validation and error recovery.

This layering allows users to adopt domain types without including AI infrastructure, or to use only graph operations without expression matrices.

## Examples

See the `modules/examples` directory for complete, runnable examples:

- **GeneExpressionAnalysis**: Load expression data, normalize, and perform quality control.
- **PathwayStratification**: Cluster samples using pathway scores and visualize results.
- **PhenotypePredictor**: Train and evaluate a phenotype prediction model with explanations.

## Testing

The library includes comprehensive unit tests, property-based tests, and integration examples:

```bash
sbt test
```

All public modules maintain high test coverage with focus on correctness and failure modes.

## Documentation

- **API Documentation**: Generated Scaladoc is available via scaladex.org.
- **Architecture Decisions**: See `docs/architecture` for detailed design decisions.
- **VERSIONING.md**: Semantic versioning and compatibility policy.
- **COMPATIBILITY.md**: Breaking change definitions and migration guides.
- **CHANGELOG.md**: Release history and updates.

## Contributing

Contributions are welcome. Please see CONTRIBUTING.md for guidelines on code style, testing, and the submission process.

## Compatibility

Schnéileopard follows semantic versioning and maintains binary compatibility within major versions according to the Scala Platform versioning guidelines. See COMPATIBILITY.md for details.

## Citation

If you use Schnéileopard in academic research, please cite:

```bibtex
@software{laitinen2026schneileopard,
  author = {Laitinen-Fredriksson Lundstrom-Imanov, Gustav Olaf Yunus},
  title = {Schnéileopard: A type-safe Scala toolkit for systems biomedicine},
  year = {2026},
  url = {https://github.com/olaflaitinen/schneileopard}
}
```

Also see CITATION.cff for additional citation metadata.

## License

Schnéileopard is licensed under the European Union Public License 1.2 (EUPL 1.2). See LICENSE.txt for the full license text.

## Code of Conduct

This project adheres to the Contributor Covenant code of conduct. See CODE_OF_CONDUCT.md for details.

## Security

For security concerns, please see SECURITY.md.

## Authors and Maintainers

See AUTHORS.md for contributor information.

## Support

For bug reports, feature requests, and questions, please use the GitHub issue tracker at https://github.com/olaflaitinen/schneileopard/issues.

## Scala Standard Library Alignment

Schnéileopard's core validation framework is designed for compatibility with the Scala standard library. See [SCALA_STDLIB.md](SCALA_STDLIB.md) for details on the path toward potential inclusion in Scala 3.8+ and [SCALA_STDLIB_CANDIDACY.md](SCALA_STDLIB_CANDIDACY.md) for which components are stdlib-ready.

---

*README - Last Updated: March 2026*
*Next Review: June 2026*
