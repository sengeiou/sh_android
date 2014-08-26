#import "_Mode.h"

@interface Mode : _Mode {}
// Custom logic goes here.

+(Mode *)insertWithDictionary:(NSDictionary *)dict;
+(Mode *)insertWithDictionary:(NSDictionary *)dict andIndex:(NSInteger)index;
+(Mode *)updateWithDictionary:(NSDictionary *)dict;
+(Mode *)updateWithDictionary:(NSDictionary *)dict withIndex:(NSInteger)index;

+(Mode *)createTemporaryMode;
+(Mode *)createTemporaryModeWithMode:(Mode *)mode;

-(NSArray *)getOrderedMatches;

@end
