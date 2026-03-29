package io.github.olaflaitinen.schneileopard.omics

import io.github.olaflaitinen.schneileopard.core.*

/**
 * Type-safe representation for sample metadata values.
 *
 * Metadata values can be categorical, continuous, or date based. This enum ensures type safety when
 * working with sample annotations.
 */
enum MetadataValue:
  case StringValue(value: String)
  case NumericValue(value: Double)
  case CategoricalValue(category: String, level: Int)
  case BooleanValue(value: Boolean)

  /**
   * Convert to string representation.
   *
   * @return
   *   string representation of this value
   */
  override def toString: String = this match
    case StringValue(v)         => v
    case NumericValue(v)        => v.toString
    case CategoricalValue(c, _) => c
    case BooleanValue(v)        => v.toString

/**
 * Sample metadata containing annotations for a sample.
 *
 * This represents all annotated metadata for a single sample across all columns.
 *
 * @param sampleId
 *   the sample identifier
 * @param values
 *   the metadata values keyed by column name
 */
case class SampleMetadata(
    sampleId: SampleId,
    values: Map[String, MetadataValue]
):
  /**
   * Get a metadata value by column name.
   *
   * @param column
   *   the column name
   * @return
   *   Some(value) if present, None otherwise
   */
  def get(column: String): Option[MetadataValue] = values.get(column)

  /**
   * Get a numeric value, if present and of the correct type.
   *
   * @param column
   *   the column name
   * @return
   *   Some(value) if present and numeric, None otherwise
   */
  def getNumeric(column: String): Option[Double] = values.get(column) match
    case Some(MetadataValue.NumericValue(v)) => Some(v)
    case _                                   => None

  /**
   * Get a categorical value, if present.
   *
   * @param column
   *   the column name
   * @return
   *   the category string if present, None otherwise
   */
  def getCategory(column: String): Option[String] = values.get(column) match
    case Some(MetadataValue.CategoricalValue(c, _)) => Some(c)
    case _                                          => None

/**
 * Table of sample metadata for multiple samples.
 *
 * Coordinates metadata across samples, tracking column definitions and types.
 *
 * @param metadata
 *   the metadata records for each sample
 * @param columns
 *   the column names and their types
 */
case class SampleMetadataTable(
    metadata: Vector[SampleMetadata],
    columns: Vector[String]
):
  /**
   * Number of samples.
   *
   * @return
   *   the sample count
   */
  def sampleCount: Int = metadata.length

  /**
   * Number of columns.
   *
   * @return
   *   the column count
   */
  def columnCount: Int = columns.length

  /**
   * Get all samples.
   *
   * @return
   *   the set of sample IDs
   */
  def samples: Set[SampleId] = metadata.map(_.sampleId).toSet

  /**
   * Get metadata for a specific sample.
   *
   * @param sampleId
   *   the sample identifier
   * @return
   *   Some(metadata) if found, None otherwise
   */
  def getSample(sampleId: SampleId): Option[SampleMetadata] =
    metadata.find(_.sampleId == sampleId)

/**
 * Factory methods for SampleMetadataTable.
 */
object SampleMetadataTable:
  /**
   * Create and validate a metadata table.
   *
   * @param metadata
   *   the sample metadata records
   * @param columns
   *   the column names
   * @return
   *   a Validation of the table
   */
  def validate(
      metadata: Vector[SampleMetadata],
      columns: Vector[String]
  ): Validation[SampleMetadataTable] =
    if metadata.isEmpty then
      Validation.invalid(ValidationError("Metadata table must contain at least one sample"))
    else if columns.isEmpty then
      Validation.invalid(ValidationError("Metadata table must have at least one column"))
    else Validation.valid(SampleMetadataTable(metadata, columns))
