# Release Signing & Keystore Setup — Lumera

This guide explains how the release keystore is managed and how to configure GitHub Actions secrets for signed release builds.

---

## 🔒 1. Security First

Never commit keystore files or passwords to GitHub. The following patterns are ignored in `.gitignore`:
- `*.jks`
- `*.keystore`
- `keystore.properties`

The Lumera keystore lives **outside the repo**, at:

```
C:\Users\shadd\keystores\Lumera-release.jks
C:\Users\shadd\keystores\Lumera-keystore.properties
```

**Back these up.** If the keystore is lost, the app can never be updated under the same signature.

---

## 🔑 2. Keystore Details

| Property | Value |
| -------- | ----- |
| Store file | `C:\Users\shadd\keystores\Lumera-release.jks` |
| Alias | `lumera` |
| Algorithm | RSA 2048 |
| Validity | 30 years (10,950 days) |
| Certificate | `CN=alzimer ahmed, O=Lumera, EMAILADDRESS=alzimerahmed84@gmail.com` |

Passwords are stored in `Lumera-keystore.properties` alongside the keystore.

---

## 📄 3. Generating Base64 for CI

```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("C:\Users\shadd\keystores\Lumera-release.jks")) | Set-Clipboard
```

---

## ⚙️ 4. Add GitHub Secrets

1. Open your repository on GitHub.
2. Go to **Settings** -> **Secrets and variables** -> **Actions**.
3. Add:

| Secret Name | Value |
| ----------- | ----- |
| `ANDROID_KEYSTORE_BASE64` | Base64 string from step 3 |
| `ANDROID_KEYSTORE_PASSWORD` | Store password from `Lumera-keystore.properties` |
| `KEY_ALIAS` | `lumera` |
| `KEY_PASSWORD` | Key password from `Lumera-keystore.properties` |

---

## 🚀 5. Triggering a Signed Release Build

- **Automated Tag Release**: Push a Git tag starting with `v` (e.g., `git tag v1.0.0 && git push origin v1.0.0`).
- **Manual Build**: Go to **Actions** -> **Build Android APK** -> **Run workflow**.
