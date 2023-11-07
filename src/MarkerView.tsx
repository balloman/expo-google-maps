import { requireNativeViewManager } from "expo-modules-core";
import * as React from "react";
import { View } from "react-native";

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
  /** Controls whether the icon for this marker should be redrawn every frame
   * @default false
   */
  tracksViewChanges?: boolean;
  children?: React.ReactElement;
};

const NativeView: React.ComponentType<MarkerViewProps> =
  requireNativeViewManager("ExpoGoogleMapsMarker");

export function MarkerView(props: MarkerViewProps) {
  return (
    <NativeView {...props}>
      {props.children && ( // Due to some weirdness with the native view, we need to wrap the children in an absolute view
        <View style={{ position: "absolute" }}>{props.children}</View>
      )}
    </NativeView>
  );
}
