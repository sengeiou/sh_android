#import "_Match.h"

@interface Match : _Match {}

typedef enum{
    kCoreDataMatchStateNotStarted = 0,
    kCoreDataMatchStateStarted,
    kCoreDataMatchStateSuspended,
    kCoreDataMatchStateFinished
} kCoreDataMatchState;      //CACTUS: named like this to avoid typedef redefinition with DMBase class

+(Match *)insertWithDictionary:(NSDictionary *)dict;

+(Match *)createTemporaryMatch;
+(Match *)createTemporaryMatchFromMatch:(Match *)match;

+(Match *)updateWithDictionary:(NSDictionary *)dict;
+(Match *)updateWithDictionary:(NSDictionary *)dict withIndex:(NSInteger)index;

//------------------------------------------------------------------------------
/** 
 Change an array of NSDictionary matches to adapt them the way CoreData Match object
 parse matches and its Teams.
 
 @param     matchesToChange Array of NSDictionary matches to transform.
 @return    NSArray         Array of adapted dictionary matches.
 */
//------------------------------------------------------------------------------
+ (NSArray *)changeMatchesFormatToParse:(NSArray *)matchesToChange;

@end
