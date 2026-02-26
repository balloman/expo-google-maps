import ExpoGoogleMapsModule from './ExpoGoogleMapsModule';

/**
 * Sets the api key for the application instance
 * @param key The key to set
 * @platform ios
 */
export function setApiKey(key: string) {
	ExpoGoogleMapsModule.setApiKey(key);
}

export * from './ExpoGoogleMaps.types';
export * from './MapView';
export * from './MarkerView';
