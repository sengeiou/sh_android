// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to EventOfMatch.m instead.

#import "_EventOfMatch.h"

const struct EventOfMatchAttributes EventOfMatchAttributes = {
	.actorInTransmitterName = @"actorInTransmitterName",
	.actorReceptorName = @"actorReceptorName",
	.actorTransmitterName = @"actorTransmitterName",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.dateIn = @"dateIn",
	.idEvent = @"idEvent",
	.idEventOfMatch = @"idEventOfMatch",
	.idMatch = @"idMatch",
	.idPeriod = @"idPeriod",
	.idTeam = @"idTeam",
	.isOwnGoal = @"isOwnGoal",
	.isPenaltyGoal = @"isPenaltyGoal",
	.localScore = @"localScore",
	.matchMinute = @"matchMinute",
	.status = @"status",
	.twittGoalDone = @"twittGoalDone",
	.visitorScore = @"visitorScore",
};

const struct EventOfMatchRelationships EventOfMatchRelationships = {
	.event = @"event",
	.eventTeam = @"eventTeam",
	.match = @"match",
};

const struct EventOfMatchFetchedProperties EventOfMatchFetchedProperties = {
};

@implementation EventOfMatchID
@end

@implementation _EventOfMatch

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"EventOfMatch" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"EventOfMatch";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"EventOfMatch" inManagedObjectContext:moc_];
}

- (EventOfMatchID*)objectID {
	return (EventOfMatchID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idEventValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idEvent"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idEventOfMatchValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idEventOfMatch"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idMatchValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idMatch"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idPeriodValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idPeriod"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"isOwnGoalValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"isOwnGoal"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"isPenaltyGoalValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"isPenaltyGoal"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"localScoreValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"localScore"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"matchMinuteValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"matchMinute"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"statusValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"status"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"twittGoalDoneValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"twittGoalDone"];
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




@dynamic actorInTransmitterName;






@dynamic actorReceptorName;






@dynamic actorTransmitterName;






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






@dynamic dateIn;






@dynamic idEvent;



- (int32_t)idEventValue {
	NSNumber *result = [self idEvent];
	return [result intValue];
}

- (void)setIdEventValue:(int32_t)value_ {
	[self setIdEvent:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdEventValue {
	NSNumber *result = [self primitiveIdEvent];
	return [result intValue];
}

- (void)setPrimitiveIdEventValue:(int32_t)value_ {
	[self setPrimitiveIdEvent:[NSNumber numberWithInt:value_]];
}





@dynamic idEventOfMatch;



- (int64_t)idEventOfMatchValue {
	NSNumber *result = [self idEventOfMatch];
	return [result longLongValue];
}

- (void)setIdEventOfMatchValue:(int64_t)value_ {
	[self setIdEventOfMatch:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdEventOfMatchValue {
	NSNumber *result = [self primitiveIdEventOfMatch];
	return [result longLongValue];
}

- (void)setPrimitiveIdEventOfMatchValue:(int64_t)value_ {
	[self setPrimitiveIdEventOfMatch:[NSNumber numberWithLongLong:value_]];
}





@dynamic idMatch;



- (int32_t)idMatchValue {
	NSNumber *result = [self idMatch];
	return [result intValue];
}

- (void)setIdMatchValue:(int32_t)value_ {
	[self setIdMatch:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdMatchValue {
	NSNumber *result = [self primitiveIdMatch];
	return [result intValue];
}

- (void)setPrimitiveIdMatchValue:(int32_t)value_ {
	[self setPrimitiveIdMatch:[NSNumber numberWithInt:value_]];
}





@dynamic idPeriod;



- (int16_t)idPeriodValue {
	NSNumber *result = [self idPeriod];
	return [result shortValue];
}

- (void)setIdPeriodValue:(int16_t)value_ {
	[self setIdPeriod:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveIdPeriodValue {
	NSNumber *result = [self primitiveIdPeriod];
	return [result shortValue];
}

- (void)setPrimitiveIdPeriodValue:(int16_t)value_ {
	[self setPrimitiveIdPeriod:[NSNumber numberWithShort:value_]];
}





@dynamic idTeam;



- (int32_t)idTeamValue {
	NSNumber *result = [self idTeam];
	return [result intValue];
}

- (void)setIdTeamValue:(int32_t)value_ {
	[self setIdTeam:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdTeamValue {
	NSNumber *result = [self primitiveIdTeam];
	return [result intValue];
}

- (void)setPrimitiveIdTeamValue:(int32_t)value_ {
	[self setPrimitiveIdTeam:[NSNumber numberWithInt:value_]];
}





@dynamic isOwnGoal;



- (BOOL)isOwnGoalValue {
	NSNumber *result = [self isOwnGoal];
	return [result boolValue];
}

- (void)setIsOwnGoalValue:(BOOL)value_ {
	[self setIsOwnGoal:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveIsOwnGoalValue {
	NSNumber *result = [self primitiveIsOwnGoal];
	return [result boolValue];
}

- (void)setPrimitiveIsOwnGoalValue:(BOOL)value_ {
	[self setPrimitiveIsOwnGoal:[NSNumber numberWithBool:value_]];
}





@dynamic isPenaltyGoal;



- (BOOL)isPenaltyGoalValue {
	NSNumber *result = [self isPenaltyGoal];
	return [result boolValue];
}

- (void)setIsPenaltyGoalValue:(BOOL)value_ {
	[self setIsPenaltyGoal:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveIsPenaltyGoalValue {
	NSNumber *result = [self primitiveIsPenaltyGoal];
	return [result boolValue];
}

- (void)setPrimitiveIsPenaltyGoalValue:(BOOL)value_ {
	[self setPrimitiveIsPenaltyGoal:[NSNumber numberWithBool:value_]];
}





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





@dynamic matchMinute;



- (int16_t)matchMinuteValue {
	NSNumber *result = [self matchMinute];
	return [result shortValue];
}

- (void)setMatchMinuteValue:(int16_t)value_ {
	[self setMatchMinute:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveMatchMinuteValue {
	NSNumber *result = [self primitiveMatchMinute];
	return [result shortValue];
}

- (void)setPrimitiveMatchMinuteValue:(int16_t)value_ {
	[self setPrimitiveMatchMinute:[NSNumber numberWithShort:value_]];
}





@dynamic status;



- (int16_t)statusValue {
	NSNumber *result = [self status];
	return [result shortValue];
}

- (void)setStatusValue:(int16_t)value_ {
	[self setStatus:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveStatusValue {
	NSNumber *result = [self primitiveStatus];
	return [result shortValue];
}

- (void)setPrimitiveStatusValue:(int16_t)value_ {
	[self setPrimitiveStatus:[NSNumber numberWithShort:value_]];
}





@dynamic twittGoalDone;



- (BOOL)twittGoalDoneValue {
	NSNumber *result = [self twittGoalDone];
	return [result boolValue];
}

- (void)setTwittGoalDoneValue:(BOOL)value_ {
	[self setTwittGoalDone:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveTwittGoalDoneValue {
	NSNumber *result = [self primitiveTwittGoalDone];
	return [result boolValue];
}

- (void)setPrimitiveTwittGoalDoneValue:(BOOL)value_ {
	[self setPrimitiveTwittGoalDone:[NSNumber numberWithBool:value_]];
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





@dynamic event;

	

@dynamic eventTeam;

	

@dynamic match;

	






@end
