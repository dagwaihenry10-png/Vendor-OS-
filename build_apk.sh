#!/bin/bash
set -e

echo "=========================================="
echo "      VendorOS APK Build Script          "
echo "=========================================="

if command -v flutter &> /dev/null; then
    echo "Running Flutter build..."
    flutter pub get
    flutter build apk --release --no-tree-shake-icons
    echo "APK built at build/app/outputs/flutter-apk/app-release.apk"
else
    echo "Flutter CLI not found on local PATH. Running Gradle APK build..."
    if command -v gradle &> /dev/null; then
        gradle :app:assembleDebug
        echo "APK built at app/build/outputs/apk/debug/app-debug.apk"
    elif [ -f "./gradlew" ]; then
        ./gradlew :app:assembleDebug
        echo "APK built at app/build/outputs/apk/debug/app-debug.apk"
    fi
fi
