// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Match.m instead.

#import "_Match.h"

const struct MatchAttributes MatchAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.elapsedMinutes = @"elapsedMinutes",
	.endFinal = @"endFinal",
	.idLocalTeam = @"idLocalTeam",
	.idMatch = @"idMatch",
	.idPreviousMatch = @"idPreviousMatch",
	.idRound = @"idRound",
	.idVisitorTeam = @"idVisitorTeam",
	.idWinnerTeam = @"idWinnerTeam",
	.listTV = @"listTV",
	.localScore = @"localScore",
	.matchDate = @"matchDate",
	.matchState = @"matchState",
	.matchSubstate = @"matchSubstate",
	.matchType = @"matchType",
	.notConfirmedMatchDate = @"notConfirmedMatchDate",
	.order = @"order",
	.overTimeStartDate = @"overTimeStartDate",
	.previousMatchScore = @"previousMatchScore",
	.scorePenaltiesLocalTeam = @"scorePenaltiesLocalTeam",
	.scorePenaltiesVisitorTeam = @"scorePenaltiesVisitorTeam",
	.secondHalfStartDate = @"secondHalfStartDate",
	.startDate = @"startDate",
	.twitterLocal = @"twitterLocal",
	.twitterVisitor = @"twitterVisitor",
	.visitorScore = @"visitorScore",
};

const struct MatchRelationships MatchRelationships = {
	.eventsOfMatch = @"eventsOfMatch",
	.lineUpLocal = @"lineUpLocal",
	.lineUpVisitor = @"lineUpVisitor",
	.matchBetType = @"matchBetType",
	.round = @"round",
	.subscription = @"subscription",
	.teamLocal = @"teamLocal",
	.teamVisitor = @"teamVisitor",
};

const struct MatchFetchedProperties MatchFetchedProperties = {
};

@implementation MatchID
@end

@implementation _Match

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Match" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Match";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Match" inManagedObjectContext:moc_];
}

- (MatchID*)objectID {
	return (MatchID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"elapsedMinutesValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"elapsedMinutes"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"endFinalValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"endFinal"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idLocalTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idLocalTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idMatchValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idMatch"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idPreviousMatchValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idPreviousMatch"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idRoundValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idRound"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idVisitorTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idVisitorTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idWinnerTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idWinnerTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"localScoreValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"localScore"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"matchDateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"matchDate"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"matchStateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"matchState"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"matchSubstateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"matchSubstate"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"matchTypeValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"matchType"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"notConfirmedMatchDateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"notConfirmedMatchDate"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"orderValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"order"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"overTimeStartDateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"overTimeStartDate"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"scorePenaltiesLocalTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"scorePenaltiesLocalTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"scorePenaltiesVisitorTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"scorePenaltiesVisitorTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"secondHalfStartDateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"secondHalfStartDate"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"startDateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"startDate"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"twitterLocalValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"twitterLocal"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"twitterVisitorValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"twitterVisitor"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"visitorScoreValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"visitorScore"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
}




@dynamic csys_birth;






@dynamic csys_deleted;






@dynamic csys_modified;






@dynamic csys_revision;



- (int64_t)csys_revisionValue {
	NSNumber *result = [self csys_revision];
	return [result longLongValue];
}

- (void)setCsys_revisionValue:(int64_t)value_ {
	[self setCsys_revision:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveCsys_revisionValue {
	NSNumber *result = [self primitiveCsys_revision];
	return [result longLongValue];
}

- (void)setPrimitiveCsys_revisionValue:(int64_t)value_ {
	[self setPrimitiveCsys_revision:[NSNumber numberWithLongLong:value_]];
}





@dynamic csys_syncronized;






@dynamic elapsedMinutes;



- (int32_t)elapsedMinutesValue {
	NSNumber *result = [self elapsedMinutes];
	return [result intValue];
}

- (void)setElapsedMinutesValue:(int32_t)value_ {
	[self setElapsedMinutes:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveElapsedMinutesValue {
	NSNumber *result = [self primitiveElapsedMinutes];
	return [result intValue];
}

- (void)setPrimitiveElapsedMinutesValue:(int32_t)value_ {
	[self setPrimitiveElapsedMinutes:[NSNumber numberWithInt:value_]];
}





@dynamic endFinal;



- (int64_t)endFinalValue {
	NSNumber *result = [self endFinal];
	return [result longLongValue];
}

- (void)setEndFinalValue:(int64_t)value_ {
	[self setEndFinal:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveEndFinalValue {
	NSNumber *result = [self primitiveEndFinal];
	return [result longLongValue];
}

- (void)setPrimitiveEndFinalValue:(int64_t)value_ {
	[self setPrimitiveEndFinal:[NSNumber numberWithLongLong:value_]];
}





@dynamic idLocalTeam;



- (int32_t)idLocalTeamValue {
	NSNumber *result = [self idLocalTeam];
	return [result intValue];
}

- (void)setIdLocalTeamValue:(int32_t)value_ {
	[self setIdLocalTeam:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdLocalTeamValue {
	NSNumber *result = [self primitiveIdLocalTeam];
	return [result intValue];
}

- (void)setPrimitiveIdLocalTeamValue:(int32_t)value_ {
	[self setPrimitiveIdLocalTeam:[NSNumber numberWithInt:value_]];
}





@dynamic idMatch;



- (int64_t)idMatchValue {
	NSNumber *result = [self idMatch];
	return [result longLongValue];
}

- (void)setIdMatchValue:(int64_t)value_ {
	[self setIdMatch:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdMatchValue {
	NSNumber *result = [self primitiveIdMatch];
	return [result longLongValue];
}

- (void)setPrimitiveIdMatchValue:(int64_t)value_ {
	[self setPrimitiveIdMatch:[NSNumber numberWithLongLong:value_]];
}





@dynamic idPreviousMatch;



- (int64_t)idPreviousMatchValue {
	NSNumber *result = [self idPreviousMatch];
	return [result longLongValue];
}

- (void)setIdPreviousMatchValue:(int64_t)value_ {
	[self setIdPreviousMatch:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdPreviousMatchValue {
	NSNumber *result = [self primitiveIdPreviousMatch];
	return [result longLongValue];
}

- (void)setPrimitiveIdPreviousMatchValue:(int64_t)value_ {
	[self setPrimitiveIdPreviousMatch:[NSNumber numberWithLongLong:value_]];
}





@dynamic idRound;



- (int32_t)idRoundValue {
	NSNumber *result = [self idRound];
	return [result intValue];
}

- (void)setIdRoundValue:(int32_t)value_ {
	[self setIdRound:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdRoundValue {
	NSNumber *result = [self primitiveIdRound];
	return [result intValue];
}

- (void)setPrimitiveIdRoundValue:(int32_t)value_ {
	[self setPrimitiveIdRound:[NSNumber numberWithInt:value_]];
}





@dynamic idVisitorTeam;



- (int32_t)idVisitorTeamValue {
	NSNumber *result = [self idVisitorTeam];
	return [result intValue];
}

- (void)setIdVisitorTeamValue:(int32_t)value_ {
	[self setIdVisitorTeam:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdVisitorTeamValue {
	NSNumber *result = [self primitiveIdVisitorTeam];
	return [result intValue];
}

- (void)setPrimitiveIdVisitorTeamValue:(int32_t)value_ {
	[self setPrimitiveIdVisitorTeam:[NSNumber numberWithInt:value_]];
}





@dynamic idWinnerTeam;



- (int32_t)idWinnerTeamValue {
	NSNumber *result = [self idWinnerTeam];
	return [result intValue];
}

- (void)setIdWinnerTeamValue:(int32_t)value_ {
	[self setIdWinnerTeam:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdWinnerTeamValue {
	NSNumber *result = [self primitiveIdWinnerTeam];
	return [result intValue];
}

- (void)setPrimitiveIdWinnerTeamValue:(int32_t)value_ {
	[self setPrimitiveIdWinnerTeam:[NSNumber numberWithInt:value_]];
}





@dynamic listTV;






@dynamic localScore;



- (int16_t)localScoreValue {
	NSNumber *result = [self localScore];
	return [result shortValue];
}

- (void)setLocalScoreValue:(int16_t)value_ {
	[self setLocalScore:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveLocalScoreValue {
	NSNumber *result = [self primitiveLocalScore];
	return [result shortValue];
}

- (void)setPrimitiveLocalScoreValue:(int16_t)value_ {
	[self setPrimitiveLocalScore:[NSNumber numberWithShort:value_]];
}





@dynamic matchDate;



- (int64_t)matchDateValue {
	NSNumber *result = [self matchDate];
	return [result longLongValue];
}

- (void)setMatchDateValue:(int64_t)value_ {
	[self setMatchDate:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveMatchDateValue {
	NSNumber *result = [self primitiveMatchDate];
	return [result longLongValue];
}

- (void)setPrimitiveMatchDateValue:(int64_t)value_ {
	[self setPrimitiveMatchDate:[NSNumber numberWithLongLong:value_]];
}





@dynamic matchState;



- (int16_t)matchStateValue {
	NSNumber *result = [self matchState];
	return [result shortValue];
}

- (void)setMatchStateValue:(int16_t)value_ {
	[self setMatchState:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveMatchStateValue {
	NSNumber *result = [self primitiveMatchState];
	return [result shortValue];
}

- (void)setPrimitiveMatchStateValue:(int16_t)value_ {
	[self setPrimitiveMatchState:[NSNumber numberWithShort:value_]];
}





@dynamic matchSubstate;



- (int16_t)matchSubstateValue {
	NSNumber *result = [self matchSubstate];
	return [result shortValue];
}

- (void)setMatchSubstateValue:(int16_t)value_ {
	[self setMatchSubstate:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveMatchSubstateValue {
	NSNumber *result = [self primitiveMatchSubstate];
	return [result shortValue];
}

- (void)setPrimitiveMatchSubstateValue:(int16_t)value_ {
	[self setPrimitiveMatchSubstate:[NSNumber numberWithShort:value_]];
}





@dynamic matchType;



- (int16_t)matchTypeValue {
	NSNumber *result = [self matchType];
	return [result shortValue];
}

- (void)setMatchTypeValue:(int16_t)value_ {
	[self setMatchType:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveMatchTypeValue {
	NSNumber *result = [self primitiveMatchType];
	return [result shortValue];
}

- (void)setPrimitiveMatchTypeValue:(int16_t)value_ {
	[self setPrimitiveMatchType:[NSNumber numberWithShort:value_]];
}





@dynamic notConfirmedMatchDate;



- (BOOL)notConfirmedMatchDateValue {
	NSNumber *result = [self notConfirmedMatchDate];
	return [result boolValue];
}

- (void)setNotConfirmedMatchDateValue:(BOOL)value_ {
	[self setNotConfirmedMatchDate:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveNotConfirmedMatchDateValue {
	NSNumber *result = [self primitiveNotConfirmedMatchDate];
	return [result boolValue];
}

- (void)setPrimitiveNotConfirmedMatchDateValue:(BOOL)value_ {
	[self setPrimitiveNotConfirmedMatchDate:[NSNumber numberWithBool:value_]];
}





@dynamic order;



- (int64_t)orderValue {
	NSNumber *result = [self order];
	return [result longLongValue];
}

- (void)setOrderValue:(int64_t)value_ {
	[self setOrder:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveOrderValue {
	NSNumber *result = [self primitiveOrder];
	return [result longLongValue];
}

- (void)setPrimitiveOrderValue:(int64_t)value_ {
	[self setPrimitiveOrder:[NSNumber numberWithLongLong:value_]];
}





@dynamic overTimeStartDate;



- (int64_t)overTimeStartDateValue {
	NSNumber *result = [self overTimeStartDate];
	return [result longLongValue];
}

- (void)setOverTimeStartDateValue:(int64_t)value_ {
	[self setOverTimeStartDate:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveOverTimeStartDateValue {
	NSNumber *result = [self primitiveOverTimeStartDate];
	return [result longLongValue];
}

- (void)setPrimitiveOverTimeStartDateValue:(int64_t)value_ {
	[self setPrimitiveOverTimeStartDate:[NSNumber numberWithLongLong:value_]];
}





@dynamic previousMatchScore;






@dynamic scorePenaltiesLocalTeam;



- (int64_t)scorePenaltiesLocalTeamValue {
	NSNumber *result = [self scorePenaltiesLocalTeam];
	return [result longLongValue];
}

- (void)setScorePenaltiesLocalTeamValue:(int64_t)value_ {
	[self setScorePenaltiesLocalTeam:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveScorePenaltiesLocalTeamValue {
	NSNumber *result = [self primitiveScorePenaltiesLocalTeam];
	return [result longLongValue];
}

- (void)setPrimitiveScorePenaltiesLocalTeamValue:(int64_t)value_ {
	[self setPrimitiveScorePenaltiesLocalTeam:[NSNumber numberWithLongLong:value_]];
}





@dynamic scorePenaltiesVisitorTeam;



- (int64_t)scorePenaltiesVisitorTeamValue {
	NSNumber *result = [self scorePenaltiesVisitorTeam];
	return [result longLongValue];
}

- (void)setScorePenaltiesVisitorTeamValue:(int64_t)value_ {
	[self setScorePenaltiesVisitorTeam:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveScorePenaltiesVisitorTeamValue {
	NSNumber *result = [self primitiveScorePenaltiesVisitorTeam];
	return [result longLongValue];
}

- (void)setPrimitiveScorePenaltiesVisitorTeamValue:(int64_t)value_ {
	[self setPrimitiveScorePenaltiesVisitorTeam:[NSNumber numberWithLongLong:value_]];
}





@dynamic secondHalfStartDate;



- (int64_t)secondHalfStartDateValue {
	NSNumber *result = [self secondHalfStartDate];
	return [result longLongValue];
}

- (void)setSecondHalfStartDateValue:(int64_t)value_ {
	[self setSecondHalfStartDate:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveSecondHalfStartDateValue {
	NSNumber *result = [self primitiveSecondHalfStartDate];
	return [result longLongValue];
}

- (void)setPrimitiveSecondHalfStartDateValue:(int64_t)value_ {
	[self setPrimitiveSecondHalfStartDate:[NSNumber numberWithLongLong:value_]];
}





@dynamic startDate;



- (int64_t)startDateValue {
	NSNumber *result = [self startDate];
	return [result longLongValue];
}

- (void)setStartDateValue:(int64_t)value_ {
	[self setStartDate:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveStartDateValue {
	NSNumber *result = [self primitiveStartDate];
	return [result longLongValue];
}

- (void)setPrimitiveStartDateValue:(int64_t)value_ {
	[self setPrimitiveStartDate:[NSNumber numberWithLongLong:value_]];
}





@dynamic twitterLocal;



- (BOOL)twitterLocalValue {
	NSNumber *result = [self twitterLocal];
	return [result boolValue];
}

- (void)setTwitterLocalValue:(BOOL)value_ {
	[self setTwitterLocal:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveTwitterLocalValue {
	NSNumber *result = [self primitiveTwitterLocal];
	return [result boolValue];
}

- (void)setPrimitiveTwitterLocalValue:(BOOL)value_ {
	[self setPrimitiveTwitterLocal:[NSNumber numberWithBool:value_]];
}





@dynamic twitterVisitor;



- (BOOL)twitterVisitorValue {
	NSNumber *result = [self twitterVisitor];
	return [result boolValue];
}

- (void)setTwitterVisitorValue:(BOOL)value_ {
	[self setTwitterVisitor:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveTwitterVisitorValue {
	NSNumber *result = [self primitiveTwitterVisitor];
	return [result boolValue];
}

- (void)setPrimitiveTwitterVisitorValue:(BOOL)value_ {
	[self setPrimitiveTwitterVisitor:[NSNumber numberWithBool:value_]];
}





@dynamic visitorScore;



- (int16_t)visitorScoreValue {
	NSNumber *result = [self visitorScore];
	return [result shortValue];
}

- (void)setVisitorScoreValue:(int16_t)value_ {
	[self setVisitorScore:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveVisitorScoreValue {
	NSNumber *result = [self primitiveVisitorScore];
	return [result shortValue];
}

- (void)setPrimitiveVisitorScoreValue:(int16_t)value_ {
	[self setPrimitiveVisitorScore:[NSNumber numberWithShort:value_]];
}





@dynamic eventsOfMatch;

	
- (NSMutableSet*)eventsOfMatchSet {
	[self willAccessValueForKey:@"eventsOfMatch"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"eventsOfMatch"];
  
	[self didAccessValueForKey:@"eventsOfMatch"];
	return result;
}
	

@dynamic lineUpLocal;

	

@dynamic lineUpVisitor;

	

@dynamic matchBetType;

	
- (NSMutableSet*)matchBetTypeSet {
	[self willAccessValueForKey:@"matchBetType"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"matchBetType"];
  
	[self didAccessValueForKey:@"matchBetType"];
	return result;
}
	

@dynamic round;

	

@dynamic subscription;

	

@dynamic teamLocal;

	

@dynamic teamVisitor;

	






@end
