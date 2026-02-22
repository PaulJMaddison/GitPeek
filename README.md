# RepoVista — GitHub Repository Viewer

RepoVista is a production-minded Android app built with Kotlin and modern Jetpack libraries for exploring GitHub repositories, users, issues, and starred projects.

## Features

- Repository search with Paging 3
- User profile details
- Repository details
- Starred repositories list
- Issues list
- Loading, empty, and error UI states
- Material 3 + dark mode support

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **Architecture**: Clean Architecture (data/domain/presentation) + MVVM
- **Networking**: Retrofit + OkHttp + Moshi
- **Async**: Coroutines + Flow
- **Pagination**: Paging 3 (+ compose integration)
- **Dependency Injection**: Hilt
- **Testing**: JUnit, MockK, Turbine, MockWebServer

## Architecture Overview

```
com.repovista
├── data
│   ├── remote (Retrofit API + DTOs)
│   └── repository (PagingSources + repository implementation)
├── domain
│   ├── model
│   ├── repository (contracts)
│   └── usecase
├── presentation
│   ├── navigation
│   ├── {search,user,repo,starred,issues}
│   └── components
├── di
└── ui/theme
```

## Screenshots

> Add screenshots here after running on device/emulator.

- Search Screen: `docs/screenshots/search.png`
- User Profile: `docs/screenshots/user.png`
- Repo Details: `docs/screenshots/repo_details.png`
- Starred Repos: `docs/screenshots/starred.png`
- Issues List: `docs/screenshots/issues.png`

## Setup

1. Install Android Studio (Koala+), Android SDK 34.
2. Clone this repository.
3. Open in Android Studio.
4. Build and run on an emulator/device.

CLI build:

```bash
./gradlew :app:assembleDebug
```

Run tests:

```bash
./gradlew test
```

## Notes

- GitHub API is used via unauthenticated requests by default.
- For higher rate limits, add token-based auth in the OkHttp layer.
