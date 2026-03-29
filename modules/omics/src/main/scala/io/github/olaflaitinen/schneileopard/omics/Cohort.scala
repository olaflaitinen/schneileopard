package io.github.olaflaitinen.schneileopard.omics

import io.github.olaflaitinen.schneileopard.core.*

/**
 * A cohort represents a group of samples with associated metadata.
 *
 * Cohorts are fundamental units for analysis, typically representing a group of samples from a
 * study or with shared characteristics. Each cohort maintains its membership and can store
 * arbitrary metadata key-value pairs.
 *
 * @param id
 *   the cohort identifier
 * @param samples
 *   the set of sample identifiers in this cohort
 * @param metadata
 *   arbitrary key-value metadata associated with the cohort
 */
case class Cohort(
    id: CohortId,
    samples: Set[SampleId],
    metadata: Map[String, String] = Map.empty
):
  /**
   * Number of samples in this cohort.
   *
   * @return
   *   the sample count
   */
  def sampleCount: Int = samples.size

  /**
   * Check if a sample belongs to this cohort.
   *
   * @param sample
   *   the sample to check
   * @return
   *   true if the sample is in this cohort
   */
  def contains(sample: SampleId): Boolean = samples.contains(sample)

  /**
   * Get metadata field by key.
   *
   * @param key
   *   the metadata key
   * @return
   *   Some(value) if the key exists, None otherwise
   */
  def getMetadata(key: String): Option[String] = metadata.get(key)

  /**
   * Create a new cohort with additional metadata.
   *
   * @param key
   *   the metadata key
   * @param value
   *   the metadata value
   * @return
   *   a new cohort with the additional metadata
   */
  def withMetadata(key: String, value: String): Cohort =
    copy(metadata = metadata.updated(key, value))

  /**
   * Create a new cohort by adding a sample.
   *
   * @param sample
   *   the sample to add
   * @return
   *   a new cohort with the sample included
   */
  def addSample(sample: SampleId): Cohort =
    copy(samples = samples + sample)

  /**
   * Create a new cohort by removing a sample.
   *
   * @param sample
   *   the sample to remove
   * @return
   *   a new cohort with the sample excluded
   */
  def removeSample(sample: SampleId): Cohort =
    copy(samples = samples - sample)

/**
 * Factory methods for Cohort.
 */
object Cohort:
  /**
   * Create and validate a cohort.
   *
   * @param id
   *   the cohort identifier
   * @param samples
   *   the sample set
   * @param metadata
   *   optional metadata
   * @return
   *   a Validation of the cohort
   */
  def validate(
      id: CohortId,
      samples: Set[SampleId],
      metadata: Map[String, String] = Map.empty
  ): Validation[Cohort] =
    if samples.isEmpty then
      Validation.invalid(ValidationError("Cohort must contain at least one sample"))
    else Validation.valid(Cohort(id, samples, metadata))
