import { requireNativeViewManager } from "expo-modules-core";
import * as React from "react";

import { Coordinate } from "./ExpoGoogleMaps.types";

export type Marker = {
  /** The position of the marker */
  position: Coordinate;
  /** The title of the marker, displayed on the pop up */
  title?: string;
  /** An id to identify the marker on subsequent renders */
  key: string;
};

export type MarkerViewProps = {
  marker: Marker;
  /** Called when the marker is pressed */
  onMarkerPress?: () => void;
  children?: React.ReactElement;
};

const NativeView: React.ComponentType<MarkerViewProps> =
  requireNativeViewManager("ExpoGoogleMapsMarker");

export function MarkerView(props: MarkerViewProps) {
  return <NativeView {...props} />;
}
