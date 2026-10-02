#!/bin/bash
set -e

echo "=========================================="
echo "      VendorOS APK Build Script          "
echo "=========================================="

# 1. If web/Vite React project with Capacitor exists:
if [ -f "package.json" ]; then
    echo "Node.js package.json detected. Checking dependencies..."
    if command -v npm &> /dev/null; then
        npm install
        if grep -q '"build":' package.json; then
            npm run build
        fi
        if [ -f "capacitor.config.ts" ] || [ -f "capacitor.config.json" ]; then
            npx cap sync android || true
        fi
    fi
fi

# 2. Determine target Android directory
if [ -d "android" ] && ([ -f "android/build.gradle" ] || [ -f "android/build.gradle.kts" ]); then
    TARGET_DIR="android"
else
    TARGET_DIR="."
fi

if [ "$TARGET_DIR" != "." ]; then
    cd "$TARGET_DIR"
fi

# 3. Build APK with Gradle Wrapper or system Gradle
if [ -f "./gradlew" ]; then
    chmod +x ./gradlew
    ./gradlew assembleDebug
    echo "=========================================="
    echo "APK built successfully at: app/build/outputs/apk/debug/app-debug.apk"
    echo "=========================================="
elif command -v gradle &> /dev/null; then
    gradle :app:assembleDebug
    echo "=========================================="
    echo "APK built successfully at: app/build/outputs/apk/debug/app-debug.apk"
    echo "=========================================="
else
    echo "Error: Neither ./gradlew nor gradle was found on PATH."
    exit 1
fi
