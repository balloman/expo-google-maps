import { requireNativeViewManager } from 'expo-modules-core';
import type * as React from 'react';
import { View } from 'react-native';

import type { Coordinate } from './ExpoGoogleMaps.types';

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
	/** Controls whether the icon for this marker should be redrawn every render
	 * @platform android
	 * @default false
	 */
	tracksViewChanges?: boolean;
	children?: React.ReactElement;
};

const NativeView: React.ComponentType<MarkerViewProps> =
	requireNativeViewManager('ExpoGoogleMapsMarker');

/**
 * Renders a native Expo Google Maps marker view for the given marker props.
 *
 * If `children` are provided, they are wrapped in an absolutely positioned View to work around native view layout behavior.
 *
 * @param props.marker - Marker data containing position, key, and optional title.
 * @param props.onMarkerPress - Callback invoked when the marker is pressed.
 * @param props.tracksViewChanges - Android-only flag that controls whether the native view should track child view changes.
 * @param props.children - Optional React element to display inside the marker.
 * @returns A React element that mounts the native `ExpoGoogleMapsMarker` view with the provided props.
 */
export function MarkerView(props: MarkerViewProps) {
	return (
		<NativeView {...props}>
			{props.children && ( // Due to some weirdness with the native view, we need to wrap the children in an absolute view
				<View style={{ position: 'absolute' }}>{props.children}</View>
			)}
		</NativeView>
	);
}
