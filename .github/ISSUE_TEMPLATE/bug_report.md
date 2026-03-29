name: Bug Report
description: Report a bug to help improve Schnéileopard
labels: [ "bug" ]
body:
  - type: markdown
    attributes:
      value: |
        Thank you for reporting a bug. Please provide as much detail as possible to help us reproduce and fix the issue.
  - type: input
    id: title
    attributes:
      label: Title
      description: Brief description of the bug
      placeholder: "Expression matrix parsing fails with large files"
    validations:
      required: true
  - type: textarea
    id: description
    attributes:
      label: Description
      description: Detailed description of what went wrong
      placeholder: "When I try to load a CSV file larger than 10,000 genes..."
    validations:
      required: true
  - type: textarea
    id: steps
    attributes:
      label: Steps to Reproduce
      description: How to reproduce the issue
      value: |
        1.
        2.
        3.
    validations:
      required: true
  - type: textarea
    id: expected
    attributes:
      label: Expected Behavior
      description: What should happen
    validations:
      required: true
  - type: textarea
    id: actual
    attributes:
      label: Actual Behavior
      description: What actually happened
    validations:
      required: true
  - type: textarea
    id: error
    attributes:
      label: Error Message or Stack Trace
      description: If applicable, paste the full error message
      render: scala
  - type: input
    id: java-version
    attributes:
      label: Java Version
      placeholder: "17"
    validations:
      required: true
  - type: input
    id: scala-version
    attributes:
      label: Scala Version
      placeholder: "3.6.1"
    validations:
      required: true
  - type: input
    id: schneileopard-version
    attributes:
      label: Schnéileopard Version
      placeholder: "0.1.0"
    validations:
      required: true
  - type: textarea
    id: additional
    attributes:
      label: Additional Context
      description: Any other relevant information
