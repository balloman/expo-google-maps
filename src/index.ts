import { EventEmitter, NativeModulesProxy } from "expo-modules-core";

import ExpoGoogleMapsModule from "./ExpoGoogleMapsModule";

const emitter = new EventEmitter(
  ExpoGoogleMapsModule ?? NativeModulesProxy.ExpoGoogleMaps,
);

emitter.addListener("log", event => {
  console.log(event);
});

export function setApiKey(key: string) {
  ExpoGoogleMapsModule.setApiKey(key);
}

export * from "./MapView";
export * from "./MarkerView";
export * from "./ExpoGoogleMaps.types";
