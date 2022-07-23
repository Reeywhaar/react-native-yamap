#import "YamapUtils.h"
#import "YamapGeocode.h"

@import YandexMapsMobile;

@implementation YamapGeocode {
    YMKSearchManager* searchManager;
    NSMutableDictionary* searchSessions;
    YMKBoundingBox* defaultBoundingBox;
    YMKGeometry* defaultGeometry;
    YMKSearchOptions* searchOptions;
}

-(id)init {
    self = [super init];

    YMKPoint* southWestPoint = [YMKPoint pointWithLatitude:-89.999999 longitude:-179.999999];
    YMKPoint* northEastPoint = [YMKPoint pointWithLatitude:89.999999 longitude:179.999999];
    defaultBoundingBox = [YMKBoundingBox boundingBoxWithSouthWest:southWestPoint northEast:northEastPoint];
    defaultGeometry = [YMKGeometry geometryWithBoundingBox:defaultBoundingBox];
    searchOptions = [YMKSearchOptions searchOptionsWithSearchTypes:YMKSearchTypeGeo
                                                    resultPageSize:@1
                                                          snippets:YMKSearchSnippetNone
                                              experimentalSnippets:@[]
                                                      userPosition:nil
                                                            origin:nil
                                                      directPageId:nil
                                                          appleCtx:nil
                                                          geometry:TRUE
                                                      advertPageId:nil
                                                      suggestWords:nil
                                         disableSpellingCorrection:FALSE];

    searchManager = [[YMKSearch sharedInstance] createSearchManagerWithSearchManagerType:YMKSearchSearchManagerTypeOnline];
    searchSessions = [NSMutableDictionary dictionary];

    return self;
}

+ (BOOL)requiresMainQueueSetup
{
    return YES;
}

NSString* ERR_GEOCODE_NO_REQUEST_ARG = @"YANDEX_ERR_GEOCODE_NO_REQUEST_ARG";
NSString* ERR_GEOCODE_SEARCH_FAILED = @"YANDEX_ERR_GEOCODE_SEARCH_FAILED";

RCT_EXPORT_METHOD(geocode:(nonnull NSString*) searchQuery
                resolver:(RCTPromiseResolveBlock) resolve
                rejecter:(RCTPromiseRejectBlock) reject {

    dispatch_async(dispatch_get_main_queue(), ^{
        @try {
            NSString *key = [[NSUUID UUID] UUIDString];
            YMKSearchSession* session = [self->searchManager submitWithText:searchQuery
                                 geometry:self->defaultGeometry
                            searchOptions:self->searchOptions
                          responseHandler:^(YMKSearchResponse * _Nullable response, NSError * _Nullable error){

                if (error) {
                    reject(ERR_GEOCODE_SEARCH_FAILED, [NSString stringWithFormat:@"search request: %@", searchQuery], error);
                } else {
                    resolve([self convert: response]);
                }

                [self->searchSessions removeObjectForKey:key];
            }];
            
            // put into dictionary to create strong reference
            [self->searchSessions setObject:session forKey:key];
        }
        @catch ( NSException *error ) {
            reject(ERR_GEOCODE_NO_REQUEST_ARG, [NSString stringWithFormat:@"search request: %@", searchQuery], nil);
        }
    });
})

RCT_EXPORT_METHOD(geocodePoint:(NSArray*) point
                resolver:(RCTPromiseResolveBlock) resolve
                rejecter:(RCTPromiseRejectBlock) reject {

    dispatch_async(dispatch_get_main_queue(), ^{
        @try {
            NSNumber* lat = point[0];
            NSNumber* lon = point[1];
            YMKPoint* ymkpoint = [YMKPoint pointWithLatitude:lat.doubleValue longitude:lon.doubleValue];
            NSString *key = [[NSUUID UUID] UUIDString];
            YMKSearchSession* session = [self->searchManager submitWithPoint:ymkpoint
                                     zoom:nil
                            searchOptions:self->searchOptions
                          responseHandler:^(YMKSearchResponse * _Nullable response, NSError * _Nullable error){

                if (error) {
                    reject(ERR_GEOCODE_SEARCH_FAILED, [NSString stringWithFormat:@"search request: %@", point], error);
                } else {
                    resolve([self convert: response]);
                }

                [self->searchSessions removeObjectForKey:key];
            }];
            
            // put into dictionary to create strong reference
            [self->searchSessions setObject:session forKey:key];
        }
        @catch ( NSException *error ) {
            reject(ERR_GEOCODE_NO_REQUEST_ARG, [NSString stringWithFormat:@"search request: %@", point], nil);
        }
    });
})

RCT_EXPORT_MODULE();

-(NSDictionary *) convert:(YMKSearchResponse*) response {
    YMKGeoObject* geoObject = nil;
    YMKPoint* point = nil;
    YMKSearchAddress* address = nil;
    YMKBoundingBox* box = nil;
    
    for(YMKGeoObjectCollectionItem *result in response.collection.children) {
        if(result == nil) { continue; }
        
        YMKGeoObject* _geoObject = result.obj;
        if(_geoObject == nil) continue;
        
        YMKSearchToponymObjectMetadata* meta = [result.obj.metadataContainer getItemOfClass:YMKSearchToponymObjectMetadata.self];
        if(meta == nil) { continue; }
        YMKSearchAddress* _address = meta.address;
        if(_address == nil) { continue; }
        
        YMKPoint* _point = nil;
        
        for(YMKGeometry* geom in _geoObject.geometry) {
            YMKPoint* point = geom.point;
            if(point != nil) {
                _point = point;
                break;
            }
        }
        
        YMKBoundingBox* _box = _geoObject.boundingBox;
        if(_box == nil) { continue; }
        
        geoObject = _geoObject;
        point = _point;
        address = _address;
        box = _box;
    }
    
    if(geoObject == nil) return nil;
    
    NSMutableDictionary* coords = [NSMutableDictionary new];
    NSMutableDictionary* upperCorner = [NSMutableDictionary new];
    NSMutableDictionary* lowerCorner = [NSMutableDictionary new];
    
    [coords setValue:[NSNumber numberWithDouble:point.latitude] forKey:@"lat"];
    [coords setValue:[NSNumber numberWithDouble:point.longitude] forKey:@"lon"];
    
    [upperCorner setValue:[NSNumber numberWithDouble:box.northEast.latitude] forKey:@"lat"];
    [upperCorner setValue:[NSNumber numberWithDouble:box.northEast.longitude] forKey:@"lon"];
    
    [lowerCorner setValue:[NSNumber numberWithDouble:box.southWest.latitude] forKey:@"lat"];
    [lowerCorner setValue:[NSNumber numberWithDouble:box.southWest.longitude] forKey:@"lon"];
    
    NSMutableArray* components = [NSMutableArray array];
    for(YMKSearchAddressComponent* component in address.components) {
        NSMutableDictionary *data = [NSMutableDictionary new];
        [data setValue:component.name forKey:@"name"];

        NSMutableArray* kinds = [NSMutableArray array];
        for(NSNumber* kind in component.kinds) {
            NSString* str = [YamapUtils getKind:kind];
            [kinds addObject:str];
        }
        [data setValue:kinds forKey:@"kinds"];
        [components addObject:data];
    }

    NSMutableDictionary *resultToPass = [NSMutableDictionary new];
    [resultToPass setValue:geoObject.name forKey:@"name"];
    [resultToPass setValue:geoObject.descriptionText forKey:@"descriptionText"];
    [resultToPass setValue:address.formattedAddress forKey:@"formattedAddress"];
    [resultToPass setValue:coords forKey:@"coords"];
    [resultToPass setValue:lowerCorner forKey:@"lowerCorner"];
    [resultToPass setValue:upperCorner forKey:@"upperCorner"];
    [resultToPass setValue:components forKey:@"components"];

    return resultToPass;
}

@end
