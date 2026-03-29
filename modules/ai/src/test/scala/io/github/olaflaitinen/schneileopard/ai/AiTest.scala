package io.github.olaflaitinen.schneileopard.ai

import io.github.olaflaitinen.schneileopard.core.*
import io.github.olaflaitinen.schneileopard.omics.*
import org.scalatest.funsuite.AnyFunSuite

class FeatureRankerTest extends AnyFunSuite:

  val features = Vector(GeneId("G1"), GeneId("G2"), GeneId("G3"), GeneId("G4"))
  val samples  =
    Vector(SampleId("S1"), SampleId("S2"), SampleId("S3"), SampleId("S4"), SampleId("S5"))
  val values   = Vector(
    Vector(1.0, 2.0, 3.0, 4.0, 5.0),      // G1: variance = 2.0
    Vector(10.0, 10.0, 10.0, 10.0, 10.0), // G2: variance = 0.0 (no variation)
    Vector(1.0, 1.1, 1.2, 1.1, 1.0),      // G3: variance ~ 0.008 (minimal)
    Vector(1.0, 5.0, 2.0, 8.0, 3.0)       // G4: variance ~ 7.2 (highest)
  )

  test("rankByVariance orders features correctly") {
    val matrix  = ExpressionMatrix(features, samples, values)
    val ranker  = FeatureRanker()
    val ranking = ranker.rankByVariance(matrix)

    assert(ranking.features.length == 4)
    assert(ranking.method == "variance_ranking")
    // G4 should have highest variance, G3 should have lowest
    assert(ranking.features.head.featureId.value == "G4")
  }

  test("rankByVariance produces valid confidence scores") {
    val matrix  = ExpressionMatrix(features, samples, values)
    val ranker  = FeatureRanker()
    val ranking = ranker.rankByVariance(matrix)

    ranking.features.foreach { fi =>
      assert(fi.score >= 0.0)
      assert(fi.method == "variance")
    }
  }

  test("rankByCorrelation with high correlation phenotype") {
    val matrix    = ExpressionMatrix(features, samples, values)
    val phenotype = Vector(1.0, 2.0, 3.0, 4.0, 5.0) // Correlates with G1
    val ranker    = FeatureRanker()
    val ranking   = ranker.rankByCorrelation(matrix, phenotype)

    assert(ranking.features.length == 4)
    assert(ranking.method == "correlation_ranking")
    // G1 should rank high due to perfect correlation
    assert(ranking.features.head.featureId.value == "G1")
  }

  test("rankByCorrelation with low variance phenotype") {
    val matrix    = ExpressionMatrix(features, samples, values)
    val phenotype = Vector(1.0, 1.0, 1.0, 1.0, 1.0) // Constant
    val ranker    = FeatureRanker()
    val ranking   = ranker.rankByCorrelation(matrix, phenotype)

    assert(ranking.features.length == 4)
    ranking.features.foreach { fi =>
      assert(fi.score >= 0.0 && fi.score <= 1.0)
    }
  }

  test("rankByCorrelation requires matching dimensions") {
    val matrix    = ExpressionMatrix(features, samples, values)
    val phenotype = Vector(1.0, 2.0, 3.0) // Wrong length
    val ranker    = FeatureRanker()

    assertThrows[IllegalArgumentException] {
      ranker.rankByCorrelation(matrix, phenotype)
    }
  }

  test("scoreFeatures respects cohort membership") {
    val matrix  = ExpressionMatrix(features, samples, values)
    val cohort  = Cohort(
      CohortId("C1"),
      Set(SampleId("S1"), SampleId("S2"), SampleId("S3"))
    )
    val ranker  = FeatureRanker()
    val ranking = ranker.scoreFeatures(matrix, cohort)

    assert(ranking.features.length == 4)
    assert(ranking.method == "cohort_scoring")
    assert(ranking.parameters("cohort") == "C1")
    assert(ranking.parameters("samples") == "3")
  }

class ExplainabilityTest extends AnyFunSuite:

  test("Confidence creation and ranges") {
    val high = Confidence(0.9)
    assert(high.isHigh)
    assert(!high.isModerate)
    assert(!high.isLow)

    val moderate = Confidence(0.6)
    assert(!moderate.isHigh)
    assert(moderate.isModerate)
    assert(!moderate.isLow)

    val low = Confidence(0.2)
    assert(!low.isHigh)
    assert(!low.isModerate)
    assert(low.isLow)
  }

  test("Confidence requires valid range") {
    assertThrows[IllegalArgumentException] {
      Confidence(1.5)
    }
    assertThrows[IllegalArgumentException] {
      Confidence(-0.1)
    }
  }

  test("FeatureImportance normalization") {
    val fi         = FeatureImportance(FeatureId("F1"), 10.0, "test")
    val normalized = fi.normalized
    assert(normalized > 0.0 && normalized < 1.0)

    val fi2         = FeatureImportance(FeatureId("F2"), -5.0, "test")
    val normalized2 = fi2.normalized
    assert(normalized2 > 0.0 && normalized2 < 1.0)
  }

  test("RankingExplanation topN filtering") {
    val features = Vector(
      FeatureImportance(FeatureId("F1"), 10.0, "test"),
      FeatureImportance(FeatureId("F2"), 8.0, "test"),
      FeatureImportance(FeatureId("F3"), 6.0, "test"),
      FeatureImportance(FeatureId("F4"), 4.0, "test")
    )
    val ranking  = RankingExplanation(features, "test_method")

    assert(ranking.topN(2).length == 2)
    assert(ranking.topN(2).head.featureId.value == "F1")
  }

  test("RankingExplanation threshold filtering") {
    val features = Vector(
      FeatureImportance(FeatureId("F1"), 10.0, "test"),
      FeatureImportance(FeatureId("F2"), 8.0, "test"),
      FeatureImportance(FeatureId("F3"), 6.0, "test"),
      FeatureImportance(FeatureId("F4"), 4.0, "test")
    )
    val ranking  = RankingExplanation(features, "test_method")

    val above7 = ranking.aboveThreshold(7.0)
    assert(above7.length == 2)
    assert(above7.forall(_.score >= 7.0))
  }

  test("PredictionExplanation report generation") {
    val features    = Vector(
      FeatureImportance(FeatureId("G1"), 0.8, "correlation"),
      FeatureImportance(FeatureId("G2"), 0.6, "correlation")
    )
    val explanation = PredictionExplanation(
      prediction = "healthy",
      confidence = Confidence(0.85),
      topFeatures = features,
      method = "classifier"
    )

    val report = explanation.toReport
    assert(report.contains("healthy"))
    assert(report.contains("high"))
    assert(report.contains("G1"))
  }

  test("StratificationResult sample counting") {
    val stratum = StratumResult(
      samples = Set(SampleId("S1"), SampleId("S2"), SampleId("S3")),
      stratumId = "stratum_1"
    )
    assert(stratum.sampleCount == 3)
  }

  test("StratificationResult with metadata") {
    val stratum = StratumResult(
      samples = Set(SampleId("S1")),
      stratumId = "stratum_1",
      characterization = Some("healthy controls"),
      metadata = Map("tissue" -> "liver", "condition" -> "healthy")
    )
    assert(stratum.characterization.contains("healthy controls"))
    assert(stratum.metadata("tissue") == "liver")
  }

  test("StratificationResult sample finding") {
    val strata = Vector(
      StratumResult(Set(SampleId("S1"), SampleId("S2")), "stratum_1"),
      StratumResult(Set(SampleId("S3"), SampleId("S4")), "stratum_2")
    )
    val result = StratificationResult(strata, "kmeans")

    assert(result.stratumCount == 2)
    assert(result.allSamples.size == 4)
    assert(result.findSampleStratum(SampleId("S2")).exists(_.stratumId == "stratum_1"))
    assert(result.findSampleStratum(SampleId("S5")).isEmpty)
  }
