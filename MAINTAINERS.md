# Maintaining Schnéileopard

This document provides guidance for project maintainers on governance, decision-making, and operational procedures.

## Current Maintainers

- **Project Founder & Lead:** Gustav Olaf Yunus Laitinen-Fredriksson Lundstrom-Imanov
  - Email: olaf.laitinen@uni.lu
  - Affiliation: University of Luxembourg, Department of Life Sciences and Medicine
  - ORCID: 0009-0006-5184-0810
  - Role: Architecture decisions, API design, research direction

## Maintainer Responsibilities

### Primary Responsibilities

1. **Code Review** - Review and approve/reject pull requests
   - Verify code quality and consistency
   - Check test coverage
   - Ensure documentation completeness
   - Provide constructive feedback

2. **Issue Management** - Triage and track issues
   - Respond to bug reports within 48 hours
   - Label issues appropriately
   - Close resolved issues
   - Track feature requests

3. **Release Management** - Manage versions and releases
   - Plan release schedule
   - Update CHANGELOG.md
   - Create version tags
   - Publish to Maven Central

4. **Documentation** - Maintain user-facing documentation
   - Update GETTING_STARTED.md
   - Review Scaladoc quality
   - Maintain README.md
   - Update architecture docs

5. **Community** - Engage with users and contributors
   - Answer questions on GitHub Discussions
   - Respond to security reports
   - Welcome contributions
   - Recognize contributors

### Secondary Responsibilities (When Needed)

- Dependency updates and security patches
- Performance profiling and bottleneck fixes
- Infrastructure and CI/CD improvements
- Public communication and announcements

## Decision-Making Process

### Routine Decisions (Maintainer Authority)

**Maintainers can make independently:**
- Bug fixes and patches
- Small documentation improvements
- Typo corrections
- Test additions for existing functionality
- Dependency patch updates (0.0.x)
- GitHub workflow improvements

### Major Decisions (Discussion Required)

**Requires community input or consensus:**
- New public API types (request feedback via issue first)
- Breaking changes
- Major architectural decisions
- New dependencies
- Significant performance trade-offs
- Removal of existing functionality

**Process:**
1. Open GitHub Issue for discussion
2. Allow 1-2 weeks for feedback
3. Document decision and rationale
4. Implement with transparency

### Release Decisions

**Standard Release (maintainer authority):**
- Patch releases (0.1.x) - bug fixes only
- Minor releases (0.2.x) - new features, backward compatible
- Scheduled based on roadmap

**Major Release (community + maintainer):**
- Major releases (1.0, 2.0) - months of planning
- Roadmap discussion 3 months before release
- Feature freeze 4 weeks before release
- Release candidate period 1 week before release

## Release Procedures

### Pre-Release Checklist

1. **Code Preparation**
   ```bash
   git checkout main
   git pull origin main
   sbt clean test doc
   sbt scalafmtCheckAll
   ```

2. **Version Update (build.sbt)**
   ```scala
   ThisBuild / version := "0.2.0"
   ```

3. **Update CHANGELOG.md**
   - List all changes with categories
   - Link to issues/PRs where applicable
   - Note any breaking changes

4. **Update Version Documentation**
   - VERSIONING.md with compatibility info
   - COMPATIBILITY.md if needed
   - Migration guide if breaking

5. **Commit Release Commit**
   ```bash
   git add build.sbt CHANGELOG.md
   git commit -m "chore: prepare 0.2.0 release"
   ```

6. **Create and Push Tag**
   ```bash
   git tag -a v0.2.0 -m "Release version 0.2.0"
   git push origin main
   git push origin v0.2.0
   ```

### Post-Release

1. **Verify Publication**
   - Check Maven Central (may take 2-4 hours)
   - Verify Scaladex indexing (next day)
   - Check GitHub release was created

2. **Announce Release**
   - Update GitHub Releases page
   - Post to research community channels
   - Email interested parties

3. **Bump Development Version**
   ```scala
   ThisBuild / version := "0.2.1-SNAPSHOT"
   ```
   Commit and push.

## Handling Issues

### Triage Workflow

**Upon Issue Creation:**
1. Read issue completely
2. Ask clarifying questions if needed
3. Label appropriately (bug/feature/question/wontfix)
4. Assign to self if you'll handle it

**Bug Reports:**
- Label: `bug`
- Request: minimal reproduction, Java/Scala versions
- Estimate: severity (critical/major/minor)
- Assign: responsible developer
- Target: milestone for fix attempt

**Feature Requests:**
- Label: `enhancement`
- Discuss: design in issue comments
- Consensus: reach group agreement before implementation
- Target: roadmap milestone

**Questions:**
- Label: `question`
- Provide: answer or point to docs
- Convert: to discussion if needs longer thread
- Close: once answered

### Issue States

```
Open → Assigned → In Progress → Code Review → Closed
  ↓                                    ↓
Waiting for Response              Wontfix/Declined
  ↓
Closed (Stale)
```

### Stale Issue Management

- Auto-label issues with no activity >30 days as `stale`
- Comment: "This issue is stale. If still relevant, please comment."
- Close: after 7 more days of inactivity
- Allow: reopening if needed

## Pull Request Review

### Review Checklist

Before approving any PR:

- [ ] Code compiles cleanly
- [ ] Tests pass (86/86+)
- [ ] New tests added for new behavior
- [ ] Code is formatted (scalafmt)
- [ ] Scaladoc present for public APIs
- [ ] No breaking changes (or clearly documented)
- [ ] Commit messages are clear
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] No warnings in build

### Review Comments

**Helpful comments include:**
- "Why did you choose X over Y?"
- "Have you considered Z alternative?"
- "This pattern conflicts with our style, see docs/DESIGN.md"
- "Great addition! Could you add a test for edge case...?"

**Avoid:**
- Tone-deaf criticism ("This is obviously wrong")
- Demanding changes without explanation
- Bikeshedding on style (scalafmt handles this)

### Approval and Merge

**Requirements to Merge:**
- At least one maintainer approval
- All tests passing
- No merge conflicts
- All conversations resolved

**Merge Strategy:** Use "Squash and merge" for feature branches, "Create a merge commit" for release branches.

## Security and Vulnerability Handling

### Reporting Vulnerabilities

Direct security reports to: olaf.laitinen@uni.lu

**Do NOT:**
- Open public GitHub issue
- Discuss on social media
- Include exploit details

**Include:**
- Clear description of vulnerability
- Affected versions
- Steps to reproduce (if possible)
- Suggested fix (if you have one)

### Vulnerability Response Timeline

1. **Within 24 hours:** Acknowledge receipt, begin investigation
2. **Within 3 days:** Confirm/reject vulnerability, propose timeline
3. **Within 7 days:** Release patch with fix (if confirmed)
4. **Within 14 days:** Public disclosure with credit to reporter

## Maintenance Cycles

### Daily/Weekly

- [ ] Review new issues/PRs (daily)
- [ ] Respond to comments (daily)
- [ ] Run CI checks (automatic)
- [ ] Triage stale issues (weekly)

### Monthly

- [ ] Review roadmap progress
- [ ] Check dependency updates needed
- [ ] Review community feedback
- [ ] Plan next sprint

### Quarterly

- [ ] Review and update ROADMAP.md
- [ ] Assess API stability
- [ ] Plan major version releases
- [ ] Document lessons learned

### Annually

- [ ] Total project review
- [ ] Community survey
- [ ] Strategic planning
- [ ] Recognition of contributors

## Delegation and Escalation

### Escalation Triggers

Escalate to lead maintainer if:
- Breaking API change proposed
- Major dependency conflict
- Security issue found
- Community disagreement
- Significant performance regression
- Architectural questions

### Delegation Guidelines

**You can delegate to trusted contributors:**
- Reviewing straightforward bug fixes
- Merging documentation PRs
- Triage of issues
- Basic code cleanup

**Always review yourself:**
- New public API
- Algorithm changes
- Core module changes
- Release procedures

## Repository Maintenance

### Branch Policies

- **main:** Stable, always-releasable code
- **feature/*:** Development branches for new features
- **fix/*:** Development branches for bug fixes
- **release/*:** Release preparation branches

### Merge Requirements

- Main branch requires PR review before merge
- Status checks must pass
- Scalafmt compliance required
- Minimum test coverage maintained

### Cleanup Tasks

Monthly:
- Delete stale branches
- Close resolved issues
- Archive completed milestones

Quarterly:
- Review GitHub Actions for optimization
- Update security settings
- Audit collaborator access

## Onboarding New Maintainers

### When to Add Maintainers

- Substantial code contributions (50+ commits)
- Demonstrated commitment (6+ months)
- Community respect and positive interactions
- Agreement from existing maintainers

### Onboarding Process

1. **Discuss privately** with current maintainers
2. **Propose enhancement** to show commitment
3. **Invite to maintainer** role
4. **Pair programming** on first few PRs
5. **Gradual delegation** of responsibilities
6. **Document in AUTHORS.md**

### Maintaining Momentum

- Monthly check-ins with new maintainers
- Pair on complex decisions initially
- Gradual responsibility transfer
- Mentorship in project governance

## Community and Communication

### Communication Channels

- **GitHub Issues:** Bug reports, feature requests
- **GitHub Discussions:** Design decisions, questions
- **Email:** Long discussions, security reports
- **Research Channels:** Academic conferences, community meetings

### Communication Tone

- Professional and respectful
- Assume good intent
- Provide context and explanation
- Welcome diverse perspectives
- Address conflicts privately first

### Regular Communication

- Quarterly project updates
- Release announcement for significant versions
- Monthly digest for active discussions
- Annual retrospective

## Documentation Maintenance

### Documentation Locations

- **docs/:** Architecture and design documents
- **README.md:** Project overview
- **GETTING_STARTED.md:** Quick start guide
- **API.md:** API reference
- **Scala doc comments:** Function documentation

### Update Triggers

Update which docs when:
- **Public API change:** Update API.md + Scaladoc
- **Architecture change:** Update docs/ARCHITECTURE.md
- **Process change:** Update BUILD.md + CONTRIBUTING.md
- **Roadmap change:** Update ROADMAP.md
- **Bug fix:** Consider docs if related to user confusion

### Documentation Review

All PRs must have:
- Updated Scaladoc for public changes
- Updates to relevant .md files
- No broken links
- Consistent terminology

## Performance and Optimizations

### Monitoring

- Run benchmarks on each release
- Track metrics in CHANGELOG.md
- Alert if performance regresses >5%
- Document optimization trade-offs

### Optimization Process

1. **Profile** code to identify bottlenecks
2. **Propose** optimization with trade-offs explained
3. **Benchmark** before and after
4. **Review** with team for correctness
5. **Document** in code and CHANGELOG

## Long-Term Vision

### 5-Year Plan

- **2026:** Establish stable 0.x/1.0 API
- **2027:** Strong user community (100+ active users)
- **2028:** Integration with major bioinfo tools
- **2029:** Multiple maintainers taking active roles
- **2030:** Recognized as standard Scala omics library

### Success Metrics

- Cited in 20+ research papers
- 1000+ GitHub stars
- 50+ external contributors
- Active community on discussions
- Stable API that rarely changes

---

*Maintenance Guide - Last Updated: March 2026*
*Next Review: June 2026*
