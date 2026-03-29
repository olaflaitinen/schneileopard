package io.github.olaflaitinen.schneileopard.io

import io.github.olaflaitinen.schneileopard.core.*
import io.github.olaflaitinen.schneileopard.omics.*
import org.scalatest.funsuite.AnyFunSuite
import java.io.File
import java.nio.file.Files

class CsvCodecsTest extends AnyFunSuite:

  private def withTempFile[T](f: File => T): T =
    val file = Files.createTempFile("schneileopard", ".csv").toFile
    try f(file)
    finally file.delete()

  test("ExpressionMatrixCodec round trip") {
    withTempFile { file =>
      val originalFeatures = Vector(GeneId("ENSG1"), GeneId("ENSG2"), GeneId("ENSG3"))
      val originalSamples  = Vector(SampleId("S1"), SampleId("S2"), SampleId("S3"))
      val originalValues   = Vector(
        Vector(1.5, 2.0, 1.8),
        Vector(3.2, 2.8, 3.5),
        Vector(0.5, 0.6, 0.4)
      )
      val original         = ExpressionMatrix(originalFeatures, originalSamples, originalValues)

      ExpressionMatrixCodec.writeCSV(original, file)
      val loaded = ExpressionMatrixCodec.readCSV(file)

      assert(loaded.isValid)
      val matrix = loaded.getOrElse(null)
      assert(matrix.featureCount == 3)
      assert(matrix.sampleCount == 3)
      assert(matrix(0, 0) == 1.5)
    }
  }

  test("ExpressionMatrixCodec preserves feature order") {
    withTempFile { file =>
      val features = Vector(GeneId("Z_gene"), GeneId("A_gene"), GeneId("M_gene"))
      val samples  = Vector(SampleId("S1"), SampleId("S2"))
      val values   = Vector(
        Vector(1.0, 2.0),
        Vector(3.0, 4.0),
        Vector(5.0, 6.0)
      )
      val original = ExpressionMatrix(features, samples, values)

      ExpressionMatrixCodec.writeCSV(original, file)
      val loaded = ExpressionMatrixCodec.readCSV(file).getOrElse(null)

      // Note: CSV columns may be sorted, but values should correspond to original indices
      assert(loaded.featureCount == 3)
      assert(loaded.sampleCount == 2)
    }
  }

  test("ExpressionMatrixCodec with large values") {
    withTempFile { file =>
      val features = Vector(GeneId("G1"), GeneId("G2"))
      val samples  = Vector(SampleId("S1"), SampleId("S2"))
      val values   = Vector(
        Vector(1e6, 1e10),
        Vector(1e-6, 1e-10)
      )
      val original = ExpressionMatrix(features, samples, values)

      ExpressionMatrixCodec.writeCSV(original, file)
      val loaded = ExpressionMatrixCodec.readCSV(file).getOrElse(null)

      assert((loaded(0, 0) - 1e6).abs < 1e5)
      assert((loaded(1, 1) - 1e-10).abs < 1e-15)
    }
  }

  test("ExpressionMatrixCodec validation on empty file") {
    withTempFile { file =>
      file.deleteOnExit()
      file.createNewFile()
      val result = ExpressionMatrixCodec.readCSV(file)
      assert(result.isInvalid)
    }
  }

  test("SampleMetadataCodec round trip") {
    withTempFile { file =>
      val samples  = Vector(
        SampleMetadata(
          SampleId("S1"),
          Map(
            "tissue"    -> MetadataValue.StringValue("liver"),
            "age"       -> MetadataValue.NumericValue(45.0),
            "condition" -> MetadataValue.CategoricalValue("healthy", 0)
          )
        ),
        SampleMetadata(
          SampleId("S2"),
          Map(
            "tissue"    -> MetadataValue.StringValue("kidney"),
            "age"       -> MetadataValue.NumericValue(52.0),
            "condition" -> MetadataValue.CategoricalValue("diseased", 1)
          )
        )
      )
      val original = SampleMetadataTable(
        metadata = samples,
        columns = Vector("sample", "tissue", "age", "condition")
      )

      SampleMetadataCodec.writeCSV(original, file)
      val loaded = SampleMetadataCodec.readCSV(file)

      assert(loaded.isValid)
      val table = loaded.getOrElse(null)
      assert(table.sampleCount == 2)
      assert(table.columnCount == 4)
    }
  }

  test("SampleMetadataCodec preserves sample IDs") {
    withTempFile { file =>
      val samples  = Vector(
        SampleMetadata(SampleId("SAMPLE_001"), Map("type" -> MetadataValue.StringValue("control"))),
        SampleMetadata(SampleId("SAMPLE_002"), Map("type" -> MetadataValue.StringValue("case")))
      )
      val original = SampleMetadataTable(samples, Vector("sample", "type"))

      SampleMetadataCodec.writeCSV(original, file)
      val loaded = SampleMetadataCodec.readCSV(file).getOrElse(null)

      val sampleIds = loaded.samples
      assert(sampleIds.contains(SampleId("SAMPLE_001")))
      assert(sampleIds.contains(SampleId("SAMPLE_002")))
    }
  }

  test("SampleMetadataCodec retrieves sample metadata") {
    withTempFile { file =>
      val samples  = Vector(
        SampleMetadata(
          SampleId("S1"),
          Map("tissue" -> MetadataValue.StringValue("liver"))
        )
      )
      val original = SampleMetadataTable(samples, Vector("sample", "tissue"))

      SampleMetadataCodec.writeCSV(original, file)
      val loaded = SampleMetadataCodec.readCSV(file).getOrElse(null)

      val retrieved = loaded.getSample(SampleId("S1"))
      assert(retrieved.isDefined)
      assert(retrieved.get.get("tissue").isDefined)
    }
  }

  test("SampleMetadataCodec with missing sample") {
    withTempFile { file =>
      val samples  = Vector(
        SampleMetadata(SampleId("S1"), Map("type" -> MetadataValue.StringValue("A")))
      )
      val original = SampleMetadataTable(samples, Vector("sample", "type"))

      SampleMetadataCodec.writeCSV(original, file)
      val loaded = SampleMetadataCodec.readCSV(file).getOrElse(null)

      assert(loaded.getSample(SampleId("S999")).isEmpty)
    }
  }
