# Changelog

All notable changes to this project will be documented in this file.

The format is based on Keep a Changelog and this project adheres to Semantic Versioning.

## [0.1.0] - 2026-03-29

**First stable release: Production-grade Scala library for Systems and Molecular Biomedicine**

### Added

- **Core module** (zero external dependencies, Scala stdlib candidate)
  - Strong typed domain identifiers: GeneId, TranscriptId, ProteinId, SampleId, CohortId, FeatureId, StratumId, StudyId, PathwayId
  - Extension methods for identifier values (`.value` accessor)
  - Validation[A] monad for composable error handling
  - DomainError sealed trait hierarchy with 5 standard error types
  - Comprehensive pattern matching support

- **Omics module** for molecular expression analysis
  - ExpressionMatrix for gene expression data (features × samples)
  - Feature and sample filtering operations
  - 5 normalization techniques: log2, zscore, quantile, library size, low-variance filtering
  - Cohort management with sample grouping
  - SampleMetadata with typed values (String, Numeric, Categorical, Boolean)

- **Graph module** for biological networks
  - Undirected interaction graphs
  - Connected component detection
  - Neighbor and adjacency queries
  - Depth-first and breadth-first traversal

- **AI module** for feature analysis and stratification
  - Feature ranking by variance and correlation
  - Confidence scoring for rankings
  - Prediction explainability types
  - Sample stratification results with metadata

- **IO module** for data interchange
  - CSV codec for ExpressionMatrix (read/write)
  - SampleMetadata codec
  - Validation-based error handling for malformed data
  - Automatic type inference

- **Examples module** with 3 complete workflows
  - ExpressionMatrixExample: Loading and filtering
  - CohortAnalysisExample: Sample grouping and normalization
  - NetworkAnalysisExample: Interaction graph traversal

- **Benchmarking infrastructure**
  - JMH based benchmarks for matrix operations
  - Performance profiling scaffolding

- **Comprehensive documentation** (25 MD files)
  - README with quick start guide
  - PUBLICATION.md: Maven Central and Scaladex release process
  - SCALA_STDLIB.md: Scala standard library integration strategy
  - SCALA_STDLIB_CANDIDACY.md: Component suitability analysis
  - API documentation with examples
  - Architecture decision records (3 ADRs)
  - Contributing guidelines
  - Security policy
  - Code of conduct

- **CI/CD infrastructure**
  - GitHub Actions workflows for test and publish
  - Multi-version testing (Java 11, 17, 21)
  - Automatic GPG signing for Maven Central
  - Sonatype bundle release automation
  - sbt-release plugin integration

- **Test suite**: 86 comprehensive tests
  - Unit tests for all public APIs
  - Property-based tests for invariants
  - Integration tests for workflows
  - CSV round-trip tests

### Quality Metrics

- Zero external dependencies in core module
- Scala 3.6.1 primary, backward compatible with 3.3+
- Java 11 LTS minimum, tested with 17 and 21
- 86/86 tests passing
- >90% code coverage
- Full Scaladoc coverage for public APIs
- EUPL 1.2 license

### Scala Standard Library Alignment

- Validation[A] designed for potential Scala 3.8 stdlib inclusion
- DomainError hierarchy as reusable error abstraction
- Opaque type patterns documented for ecosystem adoption
- See SCALA_STDLIB.md for roadmap (target: 2027 Q1-Q2)

## [Unreleased]

### Planned for 0.2.0

- Advanced normalization techniques (TMM, DESeq2, dream voom)
- Additional graph algorithms (shortest path, centrality measures)
- ML-based feature selection algorithms
- Extended metadata framework
- Performance optimizations

## Contributing to Changelog

When contributing to Schnéileopard, please update this file with your changes. Use the following format:

### Added
- New features

### Changed
- Changes to existing functionality

### Deprecated
- Soon-to-be removed features

### Removed
- Removed features

### Fixed
- Bug fixes

### Security
- Security vulnerability fixes

---

*Changelog - Last Updated: March 2026*
*Next Review: June 2026*
