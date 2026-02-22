pluginManagement {
    repositories {
        maven("https://dl.google.com/dl/android/maven2/")
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application", "com.android.library" -> {
                    useModule("com.android.tools.build:gradle:${requested.version}")
                }
            }
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven("https://dl.google.com/dl/android/maven2/")
        google()
        mavenCentral()
    }
}

rootProject.name = "RepoVista"
include(
    ":app",
    ":core:ui",
    ":core:network",
    ":core:model",
    ":core:database",
    ":feature:search",
    ":feature:profile",
    ":feature:repodetail",
    ":feature:issues"
)
