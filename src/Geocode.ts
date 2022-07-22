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
  components: { name: string; kinds: AddressComponent[] }[];
};

type GeocodeFetcher = (
  query: string
) => Promise<YamapGeocodeResult | undefined>;
const geocode: GeocodeFetcher = (query) => YamapGeocode.geocode(query);

/**
 * This version of Geocode doesn't use http-geocoding and doesn't require separate api key
 */
const Geocode = {
  geocode,
};

export default Geocode;
