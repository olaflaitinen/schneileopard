# Dependencies

This document describes the external dependencies of Schnéileopard and the rationale for each.

## Core Module Dependencies

The `schneileopard-core` module depends only on the Scala standard library and test dependencies.

**Runtime Dependencies:**
- None (Scala 3.6.1 standard library only)

**Test Dependencies:**
- org.scalatest:scalatest (3.2.18) - Testing framework
- org.scalacheck:scalacheck (1.17.1) - Property-based testing
- org.scalatestplus:scalacheck (3.2.18.0) - ScalaTest/ScalaCheck integration

**Rationale:** Keeping the core module free of external dependencies ensures minimal API surface and maximum stability. The `Validation` type, identifiers, and error types are pure Scala code with no external runtime requirements.

## Omics Module Dependencies

The `omnics` module depends on `schneileopard-core`.

**Runtime Dependencies:**
- None additional beyond core

**Test Dependencies:**
- Same as core

**Rationale:** `ExpressionMatrix`, `Cohort`, and normalization functions are all implemented in pure Scala without external libraries.

## Graph Module Dependencies

The `graph` module depends on `schneileopard-core`.

**Runtime Dependencies:**
- None additional beyond core

**Test Dependencies:**
- Same as core

**Rationale:** Graph operations including connected components are implemented with standard collections. The module uses only Scala immutable data structures.

## AI Module Dependencies

The `ai` module depends on `schneileopard-core`, `schneileopard-omics`, and `schneileopard-graph`.

**Runtime Dependencies:**
- None additional beyond core modules

**Test Dependencies:**
- Same as core

**Rationale:** Feature ranking and stratification are implemented using only Scala standard library math operations. All explanations are typed values, not strings or serialized objects.

## IO Module Dependencies

The `io` module depends on `schneileopard-core` and `schneileopard-omics`.

**Runtime Dependencies:**
- com.github.tototoshi:scala-csv (1.4.1)

**Test Dependencies:**
- Same as core

**Rationale:** CSV parsing is a well-established need in bioinformatics. The `scala-csv` library provides mature, dependency-light CSV handling. It has no external dependencies of its own (zero-dependency library).

## Benchmark Module Dependencies

The `bench` module depends on all core modules.

**Build Dependencies:**
- org.openjdk.jmh:jmh-core (1.37)
- org.openjdk.jmh:jmh-generator-annprocess (1.37)

**Rationale:** JMH (Java Microbenchmark Harness) is the standard for Java/Scala performance benchmarking. It is not included in runtime artifacts.

## Build System Dependencies

**Build Tool:**
- sbt 1.10.1

**Plugins (in project/plugins.sbt):**
- com.github.sbt:sbt-release (1.4.0) - Release management
- com.github.sbt:sbt-sonatype (3.11.0) - Maven Central publishing
- com.github.sbt:sbt-pgp (2.2.1) - GPG signing for artifacts
- org.scalameta:sbt-scalafmt (2.4.6) - Code formatting

**Rationale:** These are standard Scala ecosystem tools for release engineering and publishing to Maven Central.

## Transitive Dependencies

**Scala-csv transitive:**
- None (zero dependencies)

**ScalaTest transitive:**
- scala-xml
- scala-parser-combinators

**Total runtime transitive dependencies:** 0 (excluding test dependencies)

**Total build transitive dependencies:** Various but all standard ecosystem tools

## Version Strategy

### Stable versions:
- Scala 3.6.1: Latest stable Scala 3 release
- JDK 11+: Long-term support Java versions supported
- sbt 1.10.1: Latest stable sbt

### Upgrade Policy:
- Dependencies are updated regularly for security and bug fixes
- Major version upgrades are coordinated with Schnéileopard releases
- Breaking dependency changes require major version bumps
- Compatibility tested with LTS Java versions (11, 17, 21)

## Dependency Lock

For reproducible builds, use dependency lock files:

```bash
export COURSIER_CACHE=~/.cache/coursier
sbt dependencyLockWrite
```

Lock file: `project/build.lock`

## Security Considerations

All dependencies are from reputable, well-maintained projects:
- Testing frameworks: ScalaTest ecosystem
- Publishing: Official sbt plugins
- CSV: Mature, actively maintained library with good security track record

For security concerns in dependencies, see [SECURITY.md](SECURITY.md).

## Adding New Dependencies

Request for new dependencies should include:

1. **Justification:** Why is this dependency needed
2. **Alternatives:** What other options were considered
3. **Impact:** How it affects the API surface and artifact size
4. **Stability:** Maintenance status and compatibility guarantees
5. **Licensing:** License compatibility with EUPL 1.2

New runtime dependencies require discussion and consensus.

## Replacing Dependencies

There is a preference for:
- Lighter dependencies over heavier ones
- Zero-dependency libraries when possible
- Well-maintained projects with active communities
- Compatibility with Scala 3 and recent JVM versions

## Known Limitations

- scala-csv does not support streaming large files (load entire CSV into memory)
- Benchmarking requires JMH, not suitable for embedded environments
- Math operations use only standard library (limited to what Scala provides)

## Future Considerations

Potential future dependencies (if decision is made to add them):
- ONNX Runtime for model inference (optional AI module)
- Apache Parquet for columnar data (optional EDA module)
- GraphFrames for advanced graph analysis (optional graph module)

These would be added as optional modules with separate publication, not pulling in to core.

---

*Dependencies - Last Updated: March 2026*
*Next Review: June 2026*
