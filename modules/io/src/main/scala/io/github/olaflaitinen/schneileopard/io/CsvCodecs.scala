package io.github.olaflaitinen.schneileopard.io

import io.github.olaflaitinen.schneileopard.core.*
import io.github.olaflaitinen.schneileopard.omics.*
import com.github.tototoshi.csv.*
import java.io.File

/**
 * CSV codec for reading and writing expression matrices.
 *
 * The standard format is:
 *   - First row contains headers with sample IDs
 *   - First column contains feature IDs
 *   - Remaining cells contain expression values
 *
 * Example:
 * {{{
 * feature,S1,S2,S3
 * ENSG00000000003,2.5,3.1,2.8
 * ENSG00000000005,4.2,3.9,4.1
 * }}}
 */
object ExpressionMatrixCodec:
  /**
   * Read an expression matrix from a CSV file.
   *
   * @param file
   *   the CSV file to read
   * @return
   *   a Validation of the parsed matrix
   */
  def readCSV(file: File): Validation[ExpressionMatrix] =
    Validation
      .attempt {
        val reader  = CSVReader.open(file)
        val allRows = reader.allWithHeaders()
        reader.close()

        if allRows.isEmpty then throw new IllegalArgumentException("CSV file is empty")

        val headers = allRows.head.keySet.toVector.sorted
        if headers.isEmpty then throw new IllegalArgumentException("No columns found")

        val featureColumn = "feature"
        if !headers.contains(featureColumn) then
          throw new IllegalArgumentException(s"Missing required column: $featureColumn")

        val sampleHeaders = headers.filter(_ != featureColumn)
        val features      = allRows.map(row => GeneId(row(featureColumn))).toVector
        val samples       = sampleHeaders.map(SampleId(_)).toVector

        val values = allRows.map { row =>
          sampleHeaders.map { header =>
            row(header).toDouble
          }.toVector
        }.toVector

        ExpressionMatrix(features, samples, values)
      }
      .flatMap { matrix =>
        ExpressionMatrix.validate(matrix.features, matrix.samples, matrix.values)
      }

  /**
   * Write an expression matrix to a CSV file.
   *
   * @param matrix
   *   the matrix to write
   * @param file
   *   the output file
   */
  def writeCSV(matrix: ExpressionMatrix, file: File): Unit =
    val writer = CSVWriter.open(file)

    // Write header
    val header = Vector("feature") ++ matrix.samples.map(_.value)
    writer.writeRow(header)

    // Write data
    matrix.features.zipWithIndex.foreach { case (feature, idx) =>
      val row = Vector(feature.value) ++ matrix.values(idx).map(_.toString)
      writer.writeRow(row)
    }

    writer.close()

/**
 * CSV codec for reading and writing sample metadata.
 *
 * Format:
 *   - First row contains column headers
 *   - First column is implicitly the sample ID
 *   - Remaining columns are metadata values
 *
 * Example:
 * {{{
 * sample,tissue,condition,age
 * S1,liver,healthy,25
 * S2,liver,diseased,45
 * }}}
 */
object SampleMetadataCodec:
  /**
   * Read sample metadata from a CSV file.
   *
   * @param file
   *   the CSV file to read
   * @return
   *   a Validation of the parsed metadata table
   */
  def readCSV(file: File): Validation[SampleMetadataTable] =
    Validation.attempt {
      val reader  = CSVReader.open(file)
      val allRows = reader.allWithHeaders()
      reader.close()

      if allRows.isEmpty then throw new IllegalArgumentException("CSV file is empty")

      val headers = allRows.head.keySet.toVector.sorted
      if headers.isEmpty then throw new IllegalArgumentException("No columns found")

      val sampleColumn = "sample"
      if !headers.contains(sampleColumn) then
        throw new IllegalArgumentException(s"Missing required column: $sampleColumn")

      val metadataHeaders = headers.filter(_ != sampleColumn)

      val metadata = allRows.map { row =>
        val sampleId = SampleId(row(sampleColumn))
        val values   = metadataHeaders.map { header =>
          header -> MetadataValue.StringValue(row(header))
        }.toMap
        SampleMetadata(sampleId, values)
      }.toVector

      SampleMetadataTable(metadata, headers)
    }

  /**
   * Write sample metadata to a CSV file.
   *
   * @param table
   *   the metadata table to write
   * @param file
   *   the output file
   */
  def writeCSV(table: SampleMetadataTable, file: File): Unit =
    val writer = CSVWriter.open(file)

    // Write header
    writer.writeRow(table.columns)

    // Write data
    table.metadata.foreach { metadata =>
      val row = Vector(metadata.sampleId.value) ++ table.columns
        .drop(
          1
        )
        .map(col => metadata.get(col).map(_.toString).getOrElse(""))
      writer.writeRow(row)
    }

    writer.close()
