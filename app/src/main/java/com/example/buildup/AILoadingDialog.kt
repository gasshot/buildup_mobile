package com.example.buildup

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.NonNull

class AILoadingDialog(@NonNull context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_ai_dialog) // 기존 로딩 레이아웃 재사용 가능

        // 다이얼로그 외부 클릭 시 닫히지 않도록 설정
        setCancelable(false)

        // 다이얼로그가 화면 전체를 덮도록 크기 및 배경 설정
        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.6f)
        }
    }
}
