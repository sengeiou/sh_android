// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to BetType.m instead.

#import "_BetType.h"

const struct BetTypeAttributes BetTypeAttributes = {
	.alwaysVisible = @"alwaysVisible",
	.comment = @"comment",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idBetType = @"idBetType",
	.name = @"name",
	.title = @"title",
	.uniqueKey = @"uniqueKey",
	.weight = @"weight",
};

const struct BetTypeRelationships BetTypeRelationships = {
	.matchBetType = @"matchBetType",
};

const struct BetTypeFetchedProperties BetTypeFetchedProperties = {
};

@implementation BetTypeID
@end

@implementation _BetType

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"BetType" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"BetType";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"BetType" inManagedObjectContext:moc_];
}

- (BetTypeID*)objectID {
	return (BetTypeID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"alwaysVisibleValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"alwaysVisible"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idBetTypeValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idBetType"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"weightValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"weight"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
}




@dynamic alwaysVisible;



- (BOOL)alwaysVisibleValue {
	NSNumber *result = [self alwaysVisible];
	return [result boolValue];
}

- (void)setAlwaysVisibleValue:(BOOL)value_ {
	[self setAlwaysVisible:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveAlwaysVisibleValue {
	NSNumber *result = [self primitiveAlwaysVisible];
	return [result boolValue];
}

- (void)setPrimitiveAlwaysVisibleValue:(BOOL)value_ {
	[self setPrimitiveAlwaysVisible:[NSNumber numberWithBool:value_]];
}





@dynamic comment;






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






@dynamic idBetType;



- (int64_t)idBetTypeValue {
	NSNumber *result = [self idBetType];
	return [result longLongValue];
}

- (void)setIdBetTypeValue:(int64_t)value_ {
	[self setIdBetType:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdBetTypeValue {
	NSNumber *result = [self primitiveIdBetType];
	return [result longLongValue];
}

- (void)setPrimitiveIdBetTypeValue:(int64_t)value_ {
	[self setPrimitiveIdBetType:[NSNumber numberWithLongLong:value_]];
}





@dynamic name;






@dynamic title;






@dynamic uniqueKey;






@dynamic weight;



- (int16_t)weightValue {
	NSNumber *result = [self weight];
	return [result shortValue];
}

- (void)setWeightValue:(int16_t)value_ {
	[self setWeight:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveWeightValue {
	NSNumber *result = [self primitiveWeight];
	return [result shortValue];
}

- (void)setPrimitiveWeightValue:(int16_t)value_ {
	[self setPrimitiveWeight:[NSNumber numberWithShort:value_]];
}





@dynamic matchBetType;

	
- (NSMutableSet*)matchBetTypeSet {
	[self willAccessValueForKey:@"matchBetType"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"matchBetType"];
  
	[self didAccessValueForKey:@"matchBetType"];
	return result;
}
	






@end
