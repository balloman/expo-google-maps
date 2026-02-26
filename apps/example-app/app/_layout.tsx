import { Stack } from 'expo-router';

/**
 * Provides the app's root navigation stack with headers hidden.
 *
 * @returns The Stack navigator component configured with headerShown set to false.
 */
export default function RootLayout() {
	return <Stack screenOptions={{ headerShown: false }} />;
}
