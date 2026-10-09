import java.util.Properties

plugins {
    id("com.android.application")
}

// ---------------------------------------------------------------------------
// API keys
// ---------------------------------------------------------------------------
// As chaves NUNCA ficam neste ficheiro (iam para o GitHub). Sao lidas de
// "local.properties", que esta no .gitignore. Copia "local.properties.example"
// para "local.properties" e preenche os teus valores.
// ---------------------------------------------------------------------------
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

fun apiKey(name: String, fallback: String): String {
    val value = localProperties.getProperty(name)
    return if (value == null || value.trim().isEmpty()) fallback else value.trim()
}

fun flag(name: String, fallback: Boolean): Boolean {
    val value = localProperties.getProperty(name)
    return if (value == null || value.trim().isEmpty()) fallback else value.trim().toBoolean()
}

val restdbUrl = apiKey("RESTDB_URL", "https://triptrail-xxxxxx.restdb.io/")
val restdbKey = apiKey("RESTDB_KEY", "X")
val positionstackUrl = apiKey("POSITIONSTACK_URL", "http://api.positionstack.com/")
val positionstackKey = apiKey("POSITIONSTACK_KEY", "X")
val currencyUrl = apiKey("CURRENCY_URL", "http://data.fixer.io/api/")
val currencyKey = apiKey("CURRENCY_KEY", "X")
val countrylayerUrl = apiKey("COUNTRYLAYER_URL", "http://api.countrylayer.com/v2/")
val countrylayerKey = apiKey("COUNTRYLAYER_KEY", "X")
val useMockData = flag("USE_MOCK_DATA", true)
val useMockEnrichment = flag("USE_MOCK_ENRICHMENT", true)

android {
    namespace = "com.triptrail"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.triptrail"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // ---- REST / APIs (seccao 6 da proposta) ----
        buildConfigField("String", "RESTDB_URL", "\"$restdbUrl\"")
        buildConfigField("String", "RESTDB_KEY", "\"$restdbKey\"")
        buildConfigField("String", "POSITIONSTACK_URL", "\"$positionstackUrl\"")
        buildConfigField("String", "POSITIONSTACK_KEY", "\"$positionstackKey\"")
        buildConfigField("String", "CURRENCY_URL", "\"$currencyUrl\"")
        buildConfigField("String", "CURRENCY_KEY", "\"$currencyKey\"")
        buildConfigField("String", "COUNTRYLAYER_URL", "\"$countrylayerUrl\"")
        buildConfigField("String", "COUNTRYLAYER_KEY", "\"$countrylayerKey\"")

        // ---- Interruptores de desenvolvimento (seccao 6.6 "Como poupar quota") ----
        // Com as chaves ainda a "X" a app corre contra dados simulados, por isso
        // executa sempre (necessario para o checkpoint).
        buildConfigField("boolean", "USE_MOCK_DATA", "$useMockData")
        buildConfigField("boolean", "USE_MOCK_ENRICHMENT", "$useMockEnrichment")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    // ---- Android base ----
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.fragment:fragment:1.8.2")
    implementation("androidx.core:core:1.13.1")
    // Leitura da rotacao EXIF das fotografias (ImageUtils)
    implementation("androidx.exifinterface:exifinterface:1.3.7")

    // ---- REST: Retrofit + OkHttp + Gson (seccao 5.1) ----
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // ---- GPS: FusedLocationProvider (seccao 7.1) ----
    implementation("com.google.android.gms:play-services-location:21.3.0")

    // ---- Camera: CameraX (seccao 7.2) ----
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // ---- Mapa: osmdroid / OpenStreetMap, sem chave de API (seccao 5.1) ----
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    // ---- Imagens: Glide, carrega as fotografias guardadas no RestDB ----
    implementation("com.github.bumptech.glide:glide:4.16.0")

    // ---- Testes ----
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
