import {
	type MapFunctions,
	MapView,
	MarkerView,
	setApiKey,
} from '@balloman/expo-google-maps';
import * as Location from 'expo-location';
import React, { useEffect } from 'react';
import { Button, Text, View } from 'react-native';
import * as z from 'zod';
import styleJson from '../style.json';

const env = z
	.object({
		EXPO_PUBLIC_API_KEY: z.string(),
	})
	.parse(process.env);

setApiKey(env.EXPO_PUBLIC_API_KEY);

/**
 * Render a full-screen map with markers, a polygon overlay, and a button that animates the camera.
 *
 * Requests foreground location permission on mount and, if the status is undetermined, prompts the user.
 *
 * @returns The React element containing the MapView (showing user location), two MarkerView entries, a polygon, and a button that triggers a camera animation.
 */
export default function Index() {
	const mapViewRef = React.useRef<MapFunctions>(null);
	const [status, setStatus] = React.useState<Location.PermissionStatus>();
	const [text] = React.useState<string>('hello');
	const [markerPos] = React.useState<number>(37.78825);
	const [markerWidth] = React.useState(25);

	useEffect(() => {
		Location.getForegroundPermissionsAsync()
			.then(({ status }) => {
				setStatus(status);
			})
			.catch((e) => {
				if (e instanceof TypeError) {
					return;
				}
				console.error(e);
			});
	}, []);

	useEffect(() => {
		if (status === Location.PermissionStatus.UNDETERMINED) {
			Location.requestForegroundPermissionsAsync()
				.then(({ status }) => {
					setStatus(status);
				})
				.catch((e) => {
					console.log('error', e);
				});
		}
	}, [status]);

	return (
		<View
			style={{
				flex: 1,
				justifyContent: 'center',
				alignItems: 'center',
			}}
		>
			<MapView
				camera={{
					center: { latitude: 37.78825, longitude: -122.4324 },
					zoom: 13,
				}}
				style={{ position: 'absolute', width: '100%', height: '100%' }}
				mapRef={mapViewRef}
				showUserLocation
				styleJson={JSON.stringify(styleJson)}
				onMapIdle={({ cameraPosition }) =>
					console.log('cameraPosition', cameraPosition)
				}
				polygons={[
					{
						key: '1',
						coordinates: [
							{ latitude: 37.78825, longitude: -122.4324 },
							{ latitude: 37.78825, longitude: -122.44 },
							{ latitude: 37.792, longitude: -122.44 },
							{ latitude: 37.792, longitude: -122.4324 },
						],
						strokeColor: 'red',
						fillColor: '#5533CC22',
					},
				]}
			>
				<MarkerView
					marker={{
						key: '1',
						position: {
							latitude: markerPos,
							longitude: -122.4324,
						},
						title: 'Hello World',
					}}
				/>
				<MarkerView
					marker={{
						key: '2',
						position: {
							latitude: markerPos,
							longitude: -122.44,
						},
						title: 'Testing',
					}}
					tracksViewChanges={true}
					onMarkerPress={() => console.log('marker pressed')}
				>
					<View
						style={{
							alignItems: 'center',
							backgroundColor: 'blue',
							padding: 20,
						}}
					>
						<Text>{`${text} ${markerWidth}`}</Text>
					</View>
				</MarkerView>
			</MapView>
			<View>
				<Button
					title="Hello World"
					onPress={() => {
						mapViewRef.current?.animateCamera(
							{
								center: {
									latitude: 35,
									longitude: 35,
								},
								zoom: 7,
								viewingAngle: 40,
							},
							{
								animationDuration: 2,
							},
						);
						console.log('Clicked');
					}}
				/>
			</View>
		</View>
	);
}
