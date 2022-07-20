import { NativeModules } from "react-native";

const { YamapGeocode } = NativeModules;

export type YamapGeocodeResult = {
  id: string;
};

type GeocodeFetcher = (query: string) => Promise<Array<YamapGeocodeResult>>;
const geocode: GeocodeFetcher = (query) => YamapGeocode.geocode(query);

const Geocode = {
  geocode,
};

export default Geocode;
