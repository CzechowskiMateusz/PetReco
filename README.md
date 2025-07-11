# 🐾 PetReco

Aplikacja mobilna (Android) pozwalająca na rozpoznawanie i rekomendację informacji o zwierzętach na podstawie zdjęć, zintegrowana z Firebase i Facebook SDK.

---

## 🔍 Spis treści

- [Funkcjonalności](#funkcjonalności)
- [Technologie](#technologie)
- [Instalacja i konfiguracja](#instalacja-i-konfiguracja)
  - [1. Skopiuj repo](#1-skopiuj-repo)
  - [2. Utwórz `local.properties`](#2-utwórz-localproperties)
  - [3. Skonfiguruj Firebase](#3-skonfiguruj-firebase)
  - [4. Zgaś Google i Facebook sekrety](#4-zgaś-google-i-facebook-sekrety)
  - [5. Build i uruchomienie](#5-build-i-uruchomienie)
- [Użycie sekretów w kodzie](#użycie-sekretów-w-kodzie)
- [CI/CD (opcjonalnie)](#cicd-opcjonalnie)
- [Kontrybutorzy](#kontrybutorzy)
- [Licencja](#licencja)

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

## Instalacja i konfiguracja

### 1. Skopiuj repo

```bash
git clone https://github.com/CzechowskiMateusz/PetReco.git
cd PetReco
