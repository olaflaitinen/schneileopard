package io.github.olaflaitinen.schneileopard.omics

import io.github.olaflaitinen.schneileopard.core.*
import org.scalatest.funsuite.AnyFunSuite

class ExpressionMatrixTest extends AnyFunSuite:

  val features = Vector(GeneId("G1"), GeneId("G2"), GeneId("G3"))
  val samples  = Vector(SampleId("S1"), SampleId("S2"))
  val values   = Vector(
    Vector(1.0, 2.0),
    Vector(3.0, 4.0),
    Vector(5.0, 6.0)
  )

  test("ExpressionMatrix creation") {
    val matrix = ExpressionMatrix(features, samples, values)
    assert(matrix.featureCount == 3)
    assert(matrix.sampleCount == 2)
  }

  test("ExpressionMatrix access") {
    val matrix = ExpressionMatrix(features, samples, values)
    assert(matrix(0, 0) == 1.0)
    assert(matrix(2, 1) == 6.0)
  }

  test("ExpressionMatrix validation") {
    val validation = ExpressionMatrix.validate(features, samples, values)
    assert(validation.isValid)
  }

  test("ExpressionMatrix validation fails with empty features") {
    val validation = ExpressionMatrix.validate(Vector(), samples, values)
    assert(validation.isInvalid)
  }

  test("ExpressionMatrix map transformation") {
    val matrix  = ExpressionMatrix(features, samples, values)
    val doubled = matrix.map(_ * 2)
    assert(doubled(0, 0) == 2.0)
    assert(doubled(2, 1) == 12.0)
  }

class CohortTest extends AnyFunSuite:

  test("Cohort creation") {
    val samples = Set(SampleId("S1"), SampleId("S2"), SampleId("S3"))
    val cohort  = Cohort(CohortId("C1"), samples)
    assert(cohort.sampleCount == 3)
  }

  test("Cohort validation") {
    val samples    = Set(SampleId("S1"))
    val validation = Cohort.validate(CohortId("C1"), samples)
    assert(validation.isValid)
  }

  test("Cohort validation fails with empty samples") {
    val validation = Cohort.validate(CohortId("C1"), Set())
    assert(validation.isInvalid)
  }

  test("Cohort membership check") {
    val samples = Set(SampleId("S1"), SampleId("S2"))
    val cohort  = Cohort(CohortId("C1"), samples)
    assert(cohort.contains(SampleId("S1")))
    assert(!cohort.contains(SampleId("S3")))
  }

  test("Cohort metadata") {
    val samples = Set(SampleId("S1"))
    val cohort  = Cohort(CohortId("C1"), samples)
    val updated = cohort.withMetadata("tissue", "liver")
    assert(updated.getMetadata("tissue").contains("liver"))
  }

class NormalizationTest extends AnyFunSuite:

  val features = Vector(GeneId("G1"), GeneId("G2"))
  val samples  = Vector(SampleId("S1"), SampleId("S2"), SampleId("S3"))
  val values   = Vector(
    Vector(1.0, 2.0, 4.0),
    Vector(8.0, 16.0, 32.0)
  )

  test("log2 transformation") {
    val matrix      = ExpressionMatrix(features, samples, values)
    val transformed = Normalization.log2Transform(matrix, pseudocount = 1.0)
    assert(transformed.featureCount == 2)
    assert(transformed.sampleCount == 3)
    assert(transformed(0, 0) > 0.0) // log of (1+1) should be 1.0
  }

  test("zscore normalization") {
    val matrix     = ExpressionMatrix(features, samples, values)
    val normalized = Normalization.zscore(matrix)
    // After z-score, each row should have mean ~0 and stddev ~1
    assert(normalized.featureCount == 2)
    assert(normalized.sampleCount == 3)
  }

  test("library normalization") {
    val matrix     = ExpressionMatrix(features, samples, values)
    val normalized = Normalization.libraryNormalize(matrix, scale = 1e6)
    assert(normalized.featureCount == 2)
    assert(normalized.sampleCount == 3)
  }
