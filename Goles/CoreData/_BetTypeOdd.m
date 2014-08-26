// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to BetTypeOdd.m instead.

#import "_BetTypeOdd.h"

const struct BetTypeOddAttributes BetTypeOddAttributes = {
	.comment = @"comment",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idBetTypeOdd = @"idBetTypeOdd",
	.idMatchBetType = @"idMatchBetType",
	.name = @"name",
	.readyToDelete = @"readyToDelete",
	.url = @"url",
	.value = @"value",
};

const struct BetTypeOddRelationships BetTypeOddRelationships = {
	.matchBetType = @"matchBetType",
};

const struct BetTypeOddFetchedProperties BetTypeOddFetchedProperties = {
};

@implementation BetTypeOddID
@end

@implementation _BetTypeOdd

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"BetTypeOdd" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"BetTypeOdd";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"BetTypeOdd" inManagedObjectContext:moc_];
}

- (BetTypeOddID*)objectID {
	return (BetTypeOddID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idBetTypeOddValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idBetTypeOdd"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idMatchBetTypeValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idMatchBetType"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"readyToDeleteValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"readyToDelete"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"valueValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"value"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
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






@dynamic idBetTypeOdd;



- (int64_t)idBetTypeOddValue {
	NSNumber *result = [self idBetTypeOdd];
	return [result longLongValue];
}

- (void)setIdBetTypeOddValue:(int64_t)value_ {
	[self setIdBetTypeOdd:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdBetTypeOddValue {
	NSNumber *result = [self primitiveIdBetTypeOdd];
	return [result longLongValue];
}

- (void)setPrimitiveIdBetTypeOddValue:(int64_t)value_ {
	[self setPrimitiveIdBetTypeOdd:[NSNumber numberWithLongLong:value_]];
}





@dynamic idMatchBetType;



- (int32_t)idMatchBetTypeValue {
	NSNumber *result = [self idMatchBetType];
	return [result intValue];
}

- (void)setIdMatchBetTypeValue:(int32_t)value_ {
	[self setIdMatchBetType:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdMatchBetTypeValue {
	NSNumber *result = [self primitiveIdMatchBetType];
	return [result intValue];
}

- (void)setPrimitiveIdMatchBetTypeValue:(int32_t)value_ {
	[self setPrimitiveIdMatchBetType:[NSNumber numberWithInt:value_]];
}





@dynamic name;






@dynamic readyToDelete;



- (BOOL)readyToDeleteValue {
	NSNumber *result = [self readyToDelete];
	return [result boolValue];
}

- (void)setReadyToDeleteValue:(BOOL)value_ {
	[self setReadyToDelete:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveReadyToDeleteValue {
	NSNumber *result = [self primitiveReadyToDelete];
	return [result boolValue];
}

- (void)setPrimitiveReadyToDeleteValue:(BOOL)value_ {
	[self setPrimitiveReadyToDelete:[NSNumber numberWithBool:value_]];
}





@dynamic url;






@dynamic value;



- (float)valueValue {
	NSNumber *result = [self value];
	return [result floatValue];
}

- (void)setValueValue:(float)value_ {
	[self setValue:[NSNumber numberWithFloat:value_]];
}

- (float)primitiveValueValue {
	NSNumber *result = [self primitiveValue];
	return [result floatValue];
}

- (void)setPrimitiveValueValue:(float)value_ {
	[self setPrimitiveValue:[NSNumber numberWithFloat:value_]];
}





@dynamic matchBetType;

	






@end
