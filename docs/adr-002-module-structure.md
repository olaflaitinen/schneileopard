# ADR-002: Module Structure

## Status

Accepted

## Context

Schnéileopard has diverse functionality: core domain types, omics data structures, graph operations, AI/ML interfaces, and IO. These must be organized into a coherent module structure that supports both simple users (who want just core types) and complex users (who need full functionality).

## Decision

We use a multi-module sbt build with clear separation of concerns:

1. `schneileopard-core`: Domain types, identifiers, validation, errors. No external dependencies except Scala stdlib.
2. `schneileopard-omics`: Expression matrices, cohorts, metadata, normalization. Depends on core.
3. `schneileopard-graph`: Pathway graphs, interaction networks. Depends on core.
4. `schneileopard-ai`: Feature ranking, stratification, explainability types. Depends on core, omics, graph.
5. `schneileopard-io`: CSV/TSV parsing and generation. Depends on core, omics.
6. `schneileopard-examples`: Executable examples. Not published, internal use only.
7. `schneileopard-bench`: JMH benchmarks. Not published, internal use only.

## Rationale

This structure provides:

- Clear dependency hierarchy: No circular dependencies
- Minimal core: Users who only need domain types don't pull in CSV libraries
- Composability: Modules can be adopted incrementally
- Clean separation: IO and examples don't pollute the published artifact
- Future extensibility: New modules for ONNX, R integration, etc. can be added without disrupting core

## Alternatives Considered

1. Single monolithic module: Simpler build, but forces all dependencies on all users
2. Fewer, larger modules: Reduces number of artifacts but less flexibility
3. More granular modules (one per public type): Harder to maintain, confusing for users

## Consequences

1. Users must add multiple dependencies if using multiple modules
2. Cross-module types appear in multiple artifact POM files
3. Release coordination must account for module independence
4. Package structure must remain stable to avoid breakage

## Implementation Details

- Root pom aggregates all modules
- Each module has independent version tracking (though synchronized in practice)
- Tests are co-located with source code
- All modules use identical compiler and formatter settings

## Related Decisions

- See ADR-001 for the Validation pattern used across modules
- See ADR-003 for identifier typing strategy
- See docs/ARCHITECTURE.md for detailed architecture

## See Also

- [BUILD.md](../BUILD.md) for building each module
- [DEPENDENCIES.md](../DEPENDENCIES.md) for dependency information

---

*ADR-002: Module Structure - Last Updated: March 2026*
*Next Review: June 2026*
