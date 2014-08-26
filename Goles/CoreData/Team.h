#import "_Team.h"

@interface Team : _Team {}
// Custom logic goes here.

+(Team *)insertWithDictionary:(NSDictionary *)dict;
+(Team *)insertWithDictionary:(NSDictionary *)dict andIndex:(NSInteger)index;
+(Team *)updateWithDictionary:(NSDictionary *)dict;
+(Team *)updateWithDictionary:(NSDictionary *)dict withIndex:(NSInteger)index;

+(Team *)createTemporaryTeam;
+(Team *)createTemporaryTeamWithTeam:(Team *)team;

@end
