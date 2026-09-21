# SYNCAPSO Android App + Real AI

This package contains:
- `app/`: Android WebView app wrapping the SYNCAPSO UI
- `backend/`: secure Node/Express AI backend
- `.github/workflows/build-apk.yml`: cloud APK build workflow

IMPORTANT:
1. Deploy `backend/` to an HTTPS Node host.
2. Set `OPENAI_API_KEY` on that host.
3. In `app/src/main/java/com/syncapso/app/MainActivity.kt`, replace
   `https://YOUR-SYNCAPSO-BACKEND.example.com` with your deployed backend URL.
4. Push the project to GitHub and run the "Build SYNCAPSO APK" workflow.
5. Download the generated APK from GitHub Actions and install it on Android.

The OpenAI key is never placed in the APK.

Current AI model: `gpt-5.6-luna`.
