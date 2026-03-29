# Compatibility Policy

This document defines Schnéileopard's compatibility guarantees and expectations for different versions.

## Binary Compatibility

Schnéileopard maintains binary compatibility within major versions according to the following principles:

1. **Source Compatibility**: Code compiled against version N.x.y continues to compile against N.z.w where z >= x.
2. **Binary Compatibility**: JAR files compiled against version N.x.y are compatible with version N.z.w where z >= x.
3. **Semantic Versioning**: All versions follow SEMVER following the rules defined in VERSIONING.md.

Binary compatibility is guaranteed through:

- No removal of public members from public types
- No changing of method signatures in incompatible ways
- No changes to class hierarchies that would affect resolution
- No changes to sealed trait implementations that break pattern matching

## Source Compatibility

Source code written against a version N.x.y continues to compile against version N.z.w where z >= x, provided:

1. No deprecated APIs are removed
2. Public member visibility does not decrease
3. Types are not changed in incompatible ways

## Serialization Compatibility

Schnéileopard does not provide long term serialization compatibility guarantees. If your application requires serialized data:

1. Do not rely on Java serialization for long-term storage
2. Use CSV, TSV, or application-specific formats via schneileopard-io
3. Test compatibility explicitly when upgrading

## Documentation Compatibility

Documentation and examples are updated to reflect the current version. Older examples may not work with newer versions, but this is not considered a breaking change.

## Java Compatibility

Schnéileopard targets the JVM. Binary compatibility is maintained with:

- Java 11 LTS and later
- All JVM implementations that support the target Java version
- Scala 3.6.1 and later

## Dependency Compatibility

Third party dependencies are updated regularly for security and bug fixes. Dependency version bumps are included in:

- PATCH versions for bug fixes and security updates that don't add new functionality
- MINOR versions for feature additions
- MAJOR versions for significant changes

Transitive dependency versions are stable within a major version but may change in minor and patch versions. If your application requires pinning specific dependency versions, use dependency lock files.

## Experimental and Internal APIs

The following are not covered by compatibility guarantees:

1. APIs in packages with "internal" in the name
2. APIs marked with @Experimental annotation
3. APIs in non-published modules (examples, bench)
4. Unreleased versions (SNAPSHOT or prerelease)

## Deprecation and Removal

When a public API must be removed:

1. The API is marked with @deprecated annotation in the version before removal
2. Scaladoc includes a deprecation message and migration guidance
3. The deprecated API remains functional for at least one additional major version
4. Removal occurs only in a new major version
5. CHANGELOG.md documents the removal and migration path

## Version Ranges in Dependencies

Users consuming Schnéileopard should use:

```scala
libraryDependencies += "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.x"
```

To get all compatible versions within a major version.

For stricter requirements, specify exact versions:

```scala
libraryDependencies += "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.5"
```

## Reporting Compatibility Issues

If a version breaks compatibility in an unexpected way:

1. Report the issue on GitHub with detailed reproduction steps
2. Include your build configuration and Java version
3. Include the specific versions of all relevant modules
4. Include any error messages or unexpected behavior

Confirmed compatibility issues will be addressed in the next patch release.

## Migration Between Major Versions

Each major version release includes:

1. Migration guide in docs/ directory
2. Deprecation warnings in previous major version
3. Examples of updated code patterns
4. List of breaking changes

## Scaladex and Package Versioning

Schnéileopard is indexed on Scaladex. Version resolution follows Maven Central conventions:

- Latest release is recommended
- Historical versions remain available
- Prerelease versions are marked as such

---

*Compatibility Policy - Last Updated: March 2026*
*Next Review: June 2026*
