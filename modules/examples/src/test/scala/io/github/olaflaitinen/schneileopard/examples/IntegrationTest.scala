package io.github.olaflaitinen.schneileopard.examples

import io.github.olaflaitinen.schneileopard.core.*
import io.github.olaflaitinen.schneileopard.omics.*
import io.github.olaflaitinen.schneileopard.graph.*
import io.github.olaflaitinen.schneileopard.ai.*
import io.github.olaflaitinen.schneileopard.io.*
import org.scalatest.funsuite.AnyFunSuite
import java.io.File
import java.nio.file.Files

class IntegrationTest extends AnyFunSuite:

  test("End-to-end workflow: expression matrix creation, normalization, and feature ranking") {
    // Create synthetic expression data
    val features  = Vector(
      GeneId("BRCA1"),
      GeneId("BRCA2"),
      GeneId("TP53"),
      GeneId("EGFR"),
      GeneId("MYC")
    )
    val samples   = Vector(
      SampleId("ctrl_001"),
      SampleId("ctrl_002"),
      SampleId("ctrl_003"),
      SampleId("case_001"),
      SampleId("case_002")
    )
    val rawValues = Vector(
      Vector(2.1, 2.0, 2.2, 5.0, 5.5),
      Vector(1.9, 2.1, 2.0, 4.8, 5.2),
      Vector(3.0, 2.9, 3.1, 6.2, 6.5),
      Vector(1.0, 1.1, 1.0, 2.0, 2.1),
      Vector(0.5, 0.6, 0.5, 0.8, 0.9)
    )

    // Create and validate matrix
    val matrix = ExpressionMatrix(features, samples, rawValues)
    assert(matrix.featureCount == 5)
    assert(matrix.sampleCount == 5)

    // Apply normalization
    val normalized = Normalization.log2Transform(matrix, pseudocount = 1.0)
    assert(normalized.featureCount == 5)

    // Rank features
    val ranker  = FeatureRanker()
    val ranking = ranker.rankByVariance(normalized)
    assert(ranking.features.length == 5)
    assert(ranking.features.forall(_.score >= 0.0))

    // Get top features
    val topFeatures = ranking.topN(3)
    assert(topFeatures.length == 3)

    // Create cohorts
    val controlCohort = Cohort(
      CohortId("control_group"),
      Set(SampleId("ctrl_001"), SampleId("ctrl_002"), SampleId("ctrl_003"))
    )
    assert(controlCohort.sampleCount == 3)

    val caseCohort = Cohort(
      CohortId("case_group"),
      Set(SampleId("case_001"), SampleId("case_002"))
    )
    assert(caseCohort.sampleCount == 2)

    // Score features within cohorts
    val controlRanking = ranker.scoreFeatures(normalized, controlCohort)
    assert(controlRanking.features.length == 5)
  }

  test("End-to-end workflow: interaction network analysis") {
    // Create interaction network
    val edges = Set(
      InteractionEdge(GeneId("BRCA1"), GeneId("TP53"), Some(0.8), Some("binding")),
      InteractionEdge(GeneId("TP53"), GeneId("EGFR"), Some(0.6), Some("regulation")),
      InteractionEdge(GeneId("EGFR"), GeneId("MYC"), Some(0.7), Some("activation")),
      InteractionEdge(GeneId("MYC"), GeneId("BRCA1"), Some(0.5), Some("feedback"))
    )

    val network = InteractionGraph.fromEdges(edges)
    assert(network.nodeCount == 4)
    assert(network.edgeCount == 4)

    // Check connectivity
    val brca1Neighbors = network.neighbors(GeneId("BRCA1"))
    assert(brca1Neighbors.size == 2)

    // Find connected components
    val components = network.connectedComponents
    assert(components.length == 1) // All connected

    // Query incident edges
    val tp53Edges = network.incidentEdges(GeneId("TP53"))
    assert(tp53Edges.size == 2)
  }

  test("End-to-end workflow: sample stratification and explanation") {
    // Create sample metadata
    val samples = Vector(
      SampleMetadata(
        SampleId("S1"),
        Map(
          "tissue"         -> MetadataValue.StringValue("liver"),
          "age"            -> MetadataValue.NumericValue(30.0),
          "disease_status" -> MetadataValue.CategoricalValue("control", 0)
        )
      ),
      SampleMetadata(
        SampleId("S2"),
        Map(
          "tissue"         -> MetadataValue.StringValue("liver"),
          "age"            -> MetadataValue.NumericValue(65.0),
          "disease_status" -> MetadataValue.CategoricalValue("diseased", 1)
        )
      ),
      SampleMetadata(
        SampleId("S3"),
        Map(
          "tissue"         -> MetadataValue.StringValue("kidney"),
          "age"            -> MetadataValue.NumericValue(45.0),
          "disease_status" -> MetadataValue.CategoricalValue("control", 0)
        )
      )
    )

    val metadata = SampleMetadataTable(samples, Vector("sample", "tissue", "age", "disease_status"))
    assert(metadata.sampleCount == 3)

    // Create stratification result
    val controlStratum = StratumResult(
      samples = Set(SampleId("S1"), SampleId("S3")),
      stratumId = "control",
      characterization = Some("Healthy control subjects"),
      metadata = Map("status_code" -> "0")
    )

    val diseaseStratum = StratumResult(
      samples = Set(SampleId("S2")),
      stratumId = "disease",
      characterization = Some("Diseased subjects"),
      metadata = Map("status_code" -> "1")
    )

    val stratification = StratificationResult(
      Vector(controlStratum, diseaseStratum),
      method = "metadata_based"
    )

    assert(stratification.stratumCount == 2)
    assert(stratification.allSamples.size == 3)

    // Find sample stratum
    val s1Stratum = stratification.findSampleStratum(SampleId("S1"))
    assert(s1Stratum.exists(_.stratumId == "control"))

    val s2Stratum = stratification.findSampleStratum(SampleId("S2"))
    assert(s2Stratum.exists(_.stratumId == "disease"))
  }

  test("End-to-end workflow: prediction explanation") {
    // Create feature importances
    val features = Vector(
      FeatureImportance(FeatureId("BRCA1"), 0.85, "correlation"),
      FeatureImportance(FeatureId("TP53"), 0.72, "correlation"),
      FeatureImportance(FeatureId("EGFR"), 0.68, "correlation"),
      FeatureImportance(FeatureId("MYC"), 0.45, "correlation"),
      FeatureImportance(FeatureId("RAF1"), 0.32, "correlation")
    )

    val ranking = RankingExplanation(
      features = features,
      method = "logistic_regression",
      parameters = Map("model" -> "disease_prediction", "auc" -> "0.89")
    )

    // Create prediction explanation
    val explanation = PredictionExplanation(
      prediction = "likely_diseased",
      confidence = Confidence(0.82),
      topFeatures = ranking.topN(3),
      method = "logistic_regression"
    )

    assert(explanation.confidence.isHigh)
    assert(explanation.topFeatures.length == 3)

    // Generate report
    val report = explanation.toReport
    assert(report.contains("likely_diseased"))
    assert(report.contains("high"))
    assert(report.contains("BRCA1"))
  }

  test("End-to-end workflow: CSV I/O with matrix and metadata") {
    val tmpDir = Files.createTempDirectory("schneileopard_test")

    try {
      // Create and write expression matrix
      val features = Vector(GeneId("G1"), GeneId("G2"), GeneId("G3"))
      val samples  = Vector(SampleId("S1"), SampleId("S2"), SampleId("S3"))
      val values   = Vector(
        Vector(1.5, 2.0, 1.8),
        Vector(3.2, 2.8, 3.5),
        Vector(0.5, 0.6, 0.4)
      )
      val matrix   = ExpressionMatrix(features, samples, values)

      val matrixFile = new File(tmpDir.toFile, "expression.csv")
      ExpressionMatrixCodec.writeCSV(matrix, matrixFile)
      assert(matrixFile.exists())

      // Read matrix back
      val loaded       = ExpressionMatrixCodec.readCSV(matrixFile)
      assert(loaded.isValid)
      val loadedMatrix = loaded.getOrElse(null)
      assert(loadedMatrix.featureCount == 3)

      // Create and write metadata
      val sampleMetadata = Vector(
        SampleMetadata(SampleId("S1"), Map("type" -> MetadataValue.StringValue("control"))),
        SampleMetadata(SampleId("S2"), Map("type" -> MetadataValue.StringValue("case"))),
        SampleMetadata(SampleId("S3"), Map("type" -> MetadataValue.StringValue("control")))
      )
      val table          = SampleMetadataTable(sampleMetadata, Vector("sample", "type"))

      val metadataFile = new File(tmpDir.toFile, "metadata.csv")
      SampleMetadataCodec.writeCSV(table, metadataFile)
      assert(metadataFile.exists())

      // Read metadata back
      val loadedMetadata = SampleMetadataCodec.readCSV(metadataFile)
      assert(loadedMetadata.isValid)
      assert(loadedMetadata.getOrElse(null).sampleCount == 3)
    } finally {
      // Cleanup
      tmpDir.toFile.listFiles().foreach(_.delete())
      tmpDir.toFile.delete()
    }
  }

  test("Validation composability") {
    val v1 = Validation.valid(5)
    val v2 = Validation.valid(10)
    val v3 = Validation.invalid[Int](ValidationError("test error"))

    val combined = for
      a <- v1
      b <- v2
    yield a + b

    assert(combined.isValid)
    assert(combined.getOrElse(0) == 15)

    val combined2 = for
      a <- v1
      b <- v3
    yield a + b

    assert(combined2.isInvalid)
  }
