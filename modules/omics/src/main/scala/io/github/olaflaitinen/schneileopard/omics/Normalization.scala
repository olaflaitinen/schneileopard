package io.github.olaflaitinen.schneileopard.omics

import scala.math.*

/**
 * Common normalization and transformation functions for omics data.
 *
 * These functions provide standardized approaches for normalizing expression data commonly used in
 * genomic and transcriptomic analysis.
 */
object Normalization:
  /**
   * Apply log2 transformation to expression values.
   *
   * Useful for variance stabilization and to handle the wide dynamic range of expression
   * measurements. A pseudocount is added before transformation to avoid log(0).
   *
   * @param matrix
   *   the expression matrix to transform
   * @param pseudocount
   *   the pseudocount to add before log transformation (default 1.0)
   * @return
   *   a new matrix with log2-transformed values
   */
  def log2Transform(matrix: ExpressionMatrix, pseudocount: Double = 1.0): ExpressionMatrix =
    matrix.map(value => log(max(value + pseudocount, pseudocount)) / log(2.0))

  /**
   * Center and scale columns (samples) to have mean 0 and standard deviation 1.
   *
   * This is commonly called Z-score normalization or standardization.
   *
   * @param matrix
   *   the expression matrix to normalize
   * @return
   *   a new matrix with normalized values
   */
  def zscore(matrix: ExpressionMatrix): ExpressionMatrix =
    val newValues = matrix.values.map { row =>
      val mean     = row.sum / row.length
      val variance = row.map(value => pow(value - mean, 2)).sum / row.length
      val stdDev   = sqrt(variance)
      if stdDev > 0 then row.map(v => (v - mean) / stdDev)
      else row.map(_ - mean)
    }
    matrix.copy(values = newValues)

  /**
   * Quantile normalization to make all samples have the same distribution.
   *
   * This is a stringent normalization often used in microarray analysis. For a large matrix, this
   * produces the same quantiles across all samples.
   *
   * @param matrix
   *   the expression matrix to normalize
   * @return
   *   a new matrix with quantile-normalized values
   */
  def quantileNormalize(matrix: ExpressionMatrix): ExpressionMatrix =
    if matrix.sampleCount == 0 then return matrix

    // Collect all values with their original indices
    val allValues = matrix.values.zipWithIndex.flatMap { case (row, featureIdx) =>
      row.zipWithIndex.map { case (value, sampleIdx) =>
        (featureIdx, sampleIdx, value)
      }
    }

    // Sort by value
    val sorted = allValues.sortBy(_._3)

    // Assign rank-based quantiles
    val quantileMap = scala.collection.mutable.Map[Int, Double]()
    (0 until sorted.length).foreach { i =>
      val quantile = i.toDouble / sorted.length
      quantileMap(i) = quantile
    }

    // Reconstruct matrix
    val newValues = Vector.tabulate(matrix.featureCount) { featureIdx =>
      Vector.tabulate(matrix.sampleCount) { sampleIdx =>
        val origValue = matrix.values(featureIdx)(sampleIdx)
        val rankIdx   = allValues.indexWhere { case (f, s, _) =>
          f == featureIdx && s == sampleIdx
        }
        if rankIdx >= 0 then sorted(rankIdx)._3 else origValue
      }
    }

    matrix.copy(values = newValues)

  /**
   * Perform total count (library size) normalization.
   *
   * Divides each column by its sum and scales to a fixed total (e.g., 1 million for counts per
   * million, CPM).
   *
   * @param matrix
   *   the expression matrix to normalize
   * @param scale
   *   the target scale factor (default 1e6 for million)
   * @return
   *   a new matrix with count-normalized values
   */
  def libraryNormalize(matrix: ExpressionMatrix, scale: Double = 1e6): ExpressionMatrix =
    val newValues = matrix.values.map { row =>
      val sum = row.sum
      if sum > 0 then row.map(_ * scale / sum)
      else row
    }
    matrix.copy(values = newValues)

  /**
   * Remove features (rows) with low variance.
   *
   * Features with variance below a threshold are often considered noise and removed for downstream
   * analysis.
   *
   * @param matrix
   *   the expression matrix to filter
   * @param threshold
   *   the minimum variance threshold
   * @return
   *   a new matrix with only high-variance features
   */
  def filterLowVariance(matrix: ExpressionMatrix, threshold: Double): ExpressionMatrix =
    matrix.filterFeatures { feature =>
      val idx = matrix.features.indexOf(feature)
      if idx >= 0 then
        val row      = matrix.values(idx)
        val mean     = row.sum / row.length
        val variance = row.map(x => pow(x - mean, 2)).sum / row.length
        variance >= threshold
      else false
    }
