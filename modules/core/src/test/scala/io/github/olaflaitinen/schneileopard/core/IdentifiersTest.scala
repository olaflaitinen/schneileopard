package io.github.olaflaitinen.schneileopard.core

import org.scalatest.funsuite.AnyFunSuite

class IdentifiersTest extends AnyFunSuite:

  test("GeneId creation with valid string") {
    val id = GeneId("ENSG00000000003")
    assert(id.value == "ENSG00000000003")
  }

  test("GeneId creation with empty string throws") {
    assertThrows[IllegalArgumentException] {
      GeneId("")
    }
  }

  test("SampleId creation with valid string") {
    val id = SampleId("S001")
    assert(id.value == "S001")
  }

  test("CohortId creation with valid string") {
    val id = CohortId("cohort_discovery")
    assert(id.value == "cohort_discovery")
  }

  test("Different identifier types are distinct") {
    val gene   = GeneId("ENSG00000000003")
    val sample = SampleId("ENSG00000000003")
    // These would not compile if types were mixed, demonstrating the benefit
    assert(gene.value == sample.value)
  }

  test("unsafe construction bypasses validation") {
    val id = GeneId.unsafe("")
    assert(id.value == "")
  }
