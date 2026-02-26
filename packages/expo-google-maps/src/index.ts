import ExpoGoogleMapsModule from './ExpoGoogleMapsModule';

/**
 * Set the Google Maps API key used by the native Android module for the application instance.
 *
 * @param key - The Google Maps API key to apply
 * @platform android
 */
export function setApiKey(key: string) {
	ExpoGoogleMapsModule.setApiKey(key);
}

export * from './ExpoGoogleMaps.types';
export * from './MapView';
export * from './MarkerView';
