# NoctaFlix TV – Android TV App

Application Android TV encapsulant **https://noctaflix.lol** dans un WebView plein écran optimisé TV.

---

## 📋 Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Android Studio | Hedgehog (2023.1) ou plus récent |
| JDK | 17 |
| Android SDK | API 34 (compileSdk) |
| minSdk | 21 (Android 5.0 – couvre la quasi-totalité des TV) |

---

## 🚀 Build & Installation

### Via Android Studio
1. Ouvrir Android Studio → **Open** → sélectionner ce dossier `NoctaflixTV/`
2. Laisser Gradle synchroniser
3. **Build → Build APK(s)** ou **Run** (émulateur Android TV recommandé : API 29+)
4. L'APK se trouve dans `app/build/outputs/apk/release/`

### Via ligne de commande
```bash
# Depuis la racine du projet
./gradlew assembleRelease
# APK → app/build/outputs/apk/release/app-release-unsigned.apk
```

### Signer l'APK (production)
```bash
keytool -genkey -v -keystore noctaflix.keystore -alias noctaflix \
        -keyalg RSA -keysize 2048 -validity 10000

./gradlew assembleRelease

jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
          -keystore noctaflix.keystore app-release-unsigned.apk noctaflix

zipalign -v 4 app-release-unsigned.apk NoctaflixTV.apk
```

---

## 📺 Installation sur la TV

### Méthode 1 – ADB (recommandé)
```bash
# Activer le mode développeur sur la TV
# Paramètres → À propos → Build → cliquer 7 fois
# Activer Débogage ADB

adb connect <IP_DE_LA_TV>:5555
adb install NoctaflixTV.apk
```

### Méthode 2 – Clé USB
1. Copier l'APK sur une clé USB
2. Utiliser un gestionnaire de fichiers (ex: **FX File Explorer**, **Send Files to TV**)
3. Ouvrir et installer l'APK

### Méthode 3 – Send Files to TV
- PC → installer **Send Files to TV** sur PC et TV
- Envoyer l'APK directement

---

## 🎮 Télécommande – Raccourcis

| Touche | Action |
|--------|--------|
| **Retour** | Page précédente |
| **OK / Centre** | Clic / Sélection |
| **Flèches** | Navigation |
| **⏩ Avance rapide** | +10 sec vidéo |
| **⏪ Retour rapide** | -10 sec vidéo |
| **Menu** | Retour accueil NoctaFlix |

---

## 🏗️ Structure du projet

```
NoctaflixTV/
├── app/
│   ├── src/main/
│   │   ├── java/com/noctaflix/tv/
│   │   │   └── MainActivity.java      ← WebView + contrôles TV
│   │   ├── res/
│   │   │   ├── drawable/tv_banner.png ← Banner launcher TV (320×180)
│   │   │   ├── mipmap-*/ic_launcher   ← Icônes app
│   │   │   ├── values/strings.xml
│   │   │   ├── values/styles.xml      ← Thème fullscreen sombre
│   │   │   └── xml/network_security_config.xml
│   │   └── AndroidManifest.xml
│   ├── build.gradle
│   └── proguard-rules.pro
├── build.gradle
├── settings.gradle
└── gradle.properties
```

---

## ⚙️ Personnalisation

- **URL de départ** : modifier `HOME_URL` dans `MainActivity.java`
- **Couleur accent** : chercher `0xFFE50914` (rouge) et remplacer
- **User-Agent** : modifier la chaîne dans `configureWebView()`
- **minSdk** : réduire à `17` pour une compatibilité maximale
