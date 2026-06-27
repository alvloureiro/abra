# Release builds

Abra ships to Google Play as a signed Android App Bundle (AAB). Release signing credentials are never committed to the repository.

## Generate a upload keystore (one-time)

```bash
keytool -genkeypair \
  -v \
  -storetype PKCS12 \
  -keystore abra-upload.keystore \
  -alias abra \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

Store the keystore file outside the repo (or in a path listed in `.gitignore`). Back it up securely; losing it prevents publishing updates with the same Play Console app.

## Local release signing

Add the following to `local.properties` (this file is gitignored):

```properties
RELEASE_STORE_FILE=/absolute/path/to/abra-upload.keystore
RELEASE_STORE_PASSWORD=your-store-password
RELEASE_KEY_ALIAS=abra
RELEASE_KEY_PASSWORD=your-key-password
```

Build a signed bundle:

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

If signing properties are missing, `bundleRelease` still runs but produces an unsigned bundle. Configure credentials before uploading to Play Console.

## CI signing

GitHub Actions release workflow uses the same property names via environment variables. See `.github/workflows/release.yml` and repository secrets:

- `KEYSTORE_BASE64` — base64-encoded keystore file
- `KEYSTORE_PASSWORD` — store password
- `KEY_ALIAS` — key alias
- `KEY_PASSWORD` — key password

## Play App Signing

Enable Play App Signing in Google Play Console on first upload. Google holds the app signing key; your upload keystore is used only to sign bundles you upload.

## Versioning

Before each Play Console upload, bump in `app/build.gradle.kts`:

- `versionCode` — monotonically increasing integer
- `versionName` — user-visible version string

## Pre-upload checks

1. Run `./gradlew qualityGate`
2. Build `./gradlew bundleRelease`
3. Verify the AAB with Android Studio APK Analyzer (16 KB page alignment if native libraries are present)
