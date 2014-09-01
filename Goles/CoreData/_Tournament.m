// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Tournament.m instead.

#import "_Tournament.h"

const struct TournamentAttributes TournamentAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.endDate = @"endDate",
	.firstC = @"firstC",
	.fourthC = @"fourthC",
	.idTournament = @"idTournament",
	.imageName = @"imageName",
	.isLeague = @"isLeague",
	.name = @"name",
	.orderBy = @"orderBy",
	.secondC = @"secondC",
	.startDate = @"startDate",
	.status = @"status",
	.thirdC = @"thirdC",
	.visibleApp = @"visibleApp",
	.year = @"year",
};

const struct TournamentRelationships TournamentRelationships = {
	.modes = @"modes",
	.rounds = @"rounds",
};

const struct TournamentFetchedProperties TournamentFetchedProperties = {
};

@implementation TournamentID
@end

@implementation _Tournament

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Tournament" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Tournament";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Tournament" inManagedObjectContext:moc_];
}

- (TournamentID*)objectID {
	return (TournamentID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"firstCValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"firstC"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"fourthCValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"fourthC"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idTournamentValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idTournament"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"isLeagueValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"isLeague"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"orderByValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"orderBy"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"secondCValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"secondC"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"statusValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"status"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"thirdCValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"thirdC"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"visibleAppValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"visibleApp"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"yearValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"year"];
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






@dynamic endDate;






@dynamic firstC;



- (int16_t)firstCValue {
	NSNumber *result = [self firstC];
	return [result shortValue];
}

- (void)setFirstCValue:(int16_t)value_ {
	[self setFirstC:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveFirstCValue {
	NSNumber *result = [self primitiveFirstC];
	return [result shortValue];
}

- (void)setPrimitiveFirstCValue:(int16_t)value_ {
	[self setPrimitiveFirstC:[NSNumber numberWithShort:value_]];
}





@dynamic fourthC;



- (int16_t)fourthCValue {
	NSNumber *result = [self fourthC];
	return [result shortValue];
}

- (void)setFourthCValue:(int16_t)value_ {
	[self setFourthC:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveFourthCValue {
	NSNumber *result = [self primitiveFourthC];
	return [result shortValue];
}

- (void)setPrimitiveFourthCValue:(int16_t)value_ {
	[self setPrimitiveFourthC:[NSNumber numberWithShort:value_]];
}





@dynamic idTournament;



- (int64_t)idTournamentValue {
	NSNumber *result = [self idTournament];
	return [result longLongValue];
}

- (void)setIdTournamentValue:(int64_t)value_ {
	[self setIdTournament:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdTournamentValue {
	NSNumber *result = [self primitiveIdTournament];
	return [result longLongValue];
}

- (void)setPrimitiveIdTournamentValue:(int64_t)value_ {
	[self setPrimitiveIdTournament:[NSNumber numberWithLongLong:value_]];
}





@dynamic imageName;






@dynamic isLeague;



- (BOOL)isLeagueValue {
	NSNumber *result = [self isLeague];
	return [result boolValue];
}

- (void)setIsLeagueValue:(BOOL)value_ {
	[self setIsLeague:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveIsLeagueValue {
	NSNumber *result = [self primitiveIsLeague];
	return [result boolValue];
}

- (void)setPrimitiveIsLeagueValue:(BOOL)value_ {
	[self setPrimitiveIsLeague:[NSNumber numberWithBool:value_]];
}





@dynamic name;






@dynamic orderBy;



- (int32_t)orderByValue {
	NSNumber *result = [self orderBy];
	return [result intValue];
}

- (void)setOrderByValue:(int32_t)value_ {
	[self setOrderBy:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveOrderByValue {
	NSNumber *result = [self primitiveOrderBy];
	return [result intValue];
}

- (void)setPrimitiveOrderByValue:(int32_t)value_ {
	[self setPrimitiveOrderBy:[NSNumber numberWithInt:value_]];
}





@dynamic secondC;



- (int16_t)secondCValue {
	NSNumber *result = [self secondC];
	return [result shortValue];
}

- (void)setSecondCValue:(int16_t)value_ {
	[self setSecondC:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveSecondCValue {
	NSNumber *result = [self primitiveSecondC];
	return [result shortValue];
}

- (void)setPrimitiveSecondCValue:(int16_t)value_ {
	[self setPrimitiveSecondC:[NSNumber numberWithShort:value_]];
}





@dynamic startDate;






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





@dynamic thirdC;



- (int16_t)thirdCValue {
	NSNumber *result = [self thirdC];
	return [result shortValue];
}

- (void)setThirdCValue:(int16_t)value_ {
	[self setThirdC:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveThirdCValue {
	NSNumber *result = [self primitiveThirdC];
	return [result shortValue];
}

- (void)setPrimitiveThirdCValue:(int16_t)value_ {
	[self setPrimitiveThirdC:[NSNumber numberWithShort:value_]];
}





@dynamic visibleApp;



- (int16_t)visibleAppValue {
	NSNumber *result = [self visibleApp];
	return [result shortValue];
}

- (void)setVisibleAppValue:(int16_t)value_ {
	[self setVisibleApp:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveVisibleAppValue {
	NSNumber *result = [self primitiveVisibleApp];
	return [result shortValue];
}

- (void)setPrimitiveVisibleAppValue:(int16_t)value_ {
	[self setPrimitiveVisibleApp:[NSNumber numberWithShort:value_]];
}





@dynamic year;



- (int64_t)yearValue {
	NSNumber *result = [self year];
	return [result longLongValue];
}

- (void)setYearValue:(int64_t)value_ {
	[self setYear:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveYearValue {
	NSNumber *result = [self primitiveYear];
	return [result longLongValue];
}

- (void)setPrimitiveYearValue:(int64_t)value_ {
	[self setPrimitiveYear:[NSNumber numberWithLongLong:value_]];
}





@dynamic modes;

	
- (NSMutableSet*)modesSet {
	[self willAccessValueForKey:@"modes"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"modes"];
  
	[self didAccessValueForKey:@"modes"];
	return result;
}
	

@dynamic rounds;

	
- (NSMutableSet*)roundsSet {
	[self willAccessValueForKey:@"rounds"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"rounds"];
  
	[self didAccessValueForKey:@"rounds"];
	return result;
}
	






@end
