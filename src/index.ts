/* eslint-disable @typescript-eslint/no-unsafe-member-access */
/* eslint-disable @typescript-eslint/no-unsafe-call */
/* eslint-disable @typescript-eslint/no-unsafe-argument */
import { EventEmitter } from "expo-modules-core";

import ExpoGoogleMapsModule from "./ExpoGoogleMapsModule";

const emitter = new EventEmitter(
  ExpoGoogleMapsModule,
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
