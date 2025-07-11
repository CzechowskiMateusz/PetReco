# 🐾 PetReco

Aplikacja mobilna (Android) pozwalająca na rozpoznawanie i rekomendację informacji o zwierzętach na podstawie zdjęć, zintegrowana z Firebase i Facebook SDK.

---

## 🔍 Spis treści

- [Funkcjonalności](#funkcjonalności)
- [Technologie](#technologie)
- [Wygląd Aplikacji](#wygląd-aplikacji)
- [Instalacja i konfiguracja](#instalacja-i-konfiguracja)

---

## Funkcjonalności

- Rozpoznawanie zwierząt za pomocą TensorFlow Lite
- Logowanie/uwierzytelnianie użytkownika przez Firebase Auth
- Integracja z Firebase Analytics i Crashlytics
- Logowanie Facebook SDK
- Zarządzanie stanem w Compose + Room
- Pozwala wybierać zdjęcie zwierzęcia i wyświetlać szczegóły

---

## Technologie

- Android (minSdk 25+, Kotlin + Compose)
- TensorFlow Lite
- Firebase: Analytics, Auth, Crashlytics, Storage
- Facebook Android SDK
- Room Database
- Retrofit + OkHttp
- Coil (grafika)

---

## Wygląd Aplikacji

### Ekran logowania
Ekran logowania umożliwia szybkie i bezpieczne zalogowanie się do aplikacji.

### Ekran początkowy
Główny ekran aplikacji, z dostępem do wszystkich funkcji i menu

### Rozpoznawanie zwierząt
Proces rozpoznawania zwierząt z efektowną animacją konfetti na zakończenie.


<div align="center">
<img width="250" alt="image" src="https://github.com/user-attachments/assets/4ecfa79a-acc8-477f-aeb9-41b93df6a385" />
<img width="250" alt="image" src="https://github.com/user-attachments/assets/4d14e32c-8e6c-4225-8920-e927236b3bb4" />
<img width="250" alt="image" src="https://github.com/user-attachments/assets/46008c99-cd3c-4dd8-9701-20492ad8a4a9" />
</div>

---

## Instalacja i konfiguracja

### 1. Skopiuj repo

```bash
git clone https://github.com/CzechowskiMateusz/PetReco.git
cd PetReco
