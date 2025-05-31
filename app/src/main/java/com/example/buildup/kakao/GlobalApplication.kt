package com.example.buildup.kakao

import android.app.Application
import com.example.buildup.R // <-- 여러분의 실제 패키지명.R 로 바꿔야 합니다!
// 또는 import android.R (이것은 잘못된 R이므로 있으면 제거하세요)
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 다른 초기화 코드들

        // Kakao SDK 초기화 (올바른 방식)
        // R.string.kakao_app_key는 strings.xml에 정의된 네이티브 앱 키를 가져옵니다.
        // 예를 들어 <string name="kakao_app_key">e6e454a1f0c187fb8abf3b764b1a5ce3</string>
        KakaoSdk.init(this, getString(R.string.kakao_app_key))
    }
}