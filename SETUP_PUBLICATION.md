# Quick Start: Publishing to Maven Central and Scaladex

This guide provides the quickest path to publishing Schnéileopard to Maven Central and Scaladex.

## 5-Minute Setup

### 1. Sonatype Namespace Registration (2 min, one-time)

**Option A: GitHub OAuth (Recommended)**
1. Go to https://central.sonatype.com/
2. Click "Sign Up" → "Sign up with GitHub"
3. Authorize and follow setup (auto-verifies `io.github.olaflaitinen` namespace)
4. Create authentication token: Profile → View Account → Generate Token
5. **Save**: Username and password (token)

**Option B: Traditional Sonatype**
1. Create Jira account: https://issues.sonatype.org/
2. Create "New Project" issue for namespace `io.github.olaflaitinen`
3. Wait for approval (2-4 hours)
4. Create password at https://central.sonatype.com/

### 2. GPG Key Setup (2 min, one-time)

On your local machine:

```bash
# Generate key (follow prompts, use strong passphrase)
gpg --gen-key
# When asked for name/email, use:
# Name: Gustav Olaf Yunus Laitinen-Fredriksson Lundstrom-Imanov
# Email: olaf.laitinen@uni.lu

# List keys to find ID
gpg --list-secret-keys
# Copy the 16-char key ID (after "rsa3072/")

# Export private key (for GitHub)
gpg --armor --export-secret-keys <YOUR-KEY-ID> > private.key

# Publish to keyserver (so Maven Central can verify)
gpg --keyserver keyserver.ubuntu.com --send-keys <YOUR-KEY-ID>

# Verify key published (wait ~10 minutes then check)
curl https://keyserver.ubuntu.com/pks/lookup?search=olaf.laitinen@uni.lu
```

### 3. GitHub Secrets Setup (1 min)

1. Go to: https://github.com/olaflaitinen/schneileopard/settings/secrets/actions
2. Click "New repository secret" and add each:

| Name | Value from above |
|------|------------------|
| `SONATYPE_USERNAME` | From Sonatype account |
| `SONATYPE_PASSWORD` | From Sonatype account (or token) |
| `GPG_PRIVATE_KEY` | Run: `cat private.key` and paste entire output |
| `GPG_PASSPHRASE` | Your GPG passphrase |

## Publishing Release

Once setup complete, publish v0.1.0 by tag push (already done):

```bash
# Tag already created:
git tag -l
# Shows: v0.1.0

# Check GitHub Actions triggered the publish
# Go to: https://github.com/olaflaitinen/schneileopard/actions
# Look for "Publish Artifacts" job running

# Takes ~5-10 minutes
```

**Verify publication:**

1. Check Sonatype staging: https://s01.oss.sonatype.org/#nexus-search;quick~io.github.olaflaitinen
2. Check Maven Central (wait 30 min): https://central.maven.org/artifact/io.github.olaflaitinen/schneileopard-core/0.1.0
3. Check Scaladex (wait 24h): https://index.scala-lang.org/search?q=schneileopard

## Future Releases

For version 0.1.1:

```bash
# Update version in build.sbt
# From: ThisBuild / version := "0.1.0"
# To:   ThisBuild / version := "0.1.1"

# Commit and tag
git add build.sbt CHANGELOG.md
git commit -m "Release 0.1.1"
git tag -a v0.1.1 -m "Release 0.1.1: Bug fixes and improvements"
git push origin main v0.1.1

# Automatically publishes via GitHub Actions
```

## Usage After Publishing

Once on Maven Central, users add:

```scala
// build.sbt
libraryDependencies += "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0"
```

Or individual modules:

```scala
libraryDependencies ++= Seq(
  "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-omics" % "0.1.0",
  "io.github.olaflaitinen" %% "schneileopard-ai" % "0.1.0"
)
```

## Troubleshooting

**"Publish Artifacts" job skipped?**
- Check tag was pushed: `git push origin v0.1.0`
- Verify tag matches pattern `v*`: `git tag -l`

**Tests failing in CI?**
- Check GitHub Actions logs: https://github.com/olaflaitinen/schneileopard/actions
- May be sbt caching issue, try: clear GitHub cache

**GPG key signature failing?**
- Verify passphrase is correct in secrets
- Check key uploaded to keyserver (wait 10 min)
- Regenerate key if issues persist

**Artifact not on Maven Central?**
- Takes 5-30 minutes for staging → staging promoted by release workflow
- Then 1-2 hours to sync to Central mirror
- Check Sonatype first: https://s01.oss.sonatype.org/

## Full Documentation

For detailed information:
- **PUBLICATION.md**: Complete step-by-step guide with troubleshooting
- **GitHub Actions Logs**: https://github.com/olaflaitinen/schneileopard/actions
- **Sonatype Help**: https://central.sonatype.org/publish/publish-guide/

---

*Quick Start Setup - Last Updated: March 2026*
*Next Review: June 2026*
