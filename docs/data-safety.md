# Google Play Data Safety — Abra

Internal reference for completing the Play Console Data safety form. Update if app behavior changes.

## Does your app collect or share user data?

**Answer: No** — Abra does not transmit user data off the device. Declare that the app does not collect or share data required to be disclosed, while accurately describing on-device processing below if the form asks about data types handled locally.

## Data types handled on-device only

| Data type | Collected? | Shared? | Purpose | Required? | Encrypted in transit | Encrypted at rest |
|-----------|------------|---------|---------|-----------|----------------------|-------------------|
| Files and docs (PDF metadata & content) | Processed locally | No | App functionality (import & listen) | Yes (user-initiated import) | N/A (no transmission) | OS device encryption |
| App activity (listening progress, bookmarks/position) | Stored locally | No | App functionality | Yes | N/A | OS device encryption |
| App info and performance | No SDK collection | No | — | — | — | — |
| Device or other IDs | No | No | — | — | — | — |

## Data handling practices

- **Ephemeral processing:** PDF text is extracted in memory during import; extracted text is persisted locally for playback.
- **User control:** Users choose which PDFs to import via the system picker.
- **Deletion:** Uninstall removes all app-local data.
- **No account:** No registration or authentication.

## Permissions

The app declares Android permissions required for background listening:

- `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_MEDIA_PLAYBACK` — keep TTS playback active with a media-style notification while you listen
- `POST_NOTIFICATIONS` — show playback controls in the notification shade (Android 13+; requested at runtime)

File import still uses Storage Access Framework with no broad storage permissions.

## Third-party SDKs

No analytics, ads, or crash reporting SDKs. Dependencies (AndroidX, PDFBox, Hilt) are compile-time libraries; they do not phone home.

## System TTS note

If the form asks about data sent to third parties: optional network TTS voices are requested by the **Android OS TTS engine**, not directly by Abra. Abra sends text to the system TTS API only. Disclose per Google's guidance if required; Abra itself does not implement network calls.

## Security practices

- Data is not transmitted by the app.
- `android:allowBackup="false"` — app data excluded from backup.
- `usesCleartextTraffic="false"`.

## Privacy policy URL

`https://github.com/alvloureiro/abra/blob/main/docs/privacy-policy.md`

## Ads

The app does not contain ads. Declare **No** for ads.

## Target audience

General audience / all ages for the app mechanism. Users may import documents with any content; that content is user-provided and not hosted by Abra.
