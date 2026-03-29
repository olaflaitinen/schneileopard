# Development Roadmap

Schnéileopard development roadmap balances research priorities with user community feedback. This roadmap is subject to change based on user needs and contributions.

## Current Status

**Version:** 0.1.0-SNAPSHOT (development)
**Release Target:** Q2 2026
**Stability:** Core APIs stable, ready for community feedback

## Version 0.1.x (Current - Early 2026)

### 0.1.0 (April 2026) - Initial Public Release

- [x] Core module with identifiers and validation
- [x] Omics module with matrices and cohorts
- [x] Graph module for pathways
- [x] AI module with feature ranking
- [x] IO module with CSV support
- [x] Examples and benchmarks
- [x] Comprehensive documentation
- [x] GitHub Actions CI/CD
- [x] Maven Central publication ready

**API Stability:** Public APIs frozen for 0.1.x

**Known Limitations:**
- Single-threaded operations only
- Full dataset loading into memory (no streaming)
- Basic AI implementations (no deep learning)
- Limited pathway annotation sources

### 0.1.1 (May 2026) - Stability Update

**Focus:** Bug fixes, documentation refinement, community feedback

**Planned:**
- [ ] Enhanced error messages with recovery suggestions
- [ ] Performance optimizations for large matrices
- [ ] Additional Scaladoc examples
- [ ] Extended test coverage (target: 95%)
- [ ] Benchmark improvements

### 0.1.2+ (June 2026) - Maintenance Releases

**Focus:** Dependency updates, security patches

**Trigger Events:**
- Critical bugs reported by users
- Security advisories in dependencies
- Java/Scala LTS updates

---

## Version 0.2.x (Mid-Late 2026)

### 0.2.0 (July 2026) - Extended AI Layer

**Theme:** Richer machine learning interfaces

**Planned Features:**

1. **Advanced Stratification**
   - Hierarchical clustering interface
   - Consensus clustering support
   - Silhouette-based validation
   - Type-safe cluster membership

2. **Explainability Enhancements**
   - SHAP-like feature attribution
   - Shapley value estimation (lightweight)
   - Interaction importance scoring
   - Model-agnostic explanations

3. **Curve Fitting and Optimization**
   - Spline interpolation for continuous phenotypes
   - Parameter optimization interfaces
   - Cross-validation helpers

4. **Extended Normalization**
   - RUVseq-like batch correction interface
   - Combat-like batch adjustment
   - TMM normalization for count data
   - EdgeR-like dispersion estimation

**Compatibility:** Minor version (0.2.0 may have new methods on existing types, no breaking changes)

**Target API Size:** 80-100 public types/methods

### 0.2.1-0.2.x (Late 2026)

- [ ] Additional AI algorithms based on user feedback
- [ ] Performance benchmarking suite publication
- [ ] Extended examples covering real-world workflows

---

## Version 1.0.0 (Q4 2026)

### 1.0.0 (December 2026) - Production-Ready Release

**Theme:** Stability, maturity, long-term support

**Planned Major Features:**

1. **Multi-Omics Integration**
   - Unified analysis interface for transcriptomic + genomic + proteomic
   - Cross-modality intersection types
   - Modality-aware normalization

2. **Causal Inference**
   - Causal graph types (DAG)
   - Intervention simulation interfaces
   - Confounding adjustment types
   - Identifiability checking

3. **Temporal Dynamics**
   - Time-series expression types
   - Trajectory inference interfaces
   - Dynamics scoring functions

4. **Biological Validation**
   - Enrichment analysis interface
   - Pathway overrepresentation types
   - Database integration patterns

5. **Streaming and Performance**
   - Iterator-based matrix operations
   - Out-of-core computation support
   - Parallel analysis interfaces
   - Memory-efficient algorithms

**Breaking Changes Policy:**
- For 1.0.0, deprecated APIs from 0.2.x removed
- Clear migration guide provided (MIGRATION_0.2_to_1.0.md)
- Minimum 2-month deprecation period before removal

**API Guarantee:**
- Binary compatibility within 1.x
- Source compatibility (unless major version bump)
- 3+ year long-term support for 1.0.x series

---

## Version 1.1.x (2027)

**Theme:** Production extensions

### Potential Additions:

1. **OpenAPI/REST Layer** (optional module)
   - HTTP API for Schnéileopard operations
   - Server module for remote analysis
   - Client library for programmatic access

2. **ONNX Model Support** (optional module)
   - Inference interface for ONNX models
   - Model zoo integration
   - Prediction pipeline

3. **Database Backends** (optional module)
   - Parquet file support
   - HDF5 matrices
   - Database connectors

4. **Advanced Analytics** (optional modules)
   - Statistical modeling
   - Bayesian inference
   - Differential expression analysis

---

## Version 2.0.0 (2028+)

**Theme:** Next-generation capabilities

**Considerations for Major Version:**
- Scala 4 compatibility (if released)
- New domain types based on research findings
- Architectural improvements learned from 0.x/1.x
- Community-driven feature prioritization

---

## Research Directions (Post 2.0)

### Federated Learning
- Multi-site cohort analysis
- Privacy-preserving computation
- Secure aggregation protocols

### Single-Cell Specifics
- Single-cell RNA-seq types
- Cell annotation helpers
- Trajectory inference

### Spatial Transcriptomics
- Spatial coordinate types
- Tissue context modeling
- Spatial interaction analysis

### Drug Response Prediction
- Pharmacogenomics data types
- Drug-gene-disease networks
- Biomarker discovery

---

## Community and User-Driven Features

### Open for Discussion
- Integration with Galaxy/Bioconda
- Jupyter notebook plugins
- Workflow language integration (CWL, WDL, Nextflow)
- R interoperability (via Posit Interop)

### Suggested by Research Community
1. QTL analysis support
2. GWAS integration
3. Protein structure prediction interfaces
4. Immunology data types
5. Microbiome analysis helpers

### Contribution Welcome
- Experimental modules can be added by community
- Graduated to core after sufficient usage and testing
- Credit and co-authorship for substantive contributions

---

## Dependency and Platform Evolution

### Scala Evolution
- **3.6.1** (current): Current target
- **3.7+** (late 2026): Consider upgrade
- **Scala 4** (TBD): Plan compatibility research

### Java Evolution
- **Java 11** (current minimum): Support through 2026
- **Java 21** (current LTS): Full support
- **Java 23+** (future): Plan for adoption

### Build Tools
- **sbt 1.10+**: Current and future (no Maven migration planned)

---

## Documentation Roadmap

### Existing (In Repository)
- Getting started guide
- Architecture documentation
- API reference
- CONTRIBUTING guide

### Planned
- **0.1.0+:** Tutorial series (3-5 notebooks)
- **0.2.0+:** Integration examples with real data
- **1.0.0+:** Complete cookbook (50+ recipes)
- **1.0.0+:** Research publication(s)

### Video Content (Aspirational)
- Introduction overview (10 min)
- Feature walkthrough (20 min)
- Advanced techniques (30 min+)

---

## Scala Standard Library Integration

Schnéileopard's core validation framework and error types are designed for potential inclusion in the Scala standard library. This represents a longer-term goal to elevate error handling practices across the Scala ecosystem.

### Stdlib Integration Roadmap

**Q2-Q3 2026: Community Testing and Feedback**
- Release schneileopard-core on Maven Central
- Gather feedback from library authors and production users
- Refine API based on real-world usage patterns

**Q3-Q4 2026: Scala Center Engagement**
- Present to Scala Center and improvement process committees
- Prepare Scala Enhancement Proposal (SEP)
- Discuss integration strategy and timeline
- Document rationale for inclusion

**2027: Formal Proposal and Integration**
- Submit SEP to Scala Improvement Process
- Scala compiler team technical review
- Plan integration into Scala 3.8 or 3.9
- Code migration and packaging adjustment

**2027 Q1-Q2: Scala Release**
- Include in Scala 3.8+ standard library
- Code becomes `scala.util.validation.*`
- Backward compatibility with Maven Central version
- Community migration period begins

**See SCALA_STDLIB.md and SCALA_STDLIB_CANDIDACY.md for detailed strategy.**

---

## Release Schedule and Cadence

### Committed Schedule
- **Patch (0.1.x, 1.0.x):** As needed for bugfixes, max 60 days
- **Minor (0.2.x, 1.1.x):** Typically 8-12 weeks between releases
- **Major (1.0, 2.0):** Minimum 6 months between planning and release

### Release Process
1. Feature freeze (2 weeks before release)
2. Release candidate period (1 week)
3. Release tagging and publication
4. Announcement to community

---

## Roadmap Governance

### How This Roadmap Can Change
- Community feature requests (GitHub Issues)
- Research breakthroughs informing new capabilities
- Dependency ecosystem changes
- User adoption patterns
- Maintainer availability

### How to Influence Roadmap
1. **GitHub Issues** - Suggest features or report limitations
2. **GitHub Discussions** - Discuss future directions
3. **Contributing** - Submit code for new features
4. **Email** - olaf.laitinen@uni.lu for significant proposals

### Feedback Loop
- Quarterly roadmap reviews (approx. May, Aug, Nov)
- Community votes on feature priorities (via GitHub)
- Annual planning meeting (January)

---

## Success Metrics

- **Adoption:** 100+ unique GitHub cloners within 12 months
- **Citations:** 10+ research papers using or mentioning Schnéileopard
- **Contributors:** 5+ active community contributors
- **Stability:** <1 critical bug per major release
- **Performance:** Matrix operations at least 10x faster than equivalent Python
- **Documentation:** 90%+ Scaladoc coverage
- **Tests:** 85%+ code coverage

---

## Known Technical Debts

Planned improvements for future versions:

1. **Streaming:** No iterator-based matrix operations yet (0.1.x)
2. **Parallelism:** No built-in multi-threading (plan for 1.0.0)
3. **Caching:** No memoization of expensive operations (plan for 0.2.x)
4. **Lazy Evaluation:** Matrices are eager, not lazy (could change in 1.x)
5. **Type Classes:** Limited type class usage (could expand in 1.x)

---

## Communication

- **GitHub Issues:** Feature requests, bug reports
- **Discussions:** Design debates, roadmap feedback
- **Email:** olaf.laitinen@uni.lu
- **Research Collaborations:** Special proposals

---

*Roadmap Last Updated: March 2026*
*Next Review: June 2026*
