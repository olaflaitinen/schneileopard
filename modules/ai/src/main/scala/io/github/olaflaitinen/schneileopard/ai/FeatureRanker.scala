package io.github.olaflaitinen.schneileopard.ai

import io.github.olaflaitinen.schneileopard.core.*
import io.github.olaflaitinen.schneileopard.omics.*
import scala.math.*

/**
 * Feature ranking based on variance and statistical measures.
 *
 * This implementation provides simple but effective feature scoring using variance-based approaches
 * suitable for genomic data.
 */
class FeatureRanker:
  /**
   * Rank features by variance across samples.
   *
   * Higher variance features are ranked higher, as they show more variation across samples and may
   * be more informative for discrimination.
   *
   * @param matrix
   *   the expression matrix
   * @return
   *   a ranking explanation with features sorted by importance
   */
  def rankByVariance(matrix: ExpressionMatrix): RankingExplanation =
    val importances = matrix.features.zipWithIndex
      .map { case (feature, idx) =>
        val values   = matrix.values(idx)
        val mean     = values.sum / values.length
        val variance = values.map(x => pow(x - mean, 2)).sum / values.length
        FeatureImportance(FeatureId(feature.value), variance, "variance")
      }
      .sortBy(_.score)(Ordering[Double].reverse)
      .toVector

    RankingExplanation(
      features = importances,
      method = "variance_ranking",
      parameters = Map("features" -> importances.length.toString)
    )

  /**
   * Rank features by correlation with a phenotype vector.
   *
   * Features with strong absolute correlation to the phenotype are ranked higher.
   *
   * @param matrix
   *   the expression matrix
   * @param phenotype
   *   a vector of phenotype values (must have same length as samples)
   * @return
   *   a ranking explanation
   */
  def rankByCorrelation(
      matrix: ExpressionMatrix,
      phenotype: Vector[Double]
  ): RankingExplanation =
    require(phenotype.length == matrix.sampleCount, "Phenotype vector length must match samples")

    val phenotypeMean = phenotype.sum / phenotype.length
    val phenotypeVar  = phenotype.map(x => pow(x - phenotypeMean, 2)).sum / phenotype.length

    val importances = matrix.features.zipWithIndex
      .map { case (feature, idx) =>
        val values   = matrix.values(idx)
        val mean     = values.sum / values.length
        val variance = values.map(x => pow(x - mean, 2)).sum / values.length

        // Compute correlation
        val covariance = values
          .zip(phenotype)
          .map { case (x, y) => (x - mean) * (y - phenotypeMean) }
          .sum / values.length

        val correlation =
          if variance > 0 && phenotypeVar > 0 then covariance / sqrt(variance * phenotypeVar)
          else 0.0

        FeatureImportance(FeatureId(feature.value), abs(correlation), "correlation")
      }
      .sortBy(_.score)(Ordering[Double].reverse)
      .toVector

    RankingExplanation(
      features = importances,
      method = "correlation_ranking",
      parameters = Map("samples" -> matrix.sampleCount.toString)
    )

  /**
   * Score features within a cohort context.
   *
   * This method can be overridden or extended for more sophisticated scoring considering cohort
   * membership and metadata.
   *
   * @param matrix
   *   the expression matrix
   * @param cohort
   *   the cohort of samples to analyze
   * @return
   *   a ranking explanation
   */
  def scoreFeatures(matrix: ExpressionMatrix, cohort: Cohort): RankingExplanation =
    val cohortIndices = matrix.samples.zipWithIndex
      .filter { case (sample, _) => cohort.contains(sample) }
      .map(_._2)

    if cohortIndices.isEmpty then
      return RankingExplanation(
        features = Vector.empty,
        method = "cohort_scoring",
        parameters = Map("cohort_size" -> "0")
      )

    val filteredMatrix = ExpressionMatrix(
      features = matrix.features,
      samples = cohortIndices.map(matrix.samples(_)),
      values = matrix.values.map(row => cohortIndices.map(row(_)))
    )

    rankByVariance(filteredMatrix).copy(
      method = "cohort_scoring",
      parameters = Map("cohort" -> cohort.id.value, "samples" -> cohortIndices.length.toString)
    )
