# Privacy Policy for Abra

**Last updated:** June 27, 2026

**Contact:** alvloureiro@users.noreply.github.com (via [GitHub Issues](https://github.com/alvloureiro/abra/issues))

Abra ("the app") is a local ebook listener for Android. This policy describes how the app handles information.

## Summary

Abra does **not** collect, transmit, or sell your personal data. All core functionality runs on your device. The app does not include analytics, advertising, or account registration.

## Information the app accesses

### PDF files you import

You choose PDF files through the Android system file picker (Storage Access Framework). The app reads only the files you select to extract text for listening. Imported files remain under your control on your device or cloud storage provider.

### Listening progress and library metadata

The app stores ebook titles, import dates, reading/listening position, and extracted text segments locally in an on-device database (Room). This data is not transmitted to us or third parties.

### Voice settings

Your preferred text-to-speech voice and related settings are stored locally using Android DataStore on your device.

## Information we do not collect

- No account or profile information
- No location data
- No contacts, photos, or other media beyond PDFs you explicitly import
- No usage analytics or crash reporting SDKs in the app
- No data sold to third parties

## Text-to-speech and the operating system

Abra uses the Android system Text-to-Speech (TTS) engine. Some TTS voices may require network access provided by your device manufacturer or a third-party TTS provider. That communication is between your device and the TTS provider, not Abra. Review your device TTS settings for details.

## Data storage and security

All app data is stored locally on your device. The app disables Android backup for its data (`allowBackup=false`). Data is protected by your device's operating-system encryption when enabled.

## Data retention and deletion

Uninstalling Abra removes the app's local database and preferences from your device. PDF files you imported from external storage are not deleted unless you remove them separately.

## Children's privacy

Abra does not knowingly collect personal information from anyone. The app has no age gate because it does not collect data.

## Changes to this policy

We may update this policy before new releases. The "Last updated" date will change accordingly. Continued use of the app after changes constitutes acceptance of the updated policy.

## Open source

Abra is open source. You can review how the app handles data in the [source repository](https://github.com/alvloureiro/abra).

**Hosted URL for Play Console:** `https://github.com/alvloureiro/abra/blob/main/docs/privacy-policy.md`
