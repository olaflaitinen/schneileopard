# Schnéileopard Publication Guide

This document guides publishing Schnéileopard to Maven Central and Scaladex.

## Overview

Schnéileopard will be published to two locations:

1. **Maven Central** (via Sonatype) - Primary artifact repository for Scala/Java projects
2. **Scaladex** - Scala package index for discoverability

## Prerequisites

### 1. Sonatype Account

**Create account and namespace:**
1. Go to https://issues.sonatype.org/
2. Create Jira account (if needed)
3. Create issue to register namespace `io.github.olaflaitinen`
   - Issue Type: New Project
   - Group Id: `io.github.olaflaitinen`
   - Project URL: https://github.com/olaflaitinen/schneileopard
   - SCM URL: https://github.com/olaflaitinen/schneileopard.git
4. Wait for approval (usually 2-4 hours)

**Alternatively (GitHub Pages Login):**
- Use GitHub OAuth to sign up at https://central.sonatype.com/
- Namespace will be auto-verified based on GitHub ownership

### 2. GPG Key Setup

Generate GPG key pair:

```bash
# Generate key
gpg --gen-key

# Follow prompts:
# Name: Gustav Olaf Yunus Laitinen-Fredriksson Lundstrom-Imanov
# Email: olaf.laitinen@uni.lu
# Passphrase: [strong passphrase - save this!]

# Export private key (base64)
gpg --armor --export-secret-keys olaf.laitinen@uni.lu > private.key

# Export public key
gpg --armor --export olaf.laitinen@uni.lu > public.key

# List keys to get ID
gpg --list-secret-keys
# Copy the key ID (e.g., 1234ABCD5678EF90)

# Publish to keyserver
gpg --keyserver keyserver.ubuntu.com --send-keys 1234ABCD5678EF90
```

**Upload public key:**
1. Go to https://keyserver.ubuntu.com/
2. Search for `olaf.laitinen@uni.lu`
3. Wait for key to appear in different keyservers

### 3. GitHub Secrets Setup

Add secrets to GitHub repository:

1. Go to https://github.com/olaflaitinen/schneileopard/settings/secrets/actions
2. Add new repository secrets:

| Secret Name | Value |
|---|---|
| `SONATYPE_USERNAME` | Your Sonatype username |
| `SONATYPE_PASSWORD` | Your Sonatype password |
| `GPG_PRIVATE_KEY` | Content of `private.key` (base64) |
| `GPG_PASSPHRASE` | GPG key passphrase |

**Get GPG private key base64:**
```bash
cat private.key # Copy entire output including BEGIN/END lines
```

### 4. Build and Test Locally

```bash
# Full clean build
sbt clean compile test

# Verify all tests pass
# Should show: Tests: succeeded 86, failed 0

# Check formatting
sbt scalafmtCheckAll

# Generate docs
sbt doc
```

## Release Process

### Step 1: Update Version

Change version from snapshot to release:

```bash
# Edit build.sbt
# Change: ThisBuild / version := "0.1.0-SNAPSHOT"
# To:     ThisBuild / version := "0.1.0"
```

### Step 2: Create Release Tag

```bash
# Stage change
git add build.sbt

# Commit with release message
git commit -m "Release 0.1.0"

# Create tag (triggers CI/CD publish workflow)
git tag -a v0.1.0 -m "Release version 0.1.0: Production-grade Scala library for systems biomedicine"

# Push branch and tag
git push origin main
git push origin v0.1.0
```

### Step 3: GitHub Actions Publish

Once tag is pushed:
1. Go to https://github.com/olaflaitinen/schneileopard/actions
2. Watch the "Publish Artifacts" workflow
3. Should execute: publish to Maven Central and sonatypeBundleRelease
4. If approved by GPG key, artifacts published

**Publish workflow runs when:**
- Tag matches pattern `v*` (e.g., `v0.1.0`)
- All tests pass
- GPG key secrets available

**Manual alternative (if workflow fails):**
```bash
sbt release
# Prompts for version information, creates tag, publishes
```

## Verification

### 1. Maven Central Status

After publishing (5-30 minutes):

```bash
# Check staging repository
curl -s https://s01.oss.sonatype.org/service/local/repositories | jq .

# Search for artifact
curl -s "https://s01.oss.sonatype.org/service/local/lucene/search?q=io.github.olaflaitinen"
```

Or visit: https://s01.oss.sonatype.org/#nexus-search;quick~io.github.olaflaitinen

### 2. Maven Central Mirror

Artifact appears on Central within 1-2 hours:

```bash
# Search Maven Central
curl -s "https://central.maven.org/search/q?q=io.github.olaflaitinen&rows=20&wt=json" | jq .
```

Or visit: https://central.maven.org/artifact/io.github.olaflaitinen/schneileopard-core/0.1.0

### 3. Usage

Once published, users can add dependency:

```scala
// In build.sbt
libraryDependencies += "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0"

// Or with maven
<dependency>
  <groupId>io.github.olaflaitinen</groupId>
  <artifactId>schneileopard-core_3</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Scaladex Registration

### 1. Publishing to Scaladex

Scaladex auto-indexes artifacts from Maven Central after ~24 hours:

1. Publish to Maven Central (above steps)
2. Wait 24 hours for Scaladex crawler
3. Verify at: https://index.scala-lang.org/search?q=schneileopard

### 2. Manual Submission (Optional)

For faster indexing:

1. Go to https://index.scala-lang.org/
2. Click "Submit Project"
3. Enter repository URL: https://github.com/olaflaitinen/schneileopard
4. Scaladex will index project and its releases

### 3. Project Page

Once indexed, Scaladex provides:

- Project page: https://index.scala-lang.org/olaflaitinen/schneileopard
- Dependency snippets for sbt, Maven, Gradle
- Documentation links
- Dependency graph visualization
- Version history

## Post-Release Steps

### 1. Update Documentation

```bash
# Update version in docs
# - README.md: Installation section
# - CHANGELOG.md: Add release notes
# - Bump version to next snapshot
#   build.sbt: "0.1.1-SNAPSHOT"
```

### 2. GitHub Release

```bash
# Create GitHub release with release notes
gh release create v0.1.0 \
  --title "Schnéileopard 0.1.0" \
  --notes "Production-grade Scala library for systems biomedicine. See CHANGELOG.md for details."
```

### 3. Announcements

- Scala Discourse: https://discourse.scala-lang.org/ (New Releases)
- Scala Reddit: https://reddit.com/r/scala/
- GitHub Discussions for your repo
- Tweet/mention on social media

### 4. Next Version Setup

```bash
# Update build.sbt for next development cycle
# Change: ThisBuild / version := "0.1.0"
# To:     ThisBuild / version := "0.1.1-SNAPSHOT"

git add build.sbt
git commit -m "Prepare 0.1.1-SNAPSHOT development"
git push origin main
```

## Troubleshooting

### GPG Key Issues

**Error: "gpg: command not found"**
```bash
# Install gnupg
brew install gnupg  # macOS
apt-get install gnupg  # Linux
choco install gnupg  # Windows (via Chocolatey)
```

**Error: "gpg: no default secret key"**
```bash
# List keys and set default
gpg --list-secret-keys
export GNUPGHOME=~/.gnupg
```

### Sonatype Credential Issues

**Check credentials work:**
```bash
curl -u "username:password" \
  https://s01.oss.sonatype.org/service/local/repositories
```

**Reset credentials:**
1. Go to Sonatype account page
2. Click "Change Password"
3. Update secrets in GitHub

### Maven Central Sync Delay

**Artifacts not appearing immediately:**
- Normal: Takes 5-30 minutes to sync
- Can be 1-2 hours for search indexing
- Check staging repo first: https://s01.oss.sonatype.org

**Force sync (if needed):**
```bash
curl -X POST \
  -u "username:password" \
  "https://s01.oss.sonatype.org/service/local/staging/bulk/promote"
```

## CI/CD Release Workflow

The GitHub Actions `.github/workflows/release.yml` automates publishing:

1. **Trigger**: When tag `v*` is pushed
2. **Steps**:
   - Check out code
   - Setup Java 17
   - Install sbt
   - Import GPG key from secrets
   - Run `sbt publishSigned`
   - Run `sbt sonatypeBundleRelease`

**Log locations**: https://github.com/olaflaitinen/schneileopard/actions

## Release Checklist

Before releasing:

- [ ] All tests passing (86/86)
- [ ] Code formatted (sbt scalafmtCheckAll)
- [ ] Documentation updated (CHANGELOG.md, README.md)
- [ ] Version number updated in build.sbt
- [ ] Sonatype credentials in GitHub secrets
- [ ] GPG key in GitHub secrets
- [ ] Commit message clear and descriptive
- [ ] Tag created with semantic versioning (v0.1.0)

After releasing:

- [ ] Artifacts appear in Maven Central Mirror (within 2 hours)
- [ ] Scaladex indexed (within 24 hours)
- [ ] GitHub Release created with notes
- [ ] Documentation updated with new version
- [ ] Next snapshot version created
- [ ] Announcement posted

## Additional Resources

- [Sonatype Maven Central Deployment](https://central.sonatype.org/publish/publish-guide/)
- [sbt-release Plugin](https://github.com/sbt/sbt-release)
- [sbt-sonatype Plugin](https://github.com/xerial/sbt-sonatype)
- [Scaladex Documentation](https://index.scala-lang.org/about)
- [Scala Package Index](https://index.scala-lang.org/)

---

*Publication Guide - Last Updated: March 2026*
*Next Review: June 2026*
