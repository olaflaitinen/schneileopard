package io.github.olaflaitinen.schneileopard.core

/**
 * Error types for domain validation and processing.
 *
 * These types represent recoverable errors that occur during normal operation, such as schema
 * validation failures or parse errors. They are distinct from exceptions, which represent
 * programming errors or unrecoverable conditions.
 */

/**
 * Base trait for all domain errors in Schnéileopard.
 *
 * Domain errors represent expected failures in data processing and validation, as opposed to
 * programming errors or unrecoverable failures.
 */
sealed trait DomainError:
  /**
   * Human readable error message.
   *
   * @return
   *   the error message
   */
  def message: String

/**
 * Error indicating validation failure for a domain model.
 *
 * Example: A cohort with no samples, or an expression matrix with missing values.
 *
 * @param message
 *   description of the validation failure
 * @param details
 *   additional context about the failure
 */
case class ValidationError(message: String, details: Option[String] = None) extends DomainError

/**
 * Error indicating a parse failure while reading data.
 *
 * Example: Reading a CSV file with invalid format, or type mismatch.
 *
 * @param message
 *   description of the parse failure
 * @param line
 *   the line number where the error occurred, if applicable
 * @param column
 *   the column index where the error occurred, if applicable
 * @param cause
 *   optionally, the underlying exception
 */
case class ParseError(
    message: String,
    line: Option[Int] = None,
    column: Option[Int] = None,
    cause: Option[Throwable] = None
) extends DomainError

/**
 * Error indicating a type mismatch or conversion failure.
 *
 * Example: Trying to parse the string "abc" as a Double.
 *
 * @param message
 *   description of the type error
 * @param expected
 *   the expected type
 * @param actual
 *   the actual type or value received
 */
case class TypeMismatchError(message: String, expected: String, actual: String) extends DomainError

/**
 * Error indicating that a required value is missing.
 *
 * Example: A required column in a data matrix is empty.
 *
 * @param message
 *   description of what is missing
 * @param field
 *   the name of the missing field
 */
case class MissingValueError(message: String, field: String) extends DomainError

/**
 * Error indicating a constraint violation.
 *
 * Example: An expression value is outside the valid range.
 *
 * @param message
 *   description of the constraint violation
 * @param constraint
 *   the constraint that was violated
 * @param value
 *   the value that violated the constraint
 */
case class ConstraintError(message: String, constraint: String, value: String) extends DomainError
