# Installation Guide

## System Requirements

- Java Development Kit (JDK) 11 or later (tested with 17 and 21)
- Scala 3.6.1 (managed by sbt)
- sbt 1.10.1 or later

## Installation Methods

### 1. Using Maven Central (Recommended)

Add the following to your `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-omics" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-graph" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-ai" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-io" % "0.1.0"
)
```

For Maven, add to `pom.xml`:

```xml
<dependencies>
  <dependency>
    <groupId>io.github.olaflaitinen</groupId>
    <artifactId>schneileopard-core_3</artifactId>
    <version>0.1.0</version>
  </dependency>
  <dependency>
    <groupId>io.github.olaflaitinen</groupId>
    <artifactId>schneileopard-omics_3</artifactId>
    <version>0.1.0</version>
  </dependency>
</dependencies>
```

### 2. Building from Source

Clone the repository:

```bash
git clone https://github.com/olaflaitinen/schneileopard.git
cd schneileopard
```

Build the library:

```bash
sbt clean compile
```

Run tests:

```bash
sbt test
```

Package artifacts:

```bash
sbt package
```

Install locally (for local use):

```bash
sbt publishLocal
```

### 3. Using Gradle

Add to `build.gradle`:

```gradle
dependencies {
  implementation 'io.github.olaflaitinen:schneileopard-core_3:0.1.0'
  implementation 'io.github.olaflaitinen:schneileopard-omics_3:0.1.0'
}
```

## Module Selection

Schnéileopard is organized into optional modules. Choose what you need:

- **schneileopard-core**: Domain types, identifiers, validation (required)
- **schneileopard-omics**: Expression matrices, cohots, metadata (most users need this)
- **schneileopard-graph**: Pathway and network types (if using pathway analysis)
- **schneileopard-ai**: Feature ranking and explainability (if using AI features)
- **schneileopard-io**: CSV/TSV parsing (if reading tabular data)

Minimal installation:

```scala
libraryDependencies += "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0"
```

Full installation:

```scala
libraryDependencies ++= Seq(
  "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-omics" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-graph" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-ai" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-io" % "0.1.0"
)
```

## Verification

Verify installation by creating a test file `Test.scala`:

```scala
import io.github.olaflaitinen.schneileopard.core.*

object Test:
  def main(args: Array[String]): Unit =
    val geneId = GeneId("ENSG00000000003")
    println(s"GeneId created: ${geneId.value}")
```

Compile and run:

```bash
scalac -cp <path-to-schneileopard-core-jar> Test.scala
scala -cp <path-to-schneileopard-core-jar>:. Test
```

## Troubleshooting

### Java Version Issues

If you encounter Java version compatibility errors:

```bash
sbt -java-home /usr/lib/jvm/openjdk-17
```

### Dependency Resolution

If Maven Central artifacts are not found, ensure you have internet access and:

```bash
sbt clean update
```

### Building Documentation

Generate Scaladoc:

```bash
sbt doc
```

Output is in `target/scala-3.6.1/api/`.

## Next Steps

- Read the Quick Start in [README.md](README.md)
- Explore examples in `modules/examples/src/main/scala`
- Review architecture in [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md)
- Check API documentation via Scaladoc

## Getting Help

- Report issues: https://github.com/olaflaitinen/schneileopard/issues
- Ask questions: GitHub Discussions (when enabled)
- Security concerns: See [SECURITY.md](SECURITY.md)

---

*Installation Guide - Last Updated: March 2026*
*Next Review: June 2026*
