# FarmTweaks agent guidance

## Project identity

- This is the `farmtweaks` NeoForge mod.
- Minecraft version: `1.21.1`.
- NeoForge version: `21.1.219`.
- Java version: 21.
- Build system: Gradle with Groovy `build.gradle` and ModDevGradle.
- Main package: `com.sncial.farmtweaks`.
- Main resources: `src/main/resources`.
- Generated resources: `src/generated/resources`.

Confirm `gradle.properties` and `build.gradle` before making loader- or version-specific changes. This repository is currently NeoForge-only; do not introduce Fabric or Architectury structure unless the task explicitly requests a migration or multiloader implementation.

## Skill routing

Use the globally available skills when the task matches their scope:

- `$minecraft-modding` for NeoForge/Fabric mod code, registries, events, mixins, networking, datagen, recipes, assets, and loader migrations.
- `$minecraft-testing` for JUnit tests, NeoForge GameTests, future Fabric GameTests, test fixtures, integration tests, and CI test jobs.
- `$test-driven-development` before implementing a feature or bug fix when a testable behavior can be specified first.
- `$code-reviewer` when reviewing a diff, staged changes, or a proposed patch.
- `$architecture-designer` for new subsystems, loader abstractions, compatibility boundaries, or larger refactors.
- `$codebase-documenter` when updating the README, architecture notes, or developer documentation.
- `$owasp-security-check` when reviewing network-facing, permission-sensitive, configuration, or data-handling changes.

Keep skill responsibilities separate: `minecraft-modding` handles implementation, while `minecraft-testing` handles test design and execution. Do not add Paper/Bukkit or MockBukkit dependencies to this mod unless the task explicitly changes the project target.

## Validation commands

Run the smallest relevant check, then use the full build for changes crossing Java and resource boundaries:

```powershell
.\gradlew.bat build
.\gradlew.bat runGameTestServer
.\gradlew.bat runData
.\gradlew.bat runClient
.\gradlew.bat runServer
.\gradlew.bat printVersion
.\gradlew.bat release
.\gradlew.bat releasePatch
.\gradlew.bat releaseMinor
.\gradlew.bat releaseMajor
```

The GitHub Actions build runs `./gradlew build` on Ubuntu with JDK 21. Keep CI commands POSIX-compatible even though local development commonly uses `gradlew.bat` on Windows.

Normal builds derive build metadata from `GITHUB_RUN_NUMBER`, `BUILD_NUMBER`, or
an explicit `-Pbuild_number=<value>` without editing `gradle.properties`. The
`releasePatch`, `releaseMinor`, and `releaseMajor` tasks change `mod_version`
according to the intended SemVer level. The `release` task remains a patch-release
alias. Each requires a clean Git worktree, runs the new-version build first, then
commits and tags locally. None of them pushes automatically.

## Commit and push gate

- Whenever a task would commit or push changes, ask the user which release level to use: `releasePatch`, `releaseMinor`, or `releaseMajor`.
- Do not choose a release level implicitly and do not commit or push before the user answers.
- Use the selected release task for the release operation; ordinary `build` verification must happen separately first.
- For testing and validation, always use the normal build command (`.\gradlew.bat build` locally or `./gradlew build` in CI). Do not use a release task merely to test compilation.

## Change conventions

- Preserve the `farmtweaks` namespace and `com.sncial.farmtweaks` package.
- Keep client-only code out of common/server paths; review `FarmTweaksClient` and mixins when changing rendering or client setup.
- Update Java code and all matching resources together: recipes, tags, models, textures, translations, metadata, and generated data where applicable.
- Prefer NeoForge registries, events, and data generation over ad hoc runtime mutation or hand-authored generated output.
- Preserve optional integrations as optional. Cloth Config and Serene Seasons must not become hard runtime dependencies without an explicit request.
- Do not change mod version, license, or publishing behavior as incidental cleanup.

## Testing expectations

- Prefer pure unit tests for isolated logic and NeoForge GameTests for world, block, entity, interaction, and registration behavior.
- For GameTests, keep structure templates under `src/main/resources/data/farmtweaks/structures/` and keep template IDs aligned with test annotations.
- Do not claim a test passed unless the relevant Gradle task was actually run or the limitation is stated.
- Avoid adding test-only dependencies or CI jobs speculatively; add them with the behavior they verify.

## Project Management Workflow

GitHub Issues are the project's lightweight backlog and idea-capture system. Long-lived technical specifications, architectural notes, decisions, and research belong in the repository under `docs/`.

Use these GitHub Issue labels consistently:

- `idea` — rough feature or product idea that has not yet been technically investigated
- `feature` — approved feature work
- `bug` — defect, regression, or incorrect behavior
- `investigate` — requires research or codebase investigation before implementation
- `experiment` — exploratory or proof-of-concept work
- `tech-debt` — refactoring, cleanup, maintenance, or architectural improvement
- `documentation` — documentation-related work

When a user references an Issue, Codex must:

1. Read the Issue first.
2. Inspect the existing codebase before proposing changes.
3. Treat `idea` Issues as product concepts, not complete technical specifications.
4. Treat implementation suggestions in Issues as input, not instructions to follow blindly.
5. Determine how the request fits the current architecture.
6. Prefer extending or reusing existing components, APIs, utilities, schemas, services, and patterns over creating duplicate systems.
7. Identify expected behavior, affected systems, edge cases, dependencies, risks, and possible migrations or schema changes.
8. If asked to `investigate issue #X`, make no code changes; inspect the repository and return a technical implementation plan.
9. If asked to `implement issue #X`, inspect the Issue and codebase first, then implement it according to repository conventions.
10. Run relevant tests, type checks, linting, or validation after implementation where available.
11. Do not modify or close unrelated Issues.

The normal flow is:

```text
Idea captured on GitHub Mobile
        ↓
GitHub Issue labeled `idea`
        ↓
Codex investigates Issue
        ↓
Technical plan created
        ↓
Issue becomes approved feature
        ↓
Optional detailed spec in docs/features/
        ↓
Codex implements it
        ↓
Tests / verification
        ↓
PR / commit references Issue
```

Useful Codex commands include:

```text
Investigate issue #42. Do not implement anything yet.

Implement issue #42.

Review all open issues labeled `idea` and recommend the best three candidates to work on next. Do not make code changes.

Turn issue #42 into a detailed feature specification under docs/features/. Do not implement it.

Implement the feature described in docs/features/example-feature.md and update the implementation notes afterward.
```
