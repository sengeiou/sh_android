// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Match.m instead.

#import "_Match.h"

const struct MatchAttributes MatchAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idLocalTeam = @"idLocalTeam",
	.idMatch = @"idMatch",
	.idVisitorTeam = @"idVisitorTeam",
	.matchDate = @"matchDate",
	.status = @"status",
};

const struct MatchRelationships MatchRelationships = {
	.teamLocal = @"teamLocal",
	.teamVisitor = @"teamVisitor",
	.watches = @"watches",
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
	
	if ([key isEqualToString:@"csys_birthValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_birth"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"csys_deletedValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_deleted"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"csys_modifiedValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_modified"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
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
	if ([key isEqualToString:@"idVisitorTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idVisitorTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"matchDateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"matchDate"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"statusValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"status"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
}




@dynamic csys_birth;



- (int64_t)csys_birthValue {
	NSNumber *result = [self csys_birth];
	return [result longLongValue];
}

- (void)setCsys_birthValue:(int64_t)value_ {
	[self setCsys_birth:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveCsys_birthValue {
	NSNumber *result = [self primitiveCsys_birth];
	return [result longLongValue];
}

- (void)setPrimitiveCsys_birthValue:(int64_t)value_ {
	[self setPrimitiveCsys_birth:[NSNumber numberWithLongLong:value_]];
}





@dynamic csys_deleted;



- (int64_t)csys_deletedValue {
	NSNumber *result = [self csys_deleted];
	return [result longLongValue];
}

- (void)setCsys_deletedValue:(int64_t)value_ {
	[self setCsys_deleted:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveCsys_deletedValue {
	NSNumber *result = [self primitiveCsys_deleted];
	return [result longLongValue];
}

- (void)setPrimitiveCsys_deletedValue:(int64_t)value_ {
	[self setPrimitiveCsys_deleted:[NSNumber numberWithLongLong:value_]];
}





@dynamic csys_modified;



- (int64_t)csys_modifiedValue {
	NSNumber *result = [self csys_modified];
	return [result longLongValue];
}

- (void)setCsys_modifiedValue:(int64_t)value_ {
	[self setCsys_modified:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveCsys_modifiedValue {
	NSNumber *result = [self primitiveCsys_modified];
	return [result longLongValue];
}

- (void)setPrimitiveCsys_modifiedValue:(int64_t)value_ {
	[self setPrimitiveCsys_modified:[NSNumber numberWithLongLong:value_]];
}





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





@dynamic teamLocal;

	

@dynamic teamVisitor;

	

@dynamic watches;

	
- (NSMutableSet*)watchesSet {
	[self willAccessValueForKey:@"watches"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"watches"];
  
	[self didAccessValueForKey:@"watches"];
	return result;
}
	






@end
