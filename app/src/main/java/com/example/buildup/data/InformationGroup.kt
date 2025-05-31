package com.example.buildup.data

data class InformationGroup(
    val id: Int,
    val name: String,
    val description: String
)

//설명:
//
//import retrofit2.Retrofit: Retrofit 클래스를 가져옵니다.
//import retrofit2.converter.gson.GsonConverterFactory: Gson을 사용하여 서버 응답을 Java/Kotlin 객체로 변환하는 Converter Factory를 가져옵니다.
//companion object: 클래스의 인스턴스 생성 없이 접근할 수 있는 객체를 정의합니다. apiService 인스턴스를 앱 실행 중에 한 번만 생성하도록 lazy 속성을 사용했습니다.
//private const val BASE_URL = "YOUR_FASTAPI_SERVER_URL": FastAPI 서버의 기본 URL을 저장하는 상수입니다. 반드시 실제 서버 주소로 변경해야 합니다. (예: http://192.168.0.100:8000 또는 로컬 개발 시 에뮬레이터의 경우 http://10.0.2.2:8000).
//val apiService: ApiService by lazy { ... }: ApiService 타입의 apiService 프로퍼티를 정의하고, lazy를 사용하여 처음 접근될 때 Retrofit 인스턴스를 생성합니다.
//Retrofit.Builder(): Retrofit 인스턴스를 생성하기 위한 빌더를 생성합니다.
//.baseUrl(BASE_URL): 서버의 기본 URL을 설정합니다. 모든 API 요청은 이 URL을 기준으로 상대 경로로 만들어집니다.
//.addConverterFactory(GsonConverterFactory.create()): 응답 데이터를 Gson을 사용하여 파싱하도록 Gson Converter Factory를 추가합니다. 서버가 JSON 형태의 데이터를 반환할 때 필요합니다.
//.build(): 설정된 옵션으로 Retrofit 인스턴스를 생성합니다.
//.create(ApiService::class.java): 생성된 Retrofit 인스턴스를 사용하여 ApiService 인터페이스의 구현체를 만듭니다. 이제 apiService 객체를 통해 ApiService에 정의된 함수들을 호출하여 서버와 통신할 수 있습니다.