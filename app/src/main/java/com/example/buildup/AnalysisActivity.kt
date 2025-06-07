package com.example.buildup

import android.Manifest
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.buildup.api.ApiManager
import com.example.buildup.databinding.ActivityAnalysisBinding

class AnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysisBinding

    private val PERMISSION_REQUEST_CODE = 100

    private var currentPhotoUri: Uri? = null

    // 사진 촬영 런처
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                binding.photoImageView.setImageURI(uri)
            }
        } else {
            Toast.makeText(this, "사진 촬영 실패", Toast.LENGTH_SHORT).show()
        }
    }

    // 사진 선택 런처
    private val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            currentPhotoUri = uri
            try {
                // 영구 권한 요청
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            binding.photoImageView.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalysisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("user_info", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", null)  // 아이디만 가져오기

        binding.realShotBtn.setOnClickListener {
            if (hasAllPermissions()) {
                currentPhotoUri = createImageUri()
                currentPhotoUri?.let {
                    takePictureLauncher.launch(it)
                }
            } else {
                requestNecessaryPermissions()
            }
        }

        binding.shotRegisterBtn.setOnClickListener {
            if (hasStoragePermission()) {
                pickPhotoLauncher.launch("image/*")
            } else {
                requestStoragePermission()
            }
        }

        binding.buttonReturnToMain.setOnClickListener {
            finish()
        }

        binding.analyzeButton.setOnClickListener {
            val uri = currentPhotoUri
            if (uri != null) {
                val filePath = getRealPathFromUri(uri)
                if (filePath != null) {
                    Toast.makeText(this, "분석 시작!", Toast.LENGTH_SHORT).show()

                    ApiManager.uploadAndAnalyzeImage(filePath, userId.toString()) { success, response, error ->
                        runOnUiThread {
                            if (success && response != null) {
                                Toast.makeText(this, "분석 결과: 성공", Toast.LENGTH_LONG).show()
                            } else {
                                Log.e("Error", error.toString())
                                Toast.makeText(this, "분석 실패: $error", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "파일 경로를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "분석할 사진을 먼저 등록해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 모든 필요한 권한 체크 (카메라 + 저장소)
    private fun hasAllPermissions(): Boolean {
        return hasCameraPermission() && hasStoragePermission()
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 이상은 READ_MEDIA_IMAGES 권한 사용
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 이하
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    // 필요한 권한 요청 (카메라 + 저장소)
    private fun requestNecessaryPermissions() {
        val permissions = mutableListOf<String>()
        if (!hasCameraPermission()) {
            permissions.add(Manifest.permission.CAMERA)
        }
        if (!hasStoragePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
    }

    // 저장소 권한만 따로 요청 (사진 선택용)
    private fun requestStoragePermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "권한이 승인되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createImageUri(): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    // Uri -> 실제 파일 경로 변환
    private fun getRealPathFromUri(uri: Uri): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                path = it.getString(columnIndex)
            }
        }
        return path
    }
}
