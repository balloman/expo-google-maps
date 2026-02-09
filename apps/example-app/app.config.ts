import type { ExpoConfig } from 'expo/config';

export default (): ExpoConfig => ({
	name: 'example-app',
	slug: 'example-app',
	version: '1.0.0',
	orientation: 'portrait',
	icon: './assets/images/icon.png',
	scheme: 'exampleapp',
	userInterfaceStyle: 'automatic',
	newArchEnabled: true,
	ios: {
		supportsTablet: true,
		bundleIdentifier: 'com.balloman.exampleapp',
	},
	android: {
		adaptiveIcon: {
			backgroundColor: '#E6F4FE',
			foregroundImage: './assets/images/android-icon-foreground.png',
			backgroundImage: './assets/images/android-icon-background.png',
			monochromeImage: './assets/images/android-icon-monochrome.png',
		},
		edgeToEdgeEnabled: true,
		predictiveBackGestureEnabled: false,
		package: 'com.balloman.exampleapp',
	},
	web: {
		output: 'static',
		favicon: './assets/images/favicon.png',
	},
	plugins: [
		'expo-router',
		[
			'expo-splash-screen',
			{
				image: './assets/images/splash-icon.png',
				imageWidth: 200,
				resizeMode: 'contain',
				backgroundColor: '#ffffff',
				dark: {
					backgroundColor: '#000000',
				},
			},
		],
		[
			'@balloman/expo-google-maps',
			{
				androidApiKey: process.env.EXPO_PUBLIC_API_KEY,
			},
		],
		[
			'expo-build-properties',
			{
				ios: {
					deploymentTarget: '16.0',
				},
				android: {
					targetSdkVersion: 33,
					minSdkVersion: 33,
				},
			},
		],
		'expo-location',
	],
	experiments: {
		typedRoutes: true,
		reactCompiler: true,
	},
});
