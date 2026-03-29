name: Feature Request
description: Suggest an idea for Schnéileopard
labels: [ "enhancement" ]
body:
  - type: markdown
    attributes:
      value: |
        Thank you for suggesting a feature. Please provide clear information about your request and use case.
  - type: input
    id: title
    attributes:
      label: Title
      description: Brief description of the feature
      placeholder: "Add support for variant annotation VEP format"
    validations:
      required: true
  - type: textarea
    id: problem
    attributes:
      label: Problem or Use Case
      description: Describe the problem this feature would solve
      placeholder: "Currently, there is no direct support for parsed VEP variant annotations..."
    validations:
      required: true
  - type: textarea
    id: solution
    attributes:
      label: Proposed Solution
      description: Describe how you would like the feature to work
      placeholder: "Add a VepVariantAnnotation case class and a VepCodec for parsing..."
    validations:
      required: true
  - type: textarea
    id: alternatives
    attributes:
      label: Alternatives Considered
      description: Other approaches you have considered
  - type: textarea
    id: examples
    attributes:
      label: Example Usage
      description: Show how this feature would be used
      render: scala
  - type: textarea
    id: additional
    attributes:
      label: Additional Context
      description: Links, references, domain background, or other relevant information
