import Modules.Applications
import Modules.Libraries

plugins {
    id("HappyXPlugin")
    id("com.android.dynamic-feature")
}

happyPlugin {
    viewBindingEnabled = true
}

dependencies {
    implementation(platform(libs.firebase.bom))

    implementation(libs.android.material)
    implementation(libs.android.play.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.viewmodel.ktx)
    implementation(libs.glide)
    implementation(libs.coroutines.android)
    implementation(libs.firebase.firestore)
    implementation(libs.koin.android)
    implementation(libs.koin.viewmodel)
    implementation(libs.sheets.input)
    implementation(libs.viewbinding.delegates)
}

withProjectLibraries(
    Applications.ladyHappy,

    Libraries.auth,
    Libraries.core,
    Libraries.extensions,
    Libraries.localization,
    Libraries.mozaik,
    Libraries.network
)