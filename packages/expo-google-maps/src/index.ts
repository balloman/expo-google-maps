import ExpoGoogleMapsModule from './ExpoGoogleMapsModule';

ExpoGoogleMapsModule.addListener('log', (event) => {
	console.log(event);
});

export function setApiKey(key: string) {
	ExpoGoogleMapsModule.setApiKey(key);
}

export * from './ExpoGoogleMaps.types';
export * from './MapView';
export * from './MarkerView';
