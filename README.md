# CarMarketMobilee

Jetpack Compose ile geliştirilmiş, **araç listeleme / detay / favoriler / sepet** akışlarını içeren Android uygulaması. Backend ile **JWT tabanlı kimlik doğrulama** yapar ve verileri **Retrofit** üzerinden çeker. Ana liste ekranında **Paging 3** kullanır.

## Özellikler

- **Auth**
  - Giriş / Kayıt
  - JWT token kontrolü (token yoksa/expired ise login’e yönlendirme)
- **Araçlar**
  - Araç listeleme (Paging 3)
  - Araç detay sayfası
  - Araç ekleme (fotoğraf ile `multipart`)
  - Araç güncelleme
  - Araç silme
- **Favoriler**
  - Favorileri listeleme / ekleme / çıkarma
- **Sepet**
  - Sepeti görüntüleme
  - Ürün ekleme/çıkarma
  - Adet güncelleme
  - Sepeti temizleme
- **Profil**
  - Profil verisini backend’den çekme
- **Admin Panel**
  - Kullanıcıları listeleme / silme
  - Kullanıcı detayları (cart + favs)
  - Admin ekleme

## Tech Stack

- **Kotlin**, **Android Gradle Plugin**
- **Jetpack Compose** (Material 3)
- **Navigation Compose**
- **Retrofit2 + Gson**
- **OkHttp Interceptor** (Authorization header)
- **Paging 3**
- **Coil 3** (Compose image loading)

## Gereksinimler

- **Android Studio** (öneri: güncel stable)
- **JDK 11** (projede `jvmTarget = 11`)
- Android cihaz veya emulator
- Çalışır durumda backend API

## Kurulum

1. Repo’yu klonla:
   ```bash
   git clone <REPO_URL>
   cd CarMarketMobilee
   ```
2. Android Studio ile projeyi aç.

  Backend Base URL ayarı
  
3. Base URL şu dosyada sabit:
    app/src/main/java/com/emreyildirim/carmarketmobilee/data/RetrofitInstance.kt
    private const val BASE_URL = "https://sortably-nonaffiliating-my.ngrok-free.dev/"
    Kendi backend adresine göre değiştirmen gerekir (örn. http://10.0.2.2:8080/).
4. Uygulamayı çalıştır:
  
    app konfigürasyonu ile Run.
   
