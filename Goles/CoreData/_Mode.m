// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Mode.m instead.

#import "_Mode.h"

const struct ModeAttributes ModeAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idMode = @"idMode",
	.name = @"name",
	.order = @"order",
};

const struct ModeRelationships ModeRelationships = {
	.device = @"device",
	.teams = @"teams",
	.tournaments = @"tournaments",
};

const struct ModeFetchedProperties ModeFetchedProperties = {
};

@implementation ModeID
@end

@implementation _Mode

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Mode" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Mode";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Mode" inManagedObjectContext:moc_];
}

- (ModeID*)objectID {
	return (ModeID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idModeValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idMode"];
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






@dynamic idMode;



- (int64_t)idModeValue {
	NSNumber *result = [self idMode];
	return [result longLongValue];
}

- (void)setIdModeValue:(int64_t)value_ {
	[self setIdMode:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdModeValue {
	NSNumber *result = [self primitiveIdMode];
	return [result longLongValue];
}

- (void)setPrimitiveIdModeValue:(int64_t)value_ {
	[self setPrimitiveIdMode:[NSNumber numberWithLongLong:value_]];
}





@dynamic name;






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





@dynamic device;

	

@dynamic teams;

	
- (NSMutableSet*)teamsSet {
	[self willAccessValueForKey:@"teams"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"teams"];
  
	[self didAccessValueForKey:@"teams"];
	return result;
}
	

@dynamic tournaments;

	
- (NSMutableSet*)tournamentsSet {
	[self willAccessValueForKey:@"tournaments"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"tournaments"];
  
	[self didAccessValueForKey:@"tournaments"];
	return result;
}
	






@end
