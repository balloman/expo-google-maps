import { NativeModule, requireNativeModule } from 'expo';
import type { ExpoGoogleMapsModuleEvents } from './ExpoGoogleMaps.types';

declare class ExpoGoogleMapsModule extends NativeModule<ExpoGoogleMapsModuleEvents> {
	setApiKey(apiKey: string): void;
}

// It loads the native module object from the JSI or falls back to
// the bridge module (from NativeModulesProxy) if the remote debugger is on.
export default requireNativeModule<ExpoGoogleMapsModule>('ExpoGoogleMaps');
