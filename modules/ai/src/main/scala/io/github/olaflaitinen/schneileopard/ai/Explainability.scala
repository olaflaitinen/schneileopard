package io.github.olaflaitinen.schneileopard.ai

import io.github.olaflaitinen.schneileopard.core.*

/**
 * Typed representations for explainability in AI predictions and rankings.
 *
 * These types ensure that explanations are not opaque strings but structured, queryable values that
 * capture the reasoning behind AI decisions.
 */

/**
 * Confidence or uncertainty score for a prediction.
 *
 * Represents a calibrated confidence between 0 (uncertain) and 1 (certain).
 *
 * @param value
 *   the confidence score between 0 and 1
 */
case class Confidence(value: Double):
  require(value >= 0.0 && value <= 1.0, "Confidence must be between 0 and 1")

  /**
   * Check if confidence is high (above 0.8).
   *
   * @return
   *   true if confidence is high
   */
  def isHigh: Boolean = value >= 0.8

  /**
   * Check if confidence is moderate (between 0.5 and 0.8).
   *
   * @return
   *   true if confidence is moderate
   */
  def isModerate: Boolean = value >= 0.5 && value < 0.8

  /**
   * Check if confidence is low (below 0.5).
   *
   * @return
   *   true if confidence is low
   */
  def isLow: Boolean = value < 0.5

/**
 * Feature importance score representing the contribution of a feature to a model.
 *
 * @param featureId
 *   the feature identifier
 * @param score
 *   the importance score
 * @param method
 *   the method used to compute importance (e.g., "correlation", "mutual_information")
 */
case class FeatureImportance(
    featureId: FeatureId,
    score: Double,
    method: String
):
  /**
   * Convert to a normalized score between 0 and 1.
   *
   * @return
   *   the normalized importance
   */
  def normalized: Double = Math.abs(score) / (Math.abs(score) + 1)

/**
 * Explanation for a ranking of features.
 *
 * @param features
 *   the ranked features
 * @param method
 *   the ranking method used
 * @param parameters
 *   optional parameters used in the ranking
 * @param timestamp
 *   the time the ranking was computed
 */
case class RankingExplanation(
    features: Vector[FeatureImportance],
    method: String,
    parameters: Map[String, String] = Map.empty,
    timestamp: Long = System.currentTimeMillis()
):
  /**
   * Get the top N features.
   *
   * @param n
   *   the number of features to return
   * @return
   *   the top N features by importance
   */
  def topN(n: Int): Vector[FeatureImportance] = features.take(n)

  /**
   * Get features above an importance threshold.
   *
   * @param threshold
   *   the minimum importance score
   * @return
   *   features with importance >= threshold
   */
  def aboveThreshold(threshold: Double): Vector[FeatureImportance] =
    features.filter(_.score >= threshold)

/**
 * Explanation for a classification or prediction result.
 *
 * @param prediction
 *   the predicted class or value
 * @param confidence
 *   the confidence in the prediction
 * @param topFeatures
 *   the top contributing features
 * @param method
 *   the method used for prediction
 */
case class PredictionExplanation(
    prediction: String,
    confidence: Confidence,
    topFeatures: Vector[FeatureImportance],
    method: String
):
  /**
   * Convert to human-readable format.
   *
   * @return
   *   a string representation suitable for display
   */
  def toReport: String =
    val confidenceStr =
      if confidence.isHigh then "high"
      else if confidence.isModerate then "moderate"
      else "low"

    val featuresStr = topFeatures
      .take(5)
      .map(f => s"${f.featureId.value} (${String.format("%.3f", f.score)})")
      .mkString(", ")

    s"Prediction: $prediction (${confidenceStr} confidence, $confidenceStr certainty)\nTop features: $featuresStr"

/**
 * Result of sample stratification or clustering.
 *
 * @param samples
 *   the samples in this group
 * @param stratumId
 *   an identifier for this stratum
 * @param characterization
 *   optional string describing this stratum
 * @param metadata
 *   optional metadata about the stratum
 */
case class StratumResult(
    samples: Set[SampleId],
    stratumId: String,
    characterization: Option[String] = None,
    metadata: Map[String, String] = Map.empty
):
  /**
   * Number of samples in this stratum.
   *
   * @return
   *   the sample count
   */
  def sampleCount: Int = samples.size

/**
 * Result of stratification across multiple strata.
 *
 * @param strata
 *   the individual strata
 * @param method
 *   the stratification method used
 * @param parameters
 *   optional parameters used
 */
case class StratificationResult(
    strata: Vector[StratumResult],
    method: String,
    parameters: Map[String, String] = Map.empty
):
  /**
   * Number of strata.
   *
   * @return
   *   the stratum count
   */
  def stratumCount: Int = strata.length

  /**
   * Get all samples across all strata.
   *
   * @return
   *   the set of all sample IDs
   */
  def allSamples: Set[SampleId] = strata.flatMap(_.samples).toSet

  /**
   * Get the stratum containing a specific sample.
   *
   * @param sampleId
   *   the sample to find
   * @return
   *   Some(stratum) if the sample is found, None otherwise
   */
  def findSampleStratum(sampleId: SampleId): Option[StratumResult] =
    strata.find(_.samples.contains(sampleId))
