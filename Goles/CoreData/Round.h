#import "_Round.h"

@interface Round : _Round {}

+(Round *)insertWithDictionary:(NSDictionary *)dict;
+(Round *)updateWithDictionary:(NSDictionary *)dict;

-(NSArray *)getSortedMatchesByDate;

//------------------------------------------------------------------------------
/**
 Detach (setting its round realation to nil) all matches not present in an idMatches Array from the round
 matches list to avoid being shown as round matches any more.
 
 @param matchIdsArray   Array of matches ids to PRESERVE as round matches
*/
//------------------------------------------------------------------------------
-(void)unlinkMatchesNotInArray:(NSArray *)matchIdsArray;

@end
