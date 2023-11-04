import { requireNativeViewManager } from "expo-modules-core";
import * as React from "react";
import { ViewProps } from "react-native";

import {
  AnimationOptions,
  Camera,
  FitToBoundsOptions,
  Polygon,
} from "./ExpoGoogleMaps.types";

export type MapViewProps = {
  /** A prop to modify the camera */
  camera: Camera;
  /** Any polygons to display on the map */
  polygons?: Polygon[];
  /** A ref to the imperative functions of the map */
  mapRef?: React.Ref<MapFunctions>;
  /** A json string of the style to apply to the map, if needed. See here: https://mapstyle.withgoogle.com/ */
  styleJson?: string;
  /** A callback for when the map is idle after being moved */
  onMapIdle?: (event: {
    /** The position of the camera */
    cameraPosition: Camera;
  }) => void;
} & ViewProps;

export type MapFunctions = {
  /**
   * Animates the camera to the given position @see {@link Camera}
   * @param camera A camera object to animate to
   * @param animationOptions Optional animation options
   */
  animateCamera(
    camera: Camera,
    animationOptions?: AnimationOptions,
  ): Promise<void>;
  /**
   * Fits the camera in such a way that the coordinates passed represent the
   * opposite corners of a rectangle contained within the camera view
   * @param options The options to fit the bounds
   * @param animationOptions Optional animation options
   */
  fitToBounds(
    options: FitToBoundsOptions,
    animationOptions?: AnimationOptions,
  ): Promise<void>;
};

type NativeViewProps = Omit<MapViewProps, "onMapIdle"> & {
  ref?: React.Ref<MapFunctions>;
  onMapIdle?: (event: { nativeEvent: { cameraPosition: Camera } }) => void;
};

const NativeView: React.ComponentType<NativeViewProps> =
  requireNativeViewManager("ExpoGoogleMaps");

export function MapView(props: MapViewProps) {
  const innerOnMapIdle = (event: {
    nativeEvent: { cameraPosition: Camera };
  }) => {
    props.onMapIdle?.({ cameraPosition: event.nativeEvent.cameraPosition });
  };

  return (
    <NativeView {...props} onMapIdle={innerOnMapIdle} ref={props.mapRef} />
  );
}
