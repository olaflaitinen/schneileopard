package io.github.olaflaitinen.schneileopard.core

/**
 * Strongly typed, opaque identifiers for molecular entities, samples, and cohorts.
 *
 * Each identifier type prevents accidental mixing of different entity types. For example, a GeneId
 * cannot be used where a SampleId is expected, catching such errors at compile time.
 */

/**
 * Unique identifier for a gene.
 *
 * Typically uses standard nomenclature such as Ensembl Gene ID (ENSG) or NCBI Gene ID formats, but
 * the identifier is semantically opaque.
 *
 * Example: GeneId("ENSG00000000003")
 */
opaque type GeneId = String

object GeneId:
  /**
   * Create a GeneId from a string.
   *
   * @param id
   *   the gene identifier
   * @return
   *   a new GeneId
   * @throws IllegalArgumentException
   *   if the identifier is empty
   */
  def apply(id: String): GeneId =
    if id.isEmpty then throw IllegalArgumentException("GeneId cannot be empty")
    else id

  /**
   * Create a GeneId from a string without validation.
   *
   * Use only when the identifier has been validated elsewhere.
   *
   * @param id
   *   the gene identifier
   * @return
   *   a new GeneId
   */
  def unsafe(id: String): GeneId = id

  extension (id: GeneId) def value: String = id

/**
 * Unique identifier for a transcript (RNA molecule).
 *
 * Example: TranscriptId("ENST00000000003")
 */
opaque type TranscriptId = String

/**
 * Companion object for TranscriptId.
 */
object TranscriptId:

  def apply(id: String): TranscriptId =
    if id.isEmpty then throw IllegalArgumentException("TranscriptId cannot be empty")
    else id

  def unsafe(id: String): TranscriptId = id

  extension (id: TranscriptId) def value: String = id

/**
 * Unique identifier for a protein.
 *
 * Example: ProteinId("ENSP00000000003")
 */
opaque type ProteinId = String

/**
 * Companion object for ProteinId.
 */
object ProteinId:

  def apply(id: String): ProteinId =
    if id.isEmpty then throw IllegalArgumentException("ProteinId cannot be empty")
    else id

  def unsafe(id: String): ProteinId = id

  extension (id: ProteinId) def value: String = id

/**
 * Unique identifier for a sample (biological specimen or cell).
 *
 * Example: SampleId("S001")
 */
opaque type SampleId = String

/**
 * Companion object for SampleId.
 */
object SampleId:

  def apply(id: String): SampleId =
    if id.isEmpty then throw IllegalArgumentException("SampleId cannot be empty")
    else id

  def unsafe(id: String): SampleId = id

  extension (id: SampleId) def value: String = id

/**
 * Unique identifier for a cohort (group of samples).
 *
 * Example: CohortId("cohort_discovery_2026")
 */
opaque type CohortId = String

/**
 * Companion object for CohortId.
 */
object CohortId:

  def apply(id: String): CohortId =
    if id.isEmpty then throw IllegalArgumentException("CohortId cannot be empty")
    else id

  def unsafe(id: String): CohortId = id

  extension (id: CohortId) def value: String = id

/**
 * Unique identifier for a study or dataset.
 *
 * Example: StudyId("study_2026_003")
 */
opaque type StudyId = String

/**
 * Companion object for StudyId.
 */
object StudyId:

  def apply(id: String): StudyId =
    if id.isEmpty then throw IllegalArgumentException("StudyId cannot be empty")
    else id

  def unsafe(id: String): StudyId = id

  extension (id: StudyId) def value: String = id

/**
 * Unique identifier for a feature in an omics matrix (gene, metabolite, etc.).
 *
 * Example: FeatureId("HMDB0000001")
 */
opaque type FeatureId = String

/**
 * Companion object for FeatureId.
 */
object FeatureId:

  def apply(id: String): FeatureId =
    if id.isEmpty then throw IllegalArgumentException("FeatureId cannot be empty")
    else id

  def unsafe(id: String): FeatureId = id

  extension (id: FeatureId) def value: String = id

/**
 * Unique identifier for a genomic variant.
 *
 * Example: VariantId("rs12345678")
 */
opaque type VariantId = String

/**
 * Companion object for VariantId.
 */
object VariantId:

  def apply(id: String): VariantId =
    if id.isEmpty then throw IllegalArgumentException("VariantId cannot be empty")
    else id

  def unsafe(id: String): VariantId = id

  extension (id: VariantId) def value: String = id

/**
 * Unique identifier for a disease term (e.g., MeSH, SNOMED CT, or MONDO ID).
 *
 * Example: DiseaseTerm("MONDO:0005148")
 */
opaque type DiseaseTerm = String

/**
 * Companion object for DiseaseTerm.
 */
object DiseaseTerm:

  def apply(id: String): DiseaseTerm =
    if id.isEmpty then throw IllegalArgumentException("DiseaseTerm cannot be empty")
    else id

  def unsafe(id: String): DiseaseTerm = id

  extension (id: DiseaseTerm) def value: String = id

/**
 * Unique identifier for a phenotype term (e.g., Human Phenotype Ontology).
 *
 * Example: PhenotypeTerm("HP:0000001")
 */
opaque type PhenotypeTerm = String

/**
 * Companion object for PhenotypeTerm.
 */
object PhenotypeTerm:

  def apply(id: String): PhenotypeTerm =
    if id.isEmpty then throw IllegalArgumentException("PhenotypeTerm cannot be empty")
    else id

  def unsafe(id: String): PhenotypeTerm = id

  extension (id: PhenotypeTerm) def value: String = id
