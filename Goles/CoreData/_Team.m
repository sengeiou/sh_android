// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Team.m instead.

#import "_Team.h"

const struct TeamAttributes TeamAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idTeam = @"idTeam",
	.isNationalTeam = @"isNationalTeam",
	.name = @"name",
	.nameShort = @"nameShort",
	.order = @"order",
	.urlImage = @"urlImage",
};

const struct TeamRelationships TeamRelationships = {
	.matchesAsLocal = @"matchesAsLocal",
	.matchesAsVisitor = @"matchesAsVisitor",
};

const struct TeamFetchedProperties TeamFetchedProperties = {
};

@implementation TeamID
@end

@implementation _Team

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Team" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Team";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Team" inManagedObjectContext:moc_];
}

- (TeamID*)objectID {
	return (TeamID*)[super objectID];
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
	if ([key isEqualToString:@"idTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"isNationalTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"isNationalTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"orderValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"order"];
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






@dynamic idTeam;



- (int64_t)idTeamValue {
	NSNumber *result = [self idTeam];
	return [result longLongValue];
}

- (void)setIdTeamValue:(int64_t)value_ {
	[self setIdTeam:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdTeamValue {
	NSNumber *result = [self primitiveIdTeam];
	return [result longLongValue];
}

- (void)setPrimitiveIdTeamValue:(int64_t)value_ {
	[self setPrimitiveIdTeam:[NSNumber numberWithLongLong:value_]];
}





@dynamic isNationalTeam;



- (BOOL)isNationalTeamValue {
	NSNumber *result = [self isNationalTeam];
	return [result boolValue];
}

- (void)setIsNationalTeamValue:(BOOL)value_ {
	[self setIsNationalTeam:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveIsNationalTeamValue {
	NSNumber *result = [self primitiveIsNationalTeam];
	return [result boolValue];
}

- (void)setPrimitiveIsNationalTeamValue:(BOOL)value_ {
	[self setPrimitiveIsNationalTeam:[NSNumber numberWithBool:value_]];
}





@dynamic name;






@dynamic nameShort;






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





@dynamic urlImage;






@dynamic matchesAsLocal;

	
- (NSMutableSet*)matchesAsLocalSet {
	[self willAccessValueForKey:@"matchesAsLocal"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"matchesAsLocal"];
  
	[self didAccessValueForKey:@"matchesAsLocal"];
	return result;
}
	

@dynamic matchesAsVisitor;

	
- (NSMutableSet*)matchesAsVisitorSet {
	[self willAccessValueForKey:@"matchesAsVisitor"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"matchesAsVisitor"];
  
	[self didAccessValueForKey:@"matchesAsVisitor"];
	return result;
}
	






@end
