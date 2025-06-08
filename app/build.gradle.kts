plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}


dependencies {

}
android {
    namespace = "com.example.buildup"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.buildup"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson 컨버터 추가
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.activity:activity-ktx:1.7.2") // 최신 버전으로 맞추세요
    implementation("androidx.fragment:fragment-ktx:1.5.7") // fragment-ktx도 같이 쓰면 좋아요
    implementation("com.airbnb.android:lottie:5.2.0")
    // CardView 라이브러리 추가 (최신 버전으로 확인해서 사용하세요)
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("com.google.android.material:material:1.8.0")


    implementation ("com.kakao.sdk:v2-all:2.20.0") // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation ("com.kakao.sdk:v2-user:2.20.0") // 카카오 로그인 API 모듈
//    implementation ("com.kakao.sdk:v2-share:2.20.0") // 카카오톡 공유 API 모듈
//    implementation ("com.kakao.sdk:v2-talk:2.20.0") // 카카오톡 채널, 카카오톡 소셜, 카카오톡 메시지 API 모듈
//    implementation ("com.kakao.sdk:v2-friend:2.20.0") // 피커 API 모듈
//    implementation ("com.kakao.sdk:v2-navi:2.20.0") // 카카오내비 API 모듈
//    implementation ("com.kakao.sdk:v2-cert:2.20.0") // 카카오톡 인증 서비스 API 모듈


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}