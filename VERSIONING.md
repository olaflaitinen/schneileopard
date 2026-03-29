# Versioning Policy

Schnéileopard follows Semantic Versioning 2.0.0 for its public API. This document outlines the versioning strategy for the library.

## Version Format

Versions are formatted as MAJOR.MINOR.PATCH with optional prerelease and build metadata:

- MAJOR version when you make incompatible API changes
- MINOR version when you add functionality in a backwards-compatible manner
- PATCH version for backwards-compatible bug fixes

Example: 1.2.3-alpha+20260329

## Stability Guarantees

### Stable API (Guaranteed Stability)

Public APIs in released versions receive binary and source compatibility guarantees within a major version:

1. Code compiled against version 1.x.x continues to compile against any 1.y.z where y >= x
2. Binaries compiled against version 1.x.x are compatible with any 1.y.z where y >= x
3. Documented behavior is preserved

### Experimental API

Experimental APIs are clearly marked as such in documentation and Scaladoc. Experimental features may:

1. Change without notice
2. Be removed in minor version updates
3. Not receive backwards compatibility guarantees

APIs are moved from experimental to stable only after demonstrated use and community feedback.

### Internal APIs

APIs in packages marked as internal (io.github.olaflaitinen.schneileopard.internal.*) are not subject to any compatibility guarantees and may change in any release.

## What Is a Breaking Change

The following constitute breaking changes to the public API:

1. Removing or renaming public types, methods, or packages
2. Changing method signatures in incompatible ways
3. Changing return types in ways that break existing code
4. Adding new abstract methods to public traits without defaults
5. Changing the semantics of existing functions
6. Moving types between packages

The following do NOT constitute breaking changes:

1. Adding new public methods or types
2. Adding new parameters to methods with sensible defaults
3. Widening return types (e.g., Any to Any subtype)
4. Narrowing parameter types (e.g., Any to Comparable)
5. Adding non-abstract methods to public traits
6. Improving error handling or adding new error types
7. Performance improvements

## Release Schedule

Schnéileopard does not follow a fixed release schedule. Releases occur when features are complete and tested. However:

1. Bug fixes are released as patch versions with minimal delay
2. New features are accumulated and released as minor versions periodically
3. Major versions occur infrequently, after significant architectural decisions or API improvements

## Milestone Planning

Planned development milestones:

- **0.1.0** (Target: April 2026): Initial public release with core domain types, omics structures, and basic AI interfaces
- **0.2.x**: Extended AI capabilities and example applications
- **1.0.0** (Target: 2026 Q4): Stable API freeze, production-ready release

## Deprecation Policy

When APIs must be retired:

1. APIs are marked with @deprecated annotation and Scaladoc deprecation notice
2. Deprecated APIs remain functional for at least one major version
3. Migration guidance is provided in Scaladoc and released with the deprecation announcement
4. Deprecation is announced in CHANGELOG.md

## Prerelease Versions

Prerelease versions (e.g., 1.0.0-alpha, 1.0.0-beta) may be released for community testing and feedback. Properties of prerelease versions:

1. May contain incomplete features
2. May have performance issues
3. May require configuration changes between releases
4. Do not receive long-term support
5. Pre releases do not satisfy version constraints in typical semantic versioning resolvers

## Coordinated Releases

Dependencies are kept up to date within reasonable boundaries. Schnéileopard does not perform coordinated releases with dependencies, but monitors for security and critical updates.

## Migration Guides

Migration guides for major version changes will be maintained in the docs directory and referenced in release notes.

---

*Versioning Policy - Last Updated: March 2026*
*Next Review: June 2026*
