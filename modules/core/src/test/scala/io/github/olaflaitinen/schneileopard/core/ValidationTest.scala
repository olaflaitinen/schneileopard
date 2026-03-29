package io.github.olaflaitinen.schneileopard.core

import org.scalatest.funsuite.AnyFunSuite

class ValidationTest extends AnyFunSuite:

  test("Valid value can be created and extracted") {
    val v = Validation.valid(42)
    assert(v.isValid)
    assert(v.getOrElse(0) == 42)
  }

  test("Invalid value indicates failure") {
    val error = ValidationError("test error")
    val v     = Validation.invalid[Int](error)
    assert(v.isInvalid)
    assert(v.getOrElse(0) == 0)
  }

  test("map transforms valid values") {
    val v      = Validation.valid(10)
    val result = v.map(_ * 2)
    assert(result.getOrElse(0) == 20)
  }

  test("map preserves invalid values") {
    val error  = ValidationError("test")
    val v      = Validation.invalid[Int](error)
    val result = v.map(_ * 2)
    assert(result.isInvalid)
  }

  test("flatMap chains validations") {
    val v1 = Validation.valid(10)
    val v2 = v1.flatMap(x => Validation.valid(x * 2))
    assert(v2.getOrElse(0) == 20)
  }

  test("flatMap short circuits on invalid") {
    val error = ValidationError("test")
    val v1    = Validation.invalid[Int](error)
    val v2    = v1.flatMap(x => Validation.valid(x * 2))
    assert(v2.isInvalid)
  }

  test("fold applies correct function") {
    val v      = Validation.valid(42)
    val result = v.fold(
      _ => "invalid",
      a => s"valid: $a"
    )
    assert(result == "valid: 42")
  }

  test("toEither converts valid to Right") {
    val v      = Validation.valid(42)
    val either = v.toEither
    assert(either.isRight)
    assert(either.getOrElse(0) == 42)
  }

  test("toEither converts invalid to Left") {
    val error  = ValidationError("test")
    val v      = Validation.invalid[Int](error)
    val either = v.toEither
    assert(either.isLeft)
  }

  test("toOption converts valid to Some") {
    val v   = Validation.valid(42)
    val opt = v.toOption
    assert(opt.contains(42))
  }

  test("toOption converts invalid to None") {
    val error = ValidationError("test")
    val v     = Validation.invalid[Int](error)
    val opt   = v.toOption
    assert(opt.isEmpty)
  }

  test("sequence combines multiple validations") {
    val validations = List(
      Validation.valid(1),
      Validation.valid(2),
      Validation.valid(3)
    )
    val result      = Validation.sequence(validations)
    assert(result.isValid)
    assert(result.getOrElse(Nil) == List(1, 2, 3))
  }

  test("sequence returns first error") {
    val error       = ValidationError("test")
    val validations = List(
      Validation.valid(1),
      Validation.invalid[Int](error),
      Validation.valid(3)
    )
    val result      = Validation.sequence(validations)
    assert(result.isInvalid)
  }

  test("attempt captures exceptions") {
    val v = Validation.attempt(42 / 0)
    assert(v.isInvalid)
  }

  test("attempt preserves successful computations") {
    val v = Validation.attempt(42 * 2)
    assert(v.isValid)
    assert(v.getOrElse(0) == 84)
  }
