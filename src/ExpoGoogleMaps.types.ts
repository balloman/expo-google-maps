export type Camera = {
  /** The center of the camera */
  center: Coordinate;
  /** How zoomed in the camera is, higher numbers are more zoomed in;
   * @see {@link https://developers.google.com/maps/documentation/ios-sdk/views#zoom}
   */
  zoom: number;
  /** The heading of the camera */
  bearing?: number;
  /** The viewing angle of the camera @see {@link https://developers.google.com/maps/documentation/ios-sdk/views} */
  viewingAngle?: number;
};

export type Polygon = {
  /** The unique key of the polygon */
  key: string;
  /** The coordinates of the polygon */
  coordinates: Coordinate[];
  /** The stroke color of the polygon */
  strokeColor: string;
  /** The fill color of the polygon */
  fillColor?: string;
};

export type Coordinate = {
  latitude: number;
  longitude: number;
};

export type Insets = {
  top?: number;
  left?: number;
  bottom?: number;
  right?: number;
};

export type FitToBoundsOptions = {
  topRight: Coordinate;
  bottomLeft: Coordinate;
  /** The padding around the coordinates. On android, only a single number is allowed, so the max of the insets is chosen */
  insets?: Insets;
};

/**
 * Animation options for camera movements
 */
export type AnimationOptions = {
  /** The duration in seoncds */
  animationDuration?: number;
  /** The animation function to use - ios only
   * @platform ios
   */
  animationFunction?:
    | "easeIn"
    | "easeOut"
    | "easeInEaseOut"
    | "linear"
    | "default";
};
