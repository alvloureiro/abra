# Play Console closed testing submission checklist

Use this checklist when uploading Abra to the **Closed testing** track. Console steps are manual; this document tracks repo readiness and Play Console tasks.

## Pre-upload (repository)

- [ ] `./gradlew qualityGate` passes locally or in CI
- [ ] Upload keystore configured (`docs/RELEASE.md`)
- [ ] `./gradlew bundleRelease` produces signed `app/build/outputs/bundle/release/app-release.aab`
- [ ] Release AAB inspected in Android Studio APK Analyzer (16 KB alignment if native `.so` files present)
- [ ] Phone screenshots captured into `store/screenshots/phone/` (min 2)
- [ ] Export `store/graphics/feature_graphic.png` and `store/graphics/icon_512.png` from SVG templates

## Play Console — create app

- [ ] Create app with package name `com.abra`
- [ ] Enable **Play App Signing** (recommended: Google manages app signing key)
- [ ] Upload first AAB to **Closed testing** track

## Store listing

- [ ] **App name:** Abra (`store/listing/title.txt`)
- [ ] **Short description:** `store/listing/short_description.txt`
- [ ] **Full description:** `store/listing/full_description.txt`
- [ ] **App icon:** 512×512 PNG
- [ ] **Feature graphic:** 1024×500 PNG
- [ ] **Phone screenshots:** at least 2
- [ ] **Category:** Books & Reference (or Education)
- [ ] **Contact email** set in Console

## Policy and compliance

- [ ] **Privacy policy URL:**  
  `https://github.com/alvloureiro/abra/blob/main/docs/privacy-policy.md`
- [ ] **Data safety form:** answers in `docs/data-safety.md`
- [ ] **Ads:** No, app does not contain ads
- [ ] **Content rating:** use `store/content-rating-notes.md`
- [ ] **Target audience:** General / not designed only for children (adjust per questionnaire)
- [ ] **Government apps:** No
- [ ] **Financial features:** None

## Closed testing requirements (new personal accounts)

Google may require before production access:

- [ ] **14 days** of closed testing
- [ ] **12+ testers** opted in to closed test
- [ ] Testers install from Play Store closed-test link (not sideload)

## Post-upload verification

- [ ] Install from closed-test link on physical device
- [ ] Import a text-based PDF via SAF
- [ ] Start playback; verify notification controls (pause / play / stop)
- [ ] Background the app; confirm playback continues
- [ ] Settings → About → Privacy policy and licenses open correctly
- [ ] Grant notification permission on Android 13+ when prompted

## Versioning for each upload

Update `app/build.gradle.kts` before each new AAB:

```kotlin
versionCode = 2   // increment every upload
versionName = "1.0.1"
```

Tag releases for CI builds: `git tag v1.0.1 && git push origin v1.0.1` (triggers `.github/workflows/release.yml` when secrets are configured).

## Promote to production

After closed testing criteria are met:

1. Review Android Vitals and tester feedback
2. Promote release from Closed testing → Production
3. Complete any remaining Console warnings
