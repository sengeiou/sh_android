#import "_Player.h"


@interface Player : _Player {}

+(Player *)insertWithDictionary:(NSDictionary *)dict;
+(Player *)updateWithDictionary:(NSDictionary *)dict withIndex:(NSInteger)index;

@end
