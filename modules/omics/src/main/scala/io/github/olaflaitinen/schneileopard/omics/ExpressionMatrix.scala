package io.github.olaflaitinen.schneileopard.omics

import io.github.olaflaitinen.schneileopard.core.*

/**
 * An expression matrix representing gene expression measurements across samples.
 *
 * The matrix stores expression values in a row-major format where rows represent genes (features)
 * and columns represent samples. Values are typically normalized expression measurements such as
 * log2 fold change, counts per million, or similar.
 *
 * @param features
 *   the gene identifiers (rows)
 * @param samples
 *   the sample identifiers (columns)
 * @param values
 *   the expression values as a vector of rows
 */
case class ExpressionMatrix(
    features: Vector[GeneId],
    samples: Vector[SampleId],
    values: Vector[Vector[Double]]
):
  /**
   * Number of features (genes) in the matrix.
   *
   * @return
   *   the feature count
   */
  def featureCount: Int = features.length

  /**
   * Number of samples in the matrix.
   *
   * @return
   *   the sample count
   */
  def sampleCount: Int = samples.length

  /**
   * Get the expression value for a given feature and sample.
   *
   * @param featureIndex
   *   the feature index
   * @param sampleIndex
   *   the sample index
   * @return
   *   the expression value
   * @throws IndexOutOfBoundsException
   *   if indices are out of bounds
   */
  def apply(featureIndex: Int, sampleIndex: Int): Double =
    values(featureIndex)(sampleIndex)

  /**
   * Get all expression values for a feature.
   *
   * @param featureIndex
   *   the feature index
   * @return
   *   the vector of expression values for this feature
   */
  def featureExpressions(featureIndex: Int): Vector[Double] =
    values(featureIndex)

  /**
   * Get all expression values for a sample.
   *
   * @param sampleIndex
   *   the sample index
   * @return
   *   the vector of expression values for this sample
   */
  def sampleExpressions(sampleIndex: Int): Vector[Double] =
    values.map(_(sampleIndex))

  /**
   * Apply a transformation function to all values.
   *
   * @param f
   *   the transformation function
   * @return
   *   a new matrix with transformed values
   */
  def map(f: Double => Double): ExpressionMatrix =
    copy(values = values.map(_.map(f)))

  /**
   * Create a new matrix with rows filtered by a predicate.
   *
   * @param predicate
   *   function to test each feature
   * @return
   *   a new matrix containing only matching features
   */
  def filterFeatures(predicate: GeneId => Boolean): ExpressionMatrix =
    val newIndices = features.zipWithIndex
      .filter { case (id, _) => predicate(id) }
      .map(_._2)
    ExpressionMatrix(
      features = newIndices.map(features(_)),
      samples = samples,
      values = newIndices.map(values(_))
    )

  /**
   * Create a new matrix with columns filtered by a predicate.
   *
   * @param predicate
   *   function to test each sample
   * @return
   *   a new matrix containing only matching samples
   */
  def filterSamples(predicate: SampleId => Boolean): ExpressionMatrix =
    val newIndices = samples.zipWithIndex
      .filter { case (id, _) => predicate(id) }
      .map(_._2)
    ExpressionMatrix(
      features = features,
      samples = newIndices.map(samples(_)),
      values = values.map(row => newIndices.map(row(_)))
    )

/**
 * Factory methods for ExpressionMatrix.
 */
object ExpressionMatrix:
  /**
   * Create and validate an expression matrix.
   *
   * @param features
   *   the gene identifiers
   * @param samples
   *   the sample identifiers
   * @param values
   *   the expression values
   * @return
   *   a Validation of the matrix
   */
  def validate(
      features: Vector[GeneId],
      samples: Vector[SampleId],
      values: Vector[Vector[Double]]
  ): Validation[ExpressionMatrix] =
    if features.isEmpty then
      Validation.invalid(
        ValidationError("ExpressionMatrix must have at least one feature")
      )
    else if samples.isEmpty then
      Validation.invalid(ValidationError("ExpressionMatrix must have at least one sample"))
    else if values.length != features.length then
      Validation.invalid(
        ValidationError(
          s"Row count (${values.length}) does not match feature count (${features.length})"
        )
      )
    else if values.exists(_.length != samples.length) then
      Validation.invalid(
        ValidationError("All rows must have the same number of columns")
      )
    else Validation.valid(ExpressionMatrix(features, samples, values))
