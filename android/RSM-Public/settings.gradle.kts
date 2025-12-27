pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        //maven {
        //    url = uri("https://jitpack.io")
        //    credentials {
        //        // Имя пользователя для JitPack - это сам токен
        //        username = settings.extra["jitpack.github.token"] as? String
        //        // Пароль оставляем пустым
        //        password = ""
        //    }
        //}

    }
}


rootProject.name = "RSM"
include(":app")
