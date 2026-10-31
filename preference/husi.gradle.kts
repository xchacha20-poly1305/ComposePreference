// Build the upstream sources with Husi's toolchain and shipped platforms.
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.kotlin.multiplatform.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.multiplatform)
}

group = "me.zhanghai.compose.preference"
version = "2.2.0"

kotlin {
    explicitApi()
    compilerOptions {
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
    }
    android {
        namespace = "me.zhanghai.compose.preference"
        buildToolsVersion = "37.0.0"
        compileSdk {
            version = release(37) { minorApiLevel = 2 }
        }
        minSdk = 23
        androidResources { enable = true }
        optimization {
            consumerKeepRules.apply {
                publish = true
                file("consumer-proguard-rules.pro")
            }
        }
    }
    jvm {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_21) }
    }
    sourceSets {
        commonMain.dependencies {
            api(libs.jetbrains.compose.foundation)
            implementation(libs.jetbrains.compose.material3)
            implementation(libs.jetbrains.compose.components.resources)
            implementation(libs.jetbrains.lifecycle.runtime.compose)
        }
        jvmMain {
            kotlin.srcDir("src/jvmWasmJsMain/kotlin")
            dependencies { implementation(libs.kotlinx.serialization.json) }
        }
    }
}

compose.resources {
    packageOfResClass = "composepreference.preference.generated.resources"
}
