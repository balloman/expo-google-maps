import {
	type MapFunctions,
	MapView,
	MarkerView,
	setApiKey,
} from '@balloman/expo-google-maps';
import * as Location from 'expo-location';
import React, { useEffect } from 'react';
import { Text, View } from 'react-native';
import * as z from 'zod';
import styleJson from '../style.json';

const env = z
	.object({
		EXPO_PUBLIC_API_KEY: z.string(),
	})
	.parse(process.env);

setApiKey(env.EXPO_PUBLIC_API_KEY);

export default function Index() {
	const mapViewRef = React.useRef<MapFunctions>(null);
	const [status, setStatus] = React.useState<Location.PermissionStatus>();
	const [text] = React.useState<string>('hi');

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
			}}
		>
			<MapView
				camera={{
					center: { latitude: 37.78825, longitude: -122.4324 },
					zoom: 13,
				}}
				style={{ flex: 1 }}
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
							latitude: 37.78825,
							longitude: -122.4324,
						},
						title: 'Hello World',
					}}
				/>
				<MarkerView
					marker={{
						key: '2',
						position: {
							latitude: 37.78825,
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
						}}
					>
						<Text>{text}</Text>
					</View>
				</MarkerView>
			</MapView>
		</View>
	);
}
