# Troubleshooting Guide

## Compilation Issues

### Error: "No matching overloads for constructor"

**Symptom:** Compilation fails when creating GeneId, SampleId, or other opaque types.

**Cause:** Opaque types require using the apply method or companion object constructor.

**Solution:**
```scala
// Correct
val geneId = GeneId("ENSG00000000003")

// Incorrect
val geneId = new GeneId("ENSG00000000003")  // Does not work
```

### Error: "Cannot find symbol: scala.math"

**Symptom:** Compilation fails in normalization or statistics code.

**Cause:** Missing import or scala.math package not available.

**Solution:**
```scala
import scala.math.*  // Adds sqrt, pow, abs, etc.
```

### Error: "Type mismatch" when using identifiers

**Symptom:** Compiler rejects code trying to use wrong identifier type.

**Cause:** Strong typing prevents mixing GeneId with SampleId, etc. This is intentional.

**Solution:**
```scala
// This correctly catches the error at compile time
val gene = GeneId("G1")
val sample = SampleId("S1")
val cohort = Cohort(CohortId("C1"), Set(gene))  // Type error: GeneId not SampleId
val cohort = Cohort(CohortId("C1"), Set(sample))  // Correct
```

### Warning: "-Ysafe-init is deprecated"

**Symptom:** Build warnings about deprecated compiler flag.

**Cause:** Scala 3.6 deprecated `-Ysafe-init` in favor of `-Wsafe-init`.

**Solution:** Already handled in build.sbt. Update if using different Scala version:
```scala
scalacOptions += "-Wsafe-init"  // For Scala 3.6+
```

## Runtime Errors

### Error: "GeneId cannot be empty"

**Symptom:** IllegalArgumentException when creating identifier.

**Cause:** Empty strings are not valid identifiers.

**Solution:**
```scala
val geneId = GeneId("")  // Throws IllegalArgumentException
val geneId = GeneId("ENSG00000000003")  // Correct
```

### Error: "ExpressionMatrix must have at least one feature"

**Symptom:** ValidationError when creating matrix with zero features.

**Cause:** Expression matrices must have both rows (features) and columns (samples).

**Solution:**
```scala
val features = Vector(GeneId("G1"), GeneId("G2"))  // At least one
val samples = Vector(SampleId("S1"), SampleId("S2"))  // At least one
ExpressionMatrix.validate(features, samples, values)  // Succeeds
```

### Error: "Row count does not match feature count"

**Symptom:** ValidationError with dimension mismatch.

**Cause:** Number of rows in values matrix does not equal number of features.

**Solution:**
```scala
val features = Vector(GeneId("G1"), GeneId("G2"))  // 2 features
val samples = Vector(SampleId("S1"))  // 1 sample
val values = Vector(
  Vector(1.0), // Row 1
  Vector(2.0)  // Row 2
)
// Correct: 2 rows, 2 features
// Incorrect: 3 rows would fail validation
```

### Error: "All rows must have the same number of columns"

**Symptom:** ValidationError with inconsistent row lengths.

**Cause:** Some rows have different sample counts.

**Solution:**
```scala
val samples = Vector(SampleId("S1"), SampleId("S2"), SampleId("S3"))
val values = Vector(
  Vector(1.0, 2.0, 3.0),  // 3 samples
  Vector(4.0, 5.0)        // Only 2 samples - ERROR
)
// All rows must match sample count
val values = Vector(
  Vector(1.0, 2.0, 3.0),
  Vector(4.0, 5.0, 6.0)
)
// Now correct
```

### Error: "Cohort must contain at least one sample"

**Symptom:** ValidationError creating cohort with empty sample set.

**Cause:** Cohorts cannot be empty.

**Solution:**
```scala
val cohort = Cohort(CohortId("C1"), Set())  // Fails validation
val cohort = Cohort(CohortId("C1"), Set(SampleId("S1")))  // Correct
```

### Error: "Phenotype vector length must match samples"

**Symptom:** IllegalArgumentException in rankByCorrelation.

**Cause:** Phenotype vector has different length than sample count.

**Solution:**
```scala
val matrix = ExpressionMatrix(features, Vector(SampleId("S1"), SampleId("S2")))
val phenotype = Vector(1.0, 2.0, 3.0)  // Wrong length - failed
val phenotype = Vector(1.0, 2.0)  // Correct length
```

### Error: "Confidence must be between 0 and 1"

**Symptom:** IllegalArgumentException creating Confidence.

**Cause:** Confidence scores must be normalized between 0 and 1.

**Solution:**
```scala
val conf = Confidence(1.5)  // Fails
val conf = Confidence(0.8)  // Correct
```

## Testing Issues

### Test: "No such file or directory"

**Symptom:** File I/O tests fail in CI environment.

**Cause:** Temporary directory permissions or path issues.

**Solution:**
```scala
private def withTempFile[T](f: File => T): T =
  val file = Files.createTempFile("schneileopard", ".csv").toFile
  try f(file)
  finally file.delete()  // Always clean up
```

### Test: "CSV parsing failed"

**Symptom:** ExpressionMatrixCodec.readCSV returns Invalid.

**Cause:** Malformed CSV or missing required columns.

**Solution:**
```
Ensure CSV has:
1. First row with headers including "feature" column
2. All values parseable as Double
3. Consistent column count in all rows
```

## Build and Dependency Issues

### Error: "Scalafmt: Invalid config: Default dialect is deprecated"

**Symptom:** Scalafmt configuration error during build.

**Cause:** .scalafmt.conf missing dialect specification.

**Solution:** Update .scalafmt.conf:
```
version = "3.7.17"
runner.dialect = scala3  # Add this line
```

### Error: "sbt: command not found"

**Symptom:** sbt not available in terminal.

**Cause:** sbt not installed or not in PATH.

**Solution:**
```bash
# Install sbt
# macOS
brew install sbt

# Linux (Ubuntu/Debian)
sudo apt-get install sbt

# Or download from https://www.scala-sbt.org/download.html
```

### Error: "Compilation failed: symbol not found"

**Symptom:** Compilation fails with missing classes/methods.

**Cause:** Dependency not resolved or incorrect version.

**Solution:**
```bash
sbt clean update compile  # Force dependency refresh
```

### Warning: "Keys are not used by any other settings/tasks"

**Symptom:** Build warnings about unused keys.

**Cause:** Harmless warnings from build.sbt configuration.

**Solution:** Can ignore or add to excludeLintKeys in build.sbt.

## CSV I/O Issues

### Error: "CSV file is empty"

**Symptom:** ParseError when reading CSV.

**Cause:** CSVfile has no data rows.

**Solution:** Ensure CSV has at least:
```
feature,S1,S2
G1,1.5,2.0
```

### Error: "Missing required column: feature"

**Symptom:** ParseError reading ExpressionMatrix CSV.

**Cause:**  First column not named "feature".

**Solution:** Rename first column to "feature":
```
feature,S1,S2,S3  # Correct
ENSG1,1.5,2.0,2.2
```

### Error: "Number format exception"

**Symptom:** ParseError reading CSV with non-numeric values.

**Cause:** Expression matrix contains non-Double values.

**Solution:** Ensure all data cells contain valid numbers:
```
feature,S1,S2
G1,1.5,2.0      # Correct
G2,abc,2.0      # Fails - "abc" not parseable
```

## Performance Issues

### Symptom: "Out of memory" or "Heap space exceeded"

**Cause:** Large expression matrix or excessive copying.

**Solution:**
```bash
# Increase heap size
export _JAVA_OPTIONS="-Xmx4g"
sbt test
```

Or in sbt:
```
javaOptions in run += "-Xmx4g"
```

### Symptom: Slow matrix operations

**Cause:** Large matrices with n x m operations (O(nm) or worse).

**Solution:**
- Filter to high-variance features first
- Process in cohorts if possible
- Use normalization to pre-process data

## Platform-Specific Issues

### Windows: Path issues with spaces or special characters

**Symptom:** Build fails with path names containing special characters.

**Cause:** Special characters or accented characters in paths.

**Solution:**
```bash
# Use Forward slashes in paths
cd /c/Users/yunus/Desktop/Schnéileopard
```

### macOS: Java version conflicts

**Symptom:** "Java not found" or version mismatch.

**Solution:**
```bash
/usr/libexec/java_home -V  # List Java versions
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### Linux: Permission denied

**Symptom:** "Permission denied" when running sbt scripts.

**Solution:**
```bash
chmod +x sbt
./sbt build
```

## Getting Additional Help

### Before Asking for Help

1. Check error message carefully for line numbers
2. Search closed issues on GitHub
3. Review relevant Scaladoc
4. Test with minimal reproducible example

### Where to Ask

- **GitHub Issues:** Bug reports and feature requests
- **Discussions:** General questions and usage advice
- **Email:** olaf.laitinen@uni.lu for research questions
- **Documentation:** See README.md and INSTALLATION.md

### Reporting an Issue

Include:
1. Scala version: `scala -version`
2. Sbt version: `sbt sbtVersion`
3. Java version: `java -version`
4. Minimal reproducible code
5. Error message and stack trace
6. Steps to reproduce

## Debugging Tips

### Enable verbose logging

```bash
sbt -v test  # Verbose output
sbt -warn test  # Show warnings
```

### Print debugging in code

```scala
println(s"Debug: ${matrix.featureCount} features")
```

### Use Scala REPL

```bash
sbt console
```

Then interactively test code:
```scala
scala> import io.github.olaflaitinen.schneileopard.core.*
scala> val id = GeneId("G1")
scala> id.value
```

### Check scaladoc locally

```bash
sbt doc
# Then open target/scala-3.6.1/api/index.html
```

---

*Troubleshooting Guide - Last Updated: March 2026*
*Next Review: June 2026*
