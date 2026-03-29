# Security Policy

## Reporting Security Vulnerabilities

If you discover a security vulnerability in Schnéileopard, please email the maintainers at olaf.laitinen@uni.lu with the following information:

1. Description of the vulnerability
2. Steps to reproduce the issue
3. Potential impact
4. Suggested fix (if you have one)

Please do not open a public GitHub issue for security vulnerabilities. We ask that you give us reasonable time to address the issue before public disclosure.

## Supported Versions

Security updates are provided for the current major version and one version prior. For specific version information, see COMPATIBILITY.md.

## Security Considerations

Schnéileopard is designed for research and academic use. While we apply security best practices to the library code itself, users should be aware that:

1. The library does not perform cryptographic operations and should not be used for security-sensitive cryptographic applications.
2. Data validation focuses on semantic correctness for biomedical data rather than defense against malicious input.
3. The library is not hardened against timing attacks or other side-channel attacks.
4. Some dependencies may have security implications. We monitor and update dependencies regularly.

## Dependency Security

We use the following practices to manage dependency security:

1. Regular updates of direct and transitive dependencies
2. Monitoring of security advisories for dependencies
3. Prompt updates when vulnerabilities are discovered
4. Clear documentation of dependency changes in CHANGELOG.md

## Contributing Security Fixes

If you develop a security fix, please follow the standard contribution process detailed in CONTRIBUTING.md, but mark the pull request clearly as addressing a security issue and provide details in a private message to maintainers if appropriate.

## Vulnerability Disclosure

Vulnerabilities that are fixed will be disclosed in the release notes with appropriate severity information where applicable.

---

*Security Policy - Last Updated: March 2026*
*Next Review: June 2026*
