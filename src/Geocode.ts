import { NativeModules } from "react-native";
import { AddressComponent, Point } from "./interfaces";

const { YamapGeocode } = NativeModules;

export type YamapGeocodeResult = {
  name: string;
  descriptionText: string;
  formattedAddress: string;
  coords: Point;
  upperCorner: Point;
  lowerCorner: Point;
  components: AddressComponent[];
};

type GeocodeFetcher = (
  query: string
) => Promise<YamapGeocodeResult | undefined>;
const geocode: GeocodeFetcher = (query) => YamapGeocode.geocode(query);

const Geocode = {
  geocode,
};

export default Geocode;
