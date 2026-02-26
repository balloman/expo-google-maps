# Example App

This is the example application demonstrating the usage of `@balloman/expo-google-maps`.

## Prerequisites

- [Bun](https://bun.sh/) installed
- iOS: Xcode 16+ and iOS 16+ simulator or device
- Android: Android Studio and Android SDK
- Google Maps API keys for both platforms

## Setup

1. Install dependencies:

```bash
bun install
```

2. Configure environment variables:

Create a `.env` file in the root of this directory with your Google Maps API key:

```env
EXPO_PUBLIC_API_KEY=your_google_maps_api_key_here
```

## Running the App

### iOS

```bash
bun ios
```

This will:
- Install CocoaPods dependencies
- Build the iOS project
- Launch the app in the iOS Simulator

### Android

```bash
bun android
```

This will:
- Build the Android project
- Launch the app in the Android Emulator

## Available Scripts

- `bun start` - Start the Expo development server
- `bun ios` - Run on iOS device/simulator
- `bun android` - Run on Android device/emulator

## Project Structure

- `app/` - Main application code using Expo Router
- `app/index.tsx` - Main map demonstration screen

## Features Demonstrated

The example app showcases:
- Basic MapView with camera control
- Polygon rendering
- Custom map styling (POI labels hidden)
- Button-triggered camera animations
- State-driven polygon updates

## Troubleshooting

### Map Not Displaying

Verify that:
- `EXPO_PUBLIC_API_KEY` is set in your `.env` file
- The API key has Google Maps SDK enabled for your platform
- For iOS: The API key is set via `setApiKey()` in the code
- For Android: The API key is configured in `app.json` via the plugin
