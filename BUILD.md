# Schnéileopard Development and Build Guide

## Overview

This document provides comprehensive instructions for building, testing, developing, and releasing Schnéileopard from source.

## Prerequisites

### Required
- Java Development Kit (JDK) 11 or later
  - Tested with: OpenJDK 17.0.17, OpenJDK 21
  - Download: https://adoptium.net/
- sbt 1.10.1 or later
  - Download: https://www.scala-sbt.org/download.html
- Git
- Scala 3.6.1 (managed automatically by sbt)

### Optional
- scalafmt (for code formatting, included via sbt plugin)
- Scala CLI (for quick script testing)
- Docker (for containerized builds)
- GPG (for signing releases)

## Repository Setup

### Clone the Repository

```bash
git clone https://github.com/olaflaitinen/schneileopard.git
cd schneileopard
```

### Verify Java Installation

```bash
java -version
# Expected: Java 11+

javac -version
# Expected: javac 11+
```

### Verify sbt Installation

```bash
sbt sbtVersion
# Should output sbt version without errors
```

## Building from Source

### Clean Build

Remove all build artifacts and rebuild from scratch:

```bash
sbt clean compile
```

### Incremental Build

Compile only changed sources (faster):

```bash
sbt compile
```

### Build Specific Module

Build only one module:

```bash
sbt "core/compile"
sbt "omics/compile"
sbt "graph/compile"
sbt "ai/compile"
sbt "io/compile"
```

### Build All Artifacts

Create JAR files for all modules:

```bash
sbt package
```

Output JARs location:
```
modules/*/target/scala-3.6.1/*-0.1.0.jar
```

## Running Tests

### Run All Tests

```bash
sbt test
```

### Run Tests for Specific Module

```bash
sbt "core/test"
sbt "omics/test"
```

### Run Specific Test Class

```bash
sbt "testOnly io.github.olaflaitinen.schneileopard.core.IdentifiersTest"
```

### Run Tests with Coverage

```bash
# Requires: sbt-jacoco plugin (add to project/plugins.sbt)
sbt jacoco
```

### Continuous Testing

Run tests automatically on file changes:

```bash
sbt ~test
```

## Code Quality

### Format Code

Auto-format all Scala files using scalafmt:

```bash
sbt scalafmtAll
```

This runs automatically on compile due to `scalafmtOnCompile := true` in build.sbt.

### Check Formatting

Verify code is formatted without changing files:

```bash
sbt scalafmtCheckAll
```

### Linting

Check for unused imports and variables:

```bash
sbt clean compile
```

Watch for warnings and address them.

### Generate Documentation

Generate Scaladoc for all public APIs:

```bash
sbt doc
```

Output location:
```
target/scala-3.6.1/api/index.html
```

Open in browser:
```bash
open target/scala-3.6.1/api/index.html  # macOS
xdg-open target/scala-3.6.1/api/index.html  # Linux
start target/scala-3.6.1/api/index.html  # Windows
```

## Examples and Demonstrations

### Run Example Application

```bash
sbt "examples/run"
```

This executes the `@main def runExamples()` function which demonstrates:
1. Expression matrix creation and normalization
2. Cohort analysis and feature ranking
3. Network and pathway analysis

### Run Interactive REPL

```bash
sbt console
```

Then in the REPL:

```scala
scala> import io.github.olaflaitinen.schneileopard.core.*
scala> import io.github.olaflaitinen.schneileopard.omics.*
scala> val id = GeneId("ENSG00000000003")
scala> val matrix = ExpressionMatrix(Vector(id), Vector(SampleId("S1")), Vector(Vector(1.5)))
scala> matrix.featureCount
res0: Int = 1
```

Exit with:
```scala
scala> :quit
```

## Benchmarking

### Setup JMH Benchmarks

The benchmark module uses Java Microbenchmark Harness (JMH). Build benchmark artifacts:

```bash
sbt "bench/assembly"
```

Or use jmh-maven-plugin if building with Maven.

### Run Benchmarks

```bash
java -jar modules/bench/target/scala-3.6.1/schneileopard-bench-0.1.0-jmh.jar
```

Common JMH options:

```bash
# Run specific benchmark
java -jar ... ".*MatrixCreation.*"

# Set warmup/measurement iterations
java -jar ... -w 5 -i 10

# Output results as CSV
java -jar ... -rff results.csv -rf csv
```

## Dependency Management

### Add New Dependency

1. Edit `build.sbt`:

```scala
val myDeps = Seq(
  "org.example" %% "my-library" % "1.0.0"
)

lazy val myModule = project
  .settings(
    libraryDependencies ++= myDeps
  )
```

2. Update and reload in sbt:

```bash
sbt reload
sbt update
sbt compile
```

### Resolve Dependency Conflicts

View dependency tree:

```bash
sbt dependencyTree
```

Or for a specific module:

```bash
sbt "core/dependencyTree"
```

### Lock Dependencies

Create reproducible builds with dependency lock files:

```bash
sbt dependencyLockWrite
```

This creates `project/build.lock`.

Use locked dependencies:

```bash
sbt compile
# sbt automatically uses project/build.lock
```

## Continuous Integration

### Local CI Simulation

Run all checks locally before pushing:

```bash
sbt clean scalafmtCheckAll compile test doc
```

### GitHub Actions

The repository includes CI workflows in `.github/workflows/`:

- **ci.yml** - Runs on every push and PR
  - Compile
  - Run tests
  - Check formatting
  - Generate documentation (Java 11, 17, 21)

- **release.yml** - Runs on version tags (v*)
  - Same checks as ci.yml
  - Sign with GPG
  - Publish to Maven Central
  - Create GitHub release

## Publishing and Releasing

### Prepare for Release

1. Update version in `build.sbt`:

```scala
ThisBuild / version := "0.2.0"  # From 0.1.0
```

2. Update CHANGELOG.md:

```markdown
## [0.2.0] - 2026-04-15

### Added
- New feature X
- New feature Y

### Fixed
- Bug fix A

### Changed
- Breaking change
```

3. Commit changes:

```bash
git add build.sbt CHANGELOG.md
git commit -m "chore: prepare 0.2.0 release"
```

### Create Release Tag

```bash
git tag -a v0.2.0 -m "Release version 0.2.0"
git push origin v0.2.0
```

This triggers `.github/workflows/release.yml` which:
- Tests the code
- Signs artifacts with GPG
- Publishes to Maven Central
- Creates GitHub release

### Manual Publishing (Local)

For testing without GitHub:

```bash
sbt publishLocal
```

Published to:
```
~/.ivy2/local/io.github.olaflaitinen/
```

### Check Published Artifact

After Maven Central sync (can take 2-4 hours):

```bash
https://search.maven.org/artifact/io.github.olaflaitinen/schneileopard-core_3/0.2.0/jar
```

## Development Workflow

### Feature Development

1. Create feature branch:

```bash
git checkout -b feature/my-feature
```

2. Make changes and commit:

```bash
git add .
git commit -m "feat: add my feature"
```

3. Run local checks:

```bash
sbt clean scalafmtAll compile test doc
```

4. Push and create PR:

```bash
git push origin feature/my-feature
```

### Bug Fixes

Similar to features, but:

```bash
git checkout -b fix/bug-name
# ... fix ...
git commit -m "fix: resolve issue with X"
```

### Code Review Checklist

Before merging, verify:

- [ ] Tests pass (86/86)
- [ ] Code formatted
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] No warnings in build
- [ ] Scaladoc complete
- [ ] No breaking changes (or documented)

## Troubleshooting

### Out of Memory

Increase heap size:

```bash
export _JAVA_OPTIONS="-Xmx4g -Xms1g"
sbt compile
```

Or in sbt shell:

```scala
javaOptions in run += "-Xmx4g"
```

### Slow Compilation

1. Clear cache:

```bash
rm -rf target/
rm -rf ~/.cache/coursier  # Optional
```

2. Use incremental compilation:

```bash
sbt compile  # Incremental
# vs
sbt clean compile  # Full rebuild
```

### Stale Dependencies

Force refresh:

```bash
sbt clean update compile
```

### Scala Version Mismatch

Verify Scala version:

```bash
sbt scalaVersion
# Should output: 3.6.1
```

If wrong, check `build.sbt`:

```scala
ThisBuild / scalaVersion := "3.6.1"
```

## Performance Tips

1. **Use -Xtarget flag** for faster compilation (Scala 3.13+)
2. **Enable sbt server** for faster sbt startup:
   ```bash
   sbt -Dsbt.server.forcestart=true
   ```
3. **Use local caching**:
   ```bash
   export COURSIER_CACHE=~/.cache/coursier
   ```
4. **Parallel tests**:
   ```bash
   sbt "set parallelExecution in Test := true" test
   ```

## IDE Setup

### IntelliJ IDEA

1. Install Scala plugin
2. Open project directory
3. Mark as sbt project
4. Configure SDK: Java 17+

### VS Code

1. Install Scala Metals extension
2. Open project directory
3. Follow Metals setup prompts
4. Open Scala file to initialize

### Emacs / Vim

Use Metalsfor LSP support or ENSIME (older).

## Advanced Topics

### Custom JVM Options

Create `.jvmopts` file in project root:

```
-Xmx4g
-Xms1g
-XX:+UseG1GC
-XX:+ParallelRefProcEnabled
```

### Compiler Flags

In `build.sbt`:

```scala
ThisBuild / scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-encoding", "UTF-8"
)
```

### Build from Scripted Tests

Test sbt plugins locally before publishing:

```bash
sbt scripted
```

## Further Reading

- [sbt Documentation](https://www.scala-sbt.org/1.x/docs/index.html)
- [Scala 3 Documentation](https://docs.scala-lang.org/scala3/)
- [Maven Central Publishing](https://central.sonatype.org/publish/publish-guide/)
- [Schnéileopard CONTRIBUTING.md](CONTRIBUTING.md)

---

*Development and Build Guide - Last Updated: March 2026*
*Next Review: June 2026*
