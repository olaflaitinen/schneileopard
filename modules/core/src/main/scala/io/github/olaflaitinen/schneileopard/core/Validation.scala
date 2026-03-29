package io.github.olaflaitinen.schneileopard.core

/**
 * Composable validation type for representing the result of validation operations.
 *
 * Validation[A] represents either a success value of type A or a DomainError. It provides methods
 * for composition and chaining validation operations.
 */
enum Validation[+A]:
  case Valid(value: A)
  case Invalid(error: DomainError)

  /**
   * Apply a function to the successful value if present.
   *
   * @param f
   *   the function to apply
   * @return
   *   a new Validation with the result
   */
  def map[B](f: A => B): Validation[B] = this match
    case Valid(a)   => Valid(f(a))
    case Invalid(e) => Invalid(e)

  /**
   * Flatmap for chaining validations.
   *
   * @param f
   *   the function producing a new Validation
   * @return
   *   the result of applying f
   */
  def flatMap[B](f: A => Validation[B]): Validation[B] = this match
    case Valid(a)   => f(a)
    case Invalid(e) => Invalid(e)

  /**
   * Apply a side effect function to the successful value if present.
   *
   * @param f
   *   the function to apply
   * @return
   *   this Validation unchanged
   */
  def foreach(f: A => Unit): Unit = this match
    case Valid(a)   => f(a)
    case Invalid(_) => ()

  /**
   * Extract the value or return a default.
   *
   * @param default
   *   the default value to return on invalid
   * @return
   *   the wrapped value or the default
   */
  def getOrElse[B >: A](default: B): B = this match
    case Valid(a)   => a
    case Invalid(_) => default

  /**
   * Fold this validation into a single value.
   *
   * @param onValid
   *   function to apply if valid
   * @param onInvalid
   *   function to apply if invalid
   * @return
   *   the result of either function
   */
  def fold[B](onInvalid: DomainError => B, onValid: A => B): B = this match
    case Valid(a)   => onValid(a)
    case Invalid(e) => onInvalid(e)

  /**
   * Convert to a standard Scala Either where Left is the error.
   *
   * @return
   *   an Either[DomainError, A]
   */
  def toEither: Either[DomainError, A] = this match
    case Valid(a)   => Right(a)
    case Invalid(e) => Left(e)

  /**
   * Convert to an Option, discarding the error.
   *
   * @return
   *   Some(value) if valid, None if invalid
   */
  def toOption: Option[A] = this match
    case Valid(a)   => Some(a)
    case Invalid(_) => None

  /**
   * Check if this validation is valid.
   *
   * @return
   *   true if valid
   */
  def isValid: Boolean = this match
    case Valid(_)   => true
    case Invalid(_) => false

  /**
   * Check if this validation is invalid.
   *
   * @return
   *   true if invalid
   */
  def isInvalid: Boolean = !isValid

/**
 * Factory methods for Validation.
 */
object Validation:
  /**
   * Create a successful validation.
   *
   * @param value
   *   the successful value
   * @return
   *   a Valid validation
   */
  def valid[A](value: A): Validation[A] = Valid(value)

  /**
   * Create a failed validation.
   *
   * @param error
   *   the domain error
   * @return
   *   an Invalid validation
   */
  def invalid[A](error: DomainError): Validation[A] = Invalid(error)

  /**
   * Try to convert a value into a Validation, catching throwables.
   *
   * @param f
   *   the function that may throw
   * @return
   *   a Valid if successful, Invalid with a captured error if it throws
   */
  def attempt[A](f: => A): Validation[A] =
    try Valid(f)
    catch
      case e: IllegalArgumentException =>
        Invalid(ValidationError(e.getMessage))
      case e: Exception                =>
        Invalid(ValidationError(s"Unexpected error: ${e.getMessage}"))

  /**
   * Combine multiple validations, returning the first error if any.
   *
   * @param validations
   *   the validations to combine
   * @return
   *   a Valid with the list of values, or the first Invalid
   */
  def sequence[A](validations: List[Validation[A]]): Validation[List[A]] =
    validations.foldRight[Validation[List[A]]](Valid(Nil)) { (v, acc) =>
      for
        a  <- v
        as <- acc
      yield a :: as
    }

  /**
   * Traverse a function over a list of values, collecting validations.
   *
   * @param values
   *   the values to traverse
   * @param f
   *   the function producing a validation for each value
   * @return
   *   a Valid with the results or the first Invalid
   */
  def traverse[A, B](values: List[A])(f: A => Validation[B]): Validation[List[B]] =
    sequence(values.map(f))
