# Cinejoy TV — Android TV WebView project

Target: Xiaomi Mi Box 4, Android 9.

Home URL: https://cinejoy.to

This is a generic Android TV WebView wrapper. It does not download, decrypt,
bypass DRM, or extract media. Playback depends on the website/player.

## Build without Android Studio

Use the included GitHub Actions workflow:

1. Create a GitHub account or sign in.
2. Create a new repository (for example `CinejoyTV`).
3. Upload the contents of this folder to the repository and commit to `main`.
4. Open **Actions**.
5. Choose **Build Cinejoy TV APK**.
6. Click **Run workflow**.
7. When complete, open the run and download **CinejoyTV-debug-apk**.
8. Extract the downloaded artifact; the APK is `app-debug.apk`.

No Android Studio or emulator is required.
