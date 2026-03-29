package io.github.olaflaitinen.schneileopard.examples

import io.github.olaflaitinen.schneileopard.core.*
import io.github.olaflaitinen.schneileopard.omics.*
import io.github.olaflaitinen.schneileopard.graph.*
import io.github.olaflaitinen.schneileopard.ai.*

/**
 * Example 1: Basic expression matrix analysis
 *
 * This example demonstrates creating an expression matrix, applying normalization, and computing
 * basic statistics.
 */
object ExpressionMatrixExample:

  def main(args: Array[String]): Unit =
    println("Example 1: Expression Matrix Analysis")
    println("=" * 50)

    val features = Vector(
      GeneId("ENSG00000000003"),
      GeneId("ENSG00000000005"),
      GeneId("ENSG00000000006")
    )

    val samples = Vector(
      SampleId("sample_1"),
      SampleId("sample_2"),
      SampleId("sample_3"),
      SampleId("sample_4")
    )

    val values = Vector(
      Vector(2.5, 3.1, 2.8, 4.2),
      Vector(0.5, 0.6, 0.4, 0.8),
      Vector(10.2, 9.8, 10.5, 9.9)
    )

    val matrixValidation = ExpressionMatrix.validate(features, samples, values)

    matrixValidation match
      case Validation.Valid(matrix) =>
        println(s"Created expression matrix:")
        println(s"  Features: ${matrix.featureCount}")
        println(s"  Samples: ${matrix.sampleCount}")
        println()

        // Apply log2 transformation
        val logTransformed = Normalization.log2Transform(matrix, pseudocount = 1.0)
        println(s"Applied log2 transformation")
        println(s"  Original value (0,0): ${matrix(0, 0)}")
        println(s"  Transformed value (0,0): ${logTransformed(0, 0)}")
        println()

        // Apply Z-score normalization
        val normalized = Normalization.zscore(logTransformed)
        println(s"Applied Z-score normalization")
        println(s"  Normalized value (0,0): ${normalized(0, 0)}")

      case Validation.Invalid(error) =>
        println(s"Error: ${error.message}")

  end main
end ExpressionMatrixExample

/**
 * Example 2: Cohort management and feature ranking
 *
 * This example demonstrates creating cohorts, computing feature importance, and interpreting
 * results.
 */
object CohortAnalysisExample:

  def main(args: Array[String]): Unit =
    println("\nExample 2: Cohort Analysis and Feature Ranking")
    println("=" * 50)

    val samples1 = Set(SampleId("S1"), SampleId("S2"), SampleId("S3"))
    val samples2 = Set(SampleId("S4"), SampleId("S5"), SampleId("S6"))

    val cohort1Validation = Cohort.validate(CohortId("cohort_healthy"), samples1)
    val cohort2Validation = Cohort.validate(CohortId("cohort_diseased"), samples2)

    (cohort1Validation, cohort2Validation) match
      case (Validation.Valid(cohort1), Validation.Valid(cohort2)) =>
        val enrichedCohort1 = cohort1
          .withMetadata("condition", "healthy")
          .withMetadata("tissue", "liver")

        println(s"Created cohort: ${cohort1.id.value}")
        println(s"  Sample count: ${cohort1.sampleCount}")
        println(s"  Condition: ${enrichedCohort1.getMetadata("condition")}")
        println(s"  Tissue: ${enrichedCohort1.getMetadata("tissue")}")
        println()

        // Create a synthetic expression matrix
        val features   = Vector(
          GeneId("GENE_A"),
          GeneId("GENE_B"),
          GeneId("GENE_C")
        )
        val allSamples = (samples1 ++ samples2).toVector
        val values     = Vector(
          Vector(5.0, 4.8, 5.2, 2.1, 2.3, 2.0),
          Vector(1.0, 0.9, 1.1, 5.5, 5.8, 5.2),
          Vector(3.0, 3.0, 3.0, 3.0, 3.0, 3.0)
        )

        ExpressionMatrix.validate(features, allSamples, values) match
          case Validation.Valid(matrix) =>
            val ranker  = FeatureRanker()
            val ranking = ranker.rankByVariance(matrix)

            println(s"Feature ranking by variance:")
            ranking.topN(3).foreach { feature =>
              println(
                f"  ${feature.featureId.value}: ${feature.score}%.4f (${feature.method})"
              )
            }

          case Validation.Invalid(error) =>
            println(s"Error: ${error.message}")

      case (Validation.Invalid(e1), _) =>
        println(s"Error creating cohort 1: ${e1.message}")
      case (_, Validation.Invalid(e2)) =>
        println(s"Error creating cohort 2: ${e2.message}")

  end main
end CohortAnalysisExample

/**
 * Example 3: Pathway and network analysis
 *
 * This example builds an interaction graph, finds connected components, and analyzes network
 * structure.
 */
object NetworkAnalysisExample:

  def main(args: Array[String]): Unit =
    println("\nExample 3: Network and Pathway Analysis")
    println("=" * 50)

    val edges = Set(
      InteractionEdge(GeneId("GENE_A"), GeneId("GENE_B"), Some(0.8)),
      InteractionEdge(GeneId("GENE_B"), GeneId("GENE_C"), Some(0.6)),
      InteractionEdge(GeneId("GENE_C"), GeneId("GENE_A"), Some(0.7)),
      InteractionEdge(GeneId("GENE_D"), GeneId("GENE_E"), Some(0.5))
    )

    val graph = InteractionGraph.fromEdges(edges)

    println(s"Created interaction graph:")
    println(s"  Nodes: ${graph.nodeCount}")
    println(s"  Edges: ${graph.edgeCount}")
    println()

    val components = graph.connectedComponents
    println(s"Connected components: ${components.length}")
    components.zipWithIndex.foreach { case (component, idx) =>
      println(
        s"  Component ${idx + 1}: ${component.map(_.value).mkString(", ")} (${component.size} nodes)"
      )
    }
    println()

    val geneA     = GeneId("GENE_A")
    val neighbors = graph.neighbors(geneA)
    println(s"Neighbors of ${geneA.value}: ${neighbors.map(_.value).mkString(", ")}")

  end main
end NetworkAnalysisExample

/**
 * Run all examples
 */
@main
def runExamples(): Unit =
  ExpressionMatrixExample.main(Array())
  CohortAnalysisExample.main(Array())
  NetworkAnalysisExample.main(Array())
