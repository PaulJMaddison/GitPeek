# RepoVista Android Scaffold

[![CI](https://img.shields.io/badge/CI-passing-placeholder?logo=githubactions)](https://github.com/your-org/your-repo/actions)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Contributing](https://img.shields.io/badge/contributions-welcome-brightgreen.svg)](CONTRIBUTING.md)

Multi-module Android scaffold using **Gradle Kotlin DSL** and **Version Catalog**.

## Modules

- `:app`
- `:core:ui`
- `:core:network`
- `:feature:search`
- `:feature:profile`
- `:feature:repodetail`
- `:feature:issues`

## Configuration

- minSdk: **24**
- compileSdk/targetSdk: **35**
- Jetpack Compose enabled
- Hilt plugin + kapt configured in `:app`
- Packaging resource excludes added for common META-INF conflicts

## Verification Screen

The launcher activity renders a simple Compose message:

- **Hello RepoVista**

## Sanity check

Run:

```bash
./gradlew test
./gradlew assembleDebug
```

## Community

- Read [CONTRIBUTING.md](CONTRIBUTING.md) before opening a PR.
- Follow our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).
- Use GitHub issue templates for bugs and feature requests.

## GitHub token setup (optional)

GitHub API requests are unauthenticated by default. You can optionally provide a Personal Access Token (PAT) in the app to increase your rate limits.

1. Launch the app.
2. Tap **GitHub Token Settings**.
3. Paste a GitHub PAT and tap **Save**.

Notes:

- The token is stored locally using Android DataStore Preferences.
- Leave the token empty to clear it.
- The app redacts the `Authorization` header from HTTP logs.
