#import "YamapUtils.h"

@import YandexMapsMobile;

@implementation YamapUtils

static NSMutableDictionary* kindDict = nil;

+(void)initKindDict {
    
    NSMutableDictionary* dict = [NSMutableDictionary new];
    [dict setValue:@"unknown" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindUnknown]];
    [dict setValue:@"country" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindCountry]];
    [dict setValue:@"region" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindRegion]];
    [dict setValue:@"province" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindProvince]];
    [dict setValue:@"area" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindArea]];
    [dict setValue:@"locality" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindLocality]];
    [dict setValue:@"district" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindDistrict]];
    [dict setValue:@"street" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindStreet]];
    [dict setValue:@"house" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindHouse]];
    [dict setValue:@"entrance" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindEntrance]];
    [dict setValue:@"route" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindRoute]];
    [dict setValue:@"station" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindStation]];
    [dict setValue:@"metro" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindMetroStation]];
    [dict setValue:@"railway" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindRailwayStation]];
    [dict setValue:@"vegetation" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindVegetation]];
    [dict setValue:@"hydro" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindHydro]];
    [dict setValue:@"airport" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindAirport]];
    [dict setValue:@"other" forKey:[NSString stringWithFormat:@"%li", YMKSearchComponentKindOther]];

    kindDict = dict;
}

+(NSString *)getKind:(NSNumber *)value {
    [YamapUtils initKindDict];

    NSString* val = kindDict[value.stringValue];
    if(val == nil) {
        return @"unknown";
    }

    return val;
}

@end
