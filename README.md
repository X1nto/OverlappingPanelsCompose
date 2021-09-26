# OverlappingPanelsCompose
Jetpack Compose implementation of [Discord's Overlapping Panels](https://github.com/discord/OverlappingPanels)

Installation
-------
### Groovy
Add JitPack repository to root build.gradle
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
Add OverlappingPanelsCompose dependency to app build.gradle
```groovy
dependencies {
    implementation 'com.github.X1nto:OverlappingPanelsCompose:1.0.0'
}
```
### Kotlin DSL
Add JitPack repository to root build.gradle
```kotlin
allprojects {
    repositories {
        ...
        maven(url = "https://jitpack.io")
    }
}
```
Add OverlappingPanelsCompose dependency to app build.gradle
```kotlin
dependencies {
    implementation("com.github.X1nto:OverlappingPanelsCompose:1.0.0")
}
```

Usage
-------
```kotlin
val panelsState = rememberOverlappingPanelsState()
OverlappingPanels(
    modifier = Modifier.fillMaxSize(),
    panelsState = panelsState,
    panelStart = { /* Start Panel content */ },
    panelCenter = { /* Center Panel content */ },
    panelEnd = { /* End Panel content */ },
)
```
Check out the [sample app](/app) for examples on how to use the library.

License
-------
```
Copyright (C) 2020 X1nto.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
