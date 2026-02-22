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
