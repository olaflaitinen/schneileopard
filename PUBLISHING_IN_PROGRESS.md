# 🚀 PUBLICATION IN PROGRESS

Schnéileopard v0.1.0 şu anda **Maven Central**'e yayınlanıyor.

---

## ✅ SetUp Tamamlandı

### Sonatype Credentials
```
Username:  99a2Ni
Namespace: io.github.olaflaitinen (approved)
```

### GPG Key Generated & Published
```
Key ID:         229E4C195A491E58
Fingerprint:    6E3155F10D6766A0A9A6B8E1229E4C195A491E58
Status:         ✓ Published to keyserver.ubuntu.com
Algorithm:      RSA-4096
Email:          olaf.laitinen@uni.lu
```

### GitHub Secrets Configured
```
✓ SONATYPE_USERNAME     = 99a2Ni
✓ SONATYPE_PASSWORD     = [authenticated]
✓ GPG_PASSPHRASE        = [configured]
✓ GPG_PRIVATE_KEY       = [4096-bit RSA key]
```

### v0.1.0 Tag Pushed
```
Tag:     v0.1.0 (recreated with secrets)
Status:  ✓ Pushed to GitHub
```

---

## 📊 Publishing Timeline

| Aşama | Timeline | Status |
|-------|----------|--------|
| GitHub Actions Publish | Şu an | 🔄 Running |
| Maven Central Staging | 5-30 min | ⏳ Pending |
| Maven Central Mirror | 30 min - 2 saat | ⏳ Pending |
| Scaladex Index | 24 saat | ⏳ Pending |

---

## 🔗 Monitoring

### GitHub Actions Status
👉 https://github.com/olaflaitinen/schneileopard/actions

Look for: **"Publish Artifacts"** job running with tag `v0.1.0`

---

## ✅ Artifacts Being Published

1. **schneileopard-core** (0 external deps) - Scala stdlib candidate
2. **schneileopard-omics** - Expression matrices & normalization
3. **schneileopard-graph** - Interaction networks
4. **schneileopard-ai** - Feature ranking & explainability
5. **schneileopard-io** - CSV I/O codecs

---

## 📦 Verification Checklist (After Publishing)

### Step 1: Maven Central Staging (5-30 min)
When complete, check:
```
https://s01.oss.sonatype.org/#nexus-search;quick~io.github.olaflaitinen
```
Should show all 5 modules with version 0.1.0

### Step 2: Maven Central Mirror (30 min - 2 hours)
When complete, check:
```
https://search.maven.org/search?q=io.github.olaflaitinen
https://central.maven.org/artifact/io.github.olaflaitinen/schneileopard-core/0.1.0
```
Should show published artifacts

### Step 3: Scaladex Index (24 hours)
When complete, check:
```
https://index.scala-lang.org/search?q=schneileopard
```
Should list all Schnéileopard modules

### Step 4: Test Installation
Create test project:
```bash
# build.sbt
libraryDependencies += "io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0"

// Usage
import io.github.olaflaitinen.schneileopard.core.*
val id = GeneId("ENSG00000000003")
```

---

## 📝 What Was Configured

### 1. Sonatype Account ✓
- Namespace: `io.github.olaflaitinen` (GitHub-verified)
- Authentication: Token-based
- Release repo: Sonatype bundle release enabled

### 2. GPG Key ✓
- Algorithm: RSA-4096
- Published to: keyserver.ubuntu.com
- For artifact signing: schneileopard v0.1.0 artifacts

### 3. GitHub Secrets ✓
- Sonatype credentials encrypted
- GPG private key encrypted
- Accessible only to GitHub Actions workflow

### 4. Release Workflow ✓
- Trigger: v* tag pushed
- Steps:
  1. Checkout code
  2. Setup Java 17
  3. Import GPG key
  4. Run `sbt publishSigned`
  5. Run `sbt sonatypeBundleRelease`

---

## 📋 Next Steps

1. **Wait for GitHub Actions** (~5-10 min)
   - Check Actions tab for "Publish Artifacts" job status

2. **Verify on Maven Central** (~30 min - 2 hours)
   - Search for `io.github.olaflaitinen` on Maven Central
   - Should find all 5 modules at version 0.1.0

3. **Verify on Scaladex** (~24 hours)
   - Search for "schneileopard"
   - Should list all modules with links to GitHub

4. **Announce Release**
   - Scala Discourse (New Releases)
   - Scala Reddit community
   - Twitter/X announcement
   - GitHub Releases page

---

## 🎯 Success Criteria

✅ GitHub Actions workflow completes without errors
✅ Artifacts appear in Maven Central Staging
✅ Artifacts sync to Maven Central Mirror
✅ Scaladex indexes all modules
✅ Users can add dependency: `"io.github.olaflaitinen" %% "schneileopard-core" % "0.1.0"`

---

**Status: v0.1.0 publishing in progress! 🚀**

*Last Updated: March 29, 2026 - Real-time monitoring*
