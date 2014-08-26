#import "_Classification.h"

@interface Classification : _Classification {}

+(Classification *)insertWithDictionary:(NSDictionary *)dict forTournament:(Tournament *) tournament;
+(Classification *)updateWithDictionary:(NSDictionary *)dict forTournament:(Tournament *) tournament;

@end
