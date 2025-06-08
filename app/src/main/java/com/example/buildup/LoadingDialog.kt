package com.example.buildup

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.NonNull

class LoadingDialog(@NonNull context: Context) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loading_dialog)

        // 다이얼로그 외부 클릭 시 닫히지 않도록 설정
        setCancelable(false)

        // 다이얼로그가 화면 전체를 덮도록 크기 및 배경 설정
        window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawableResource(android.R.color.transparent) // 배경 투명 또는 반투명 조절 가능
            setDimAmount(0.6f)  // 배경 어둡게 (0f 완전 투명 ~ 1f 완전 불투명)
        }
    }
}
