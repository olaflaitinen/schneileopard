package io.github.olaflaitinen.schneileopard.bench

import io.github.olaflaitinen.schneileopard.core.*
import io.github.olaflaitinen.schneileopard.omics.*
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

/**
 * Baseline benchmarks for expression matrix operations
 */
@State(Scope.Thread)
@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(value = 1, jvmArgs = Array("-Xmx2G"))
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
class ExpressionMatrixBench:

  private var matrix: ExpressionMatrix = _
  private val featureCount             = 10000
  private val sampleCount              = 100

  @Setup
  def setup(): Unit =
    val features = Vector.tabulate(featureCount) { i =>
      GeneId(s"ENSG${String.format("%011d", i + 1)}")
    }
    val samples  = Vector.tabulate(sampleCount) { i =>
      SampleId(s"S${String.format("%05d", i + 1)}")
    }
    val values   = Vector.tabulate(featureCount) { _ =>
      Vector.tabulate(sampleCount) { _ =>
        scala.math.random() * 10.0
      }
    }
    matrix = ExpressionMatrix(features, samples, values)

  end setup

  @Benchmark
  def benchmarkMatrixCreation(): ExpressionMatrix =
    val features = Vector.tabulate(featureCount) { i =>
      GeneId(s"ENSG${String.format("%011d", i + 1)}")
    }
    val samples  = Vector.tabulate(sampleCount) { i =>
      SampleId(s"S${String.format("%05d", i + 1)}")
    }
    val values   = Vector.tabulate(featureCount) { _ =>
      Vector.tabulate(sampleCount) { _ =>
        scala.math.random() * 10.0
      }
    }
    ExpressionMatrix(features, samples, values)

  end benchmarkMatrixCreation

  @Benchmark
  def benchmarkLog2Transform(): ExpressionMatrix =
    Normalization.log2Transform(matrix, pseudocount = 1.0)

  end benchmarkLog2Transform

  @Benchmark
  def benchmarkZscoreNormalization(): ExpressionMatrix =
    Normalization.zscore(matrix)

  end benchmarkZscoreNormalization

  @Benchmark
  def benchmarkLibraryNormalization(): ExpressionMatrix =
    Normalization.libraryNormalize(matrix, scale = 1e6)

  end benchmarkLibraryNormalization

  @Benchmark
  def benchmarkMatrixAccess(blackhole: org.openjdk.jmh.infra.Blackhole): Unit =
    var sum = 0.0
    for i <- 0 until 1000 do
      sum += matrix(
        scala.math.floor(scala.math.random() * featureCount).toInt,
        scala.math.floor(scala.math.random() * sampleCount).toInt
      )
    blackhole.consume(sum)

  end benchmarkMatrixAccess

end ExpressionMatrixBench
