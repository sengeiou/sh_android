#import "_Player.h"


@interface User : _Player {}

+(User *)insertWithDictionary:(NSDictionary *)dict;
+(User *)updateWithDictionary:(NSDictionary *)dict withIndex:(NSInteger)index;

@end
