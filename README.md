# RepoVista Android Scaffold

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

After generating the Gradle wrapper locally, run:

```bash
./gradlew test
```

## GitHub token setup (optional)

GitHub API requests are unauthenticated by default. You can optionally provide a Personal Access Token (PAT) in the app to increase your rate limits.

1. Launch the app.
2. Tap **GitHub Token Settings**.
3. Paste a GitHub PAT and tap **Save**.

Notes:

- The token is stored locally using Android DataStore Preferences.
- Leave the token empty to clear it.
- The app redacts the `Authorization` header from HTTP logs.
