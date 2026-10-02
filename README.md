# VendorOS — Auto Reply + Trust + Handwork (3 Apps in 1)

**VendorOS** is the complete Nigerian business super-app designed for online merchants and artisan craft masters:
1. **Vendor Mode**: 24/7 WhatsApp & Instagram auto-reply engine with working hours and keyword trigger rules.
2. **Trust & Proofs**: Anti-scam verified order delivery ledger with dynamic 2D QR badges and customer confirmation.
3. **HandworkNG**: 36 States + FCT skilled artisan directory, interactive radar map, apprentice inquiries, and workshop registration.
4. **Secure OPay Gateway**: Encrypted payment card with strictly masked account (`7081****44`) and server-side verification.

---

# 🚀 HOW TO GET THE ANDROID APK

### **Option 1: Direct Build via GitHub Actions (Recommended)**
1. In Google AI Studio, click **Settings (gear icon) > Export > Push to GitHub** (or download as ZIP and push to GitHub).
2. The included `.github/workflows/build-apk.yml` workflow will automatically run on your GitHub repo.
3. Once completed, navigate to the **Actions** tab on your GitHub repository, click on the latest build, and download `VendorOS-APK`!

---

### **Option 2: Run Local Build Script**
1. In Google AI Studio, click **Download Code as ZIP**.
2. Extract the ZIP on your computer.
3. Open terminal/command prompt in the project root and run:
   ```bash
   chmod +x build_apk.sh
   ./build_apk.sh
   ```
   *or*
   ```bash
   flutter build apk --release --no-tree-shake-icons
   ```
   *or with Gradle:*
   ```bash
   ./gradlew assembleDebug
   ```
4. Your APK will be generated ready to install on any Android phone!

---

## 🔒 Security Compliance
- The OPay account is strictly displayed as **`7081****44`** in the application interface.
- Real account information is protected server-side via the `api/` PHP gateway scripts (`api/config.php`, `api/pay.php`, `api/sync.php`, `api/verify_payment.php`).
