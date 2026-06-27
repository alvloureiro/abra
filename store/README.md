# Store listing assets

Play Console requires assets beyond what ships in the APK. This folder holds listing copy and graphics for submission.

## Included

| Asset | Path | Play requirement |
|-------|------|------------------|
| App title | `listing/title.txt` | 30 chars max |
| Short description | `listing/short_description.txt` | 80 chars max |
| Full description | `listing/full_description.txt` | 4000 chars max |
| Feature graphic | `graphics/feature_graphic.svg` | 1024×500 PNG/JPEG for upload |
| High-res icon | `graphics/icon_512.svg` | 512×512 PNG for upload |

## Screenshots (capture before submission)

Place phone screenshots in `screenshots/phone/`:

1. **Library** — empty or populated library with import affordance
2. **Reader** — active playback with title and controls
3. **Settings** — voice selection
4. **Mini player** (optional) — bottom bar while on Library tab

Recommended size: 1080×1920 or 1440×2560 PNG.

Capture on a device or emulator:

```bash
adb exec-out screencap -p > store/screenshots/phone/01_library.png
```

## Exporting graphics

Convert SVG to PNG for Play Console upload (requires ImageMagick, Inkscape, or Android Studio):

```bash
# Feature graphic
convert -background none -resize 1024x500 store/graphics/feature_graphic.svg store/graphics/feature_graphic.png

# High-res icon
convert -background none -resize 512x512 store/graphics/icon_512.svg store/graphics/icon_512.png
```

## Category

Books & Reference (primary) or Education.

## Privacy policy URL

`https://github.com/alvloureiro/abra/blob/main/docs/privacy-policy.md`
