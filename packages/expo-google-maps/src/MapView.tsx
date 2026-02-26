import { requireNativeViewManager } from 'expo-modules-core';
import type * as React from 'react';
import type { ViewProps } from 'react-native';

import type {
	AnimationOptions,
	Camera,
	FitToBoundsOptions,
	Polygon,
} from './ExpoGoogleMaps.types';

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

export type MapViewProps = MapProperties & MapEvents;

type MapEvents = {
	/** A callback for when the map is idle after being moved */
	onMapIdle?: (event: {
		/** The position of the camera */
		cameraPosition: Camera;
	}) => void;
	/** A callback that is called repeatedly during any animations or gestures on the map (or once, if the camera is explicitly set).
	 * This may not be called for all intermediate camera positions. It is always called for the final position of an animation or gesture.
	 */
	onDidChange?: (event: {
		/** The position of the camera */
		cameraPosition: Camera;
	}) => void;
};

type MapProperties = {
	/** A prop to modify the camera */
	camera: Camera;
	/** Any polygons to display on the map */
	polygons?: Polygon[];
	/** A ref to the imperative functions of the map */
	mapRef?: React.Ref<MapFunctions>;
	/** A json string of the style to apply to the map, if needed. See here: https://mapstyle.withgoogle.com/ */
	styleJson?: string;
	/** Controls whether the My Location dot and accuracy circle is enabled.
	 * Due to a limitation in expo modules, setting this prop to undefined does not update the map.
	 * It must be set to either true or false to change the state
	 * @default false
	 */
	showUserLocation?: boolean;
} & ViewProps;

type NativeViewProps = MapProperties & {
	ref?: React.Ref<MapFunctions>;
	onMapIdle?: (event: { nativeEvent: { cameraPosition: Camera } }) => void;
	onDidChange?: (event: { nativeEvent: { cameraPosition: Camera } }) => void;
};

const NativeView: React.ComponentType<NativeViewProps> =
	requireNativeViewManager('ExpoGoogleMaps');

/**
 * Renders the Expo Google Maps native view, forwarding props and ref and converting native map events into the public MapView callbacks.
 *
 * @returns A React element that displays the native Expo Google Maps view configured with the provided props and ref.
 */
export function MapView(props: MapViewProps) {
	const innerOnMapIdle = (event: {
		nativeEvent: { cameraPosition: Camera };
	}) => {
		props.onMapIdle?.({ cameraPosition: event.nativeEvent.cameraPosition });
	};

	const innerOnDidChange = (event: {
		nativeEvent: { cameraPosition: Camera };
	}) => {
		props.onDidChange?.({ cameraPosition: event.nativeEvent.cameraPosition });
	};

	return (
		<NativeView
			{...props}
			onMapIdle={innerOnMapIdle}
			onDidChange={innerOnDidChange}
			ref={props.mapRef}
		/>
	);
}
