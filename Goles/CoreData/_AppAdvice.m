// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to AppAdvice.m instead.

#import "_AppAdvice.h"

const struct AppAdviceAttributes AppAdviceAttributes = {
	.buttonAction = @"buttonAction",
	.buttonData = @"buttonData",
	.buttonTextId = @"buttonTextId",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.endDate = @"endDate",
	.endVersion = @"endVersion",
	.idAppAdvice = @"idAppAdvice",
	.idMessage = @"idMessage",
	.path = @"path",
	.platform = @"platform",
	.startDate = @"startDate",
	.startVersion = @"startVersion",
	.status = @"status",
	.visibleButton = @"visibleButton",
	.weight = @"weight",
};

const struct AppAdviceRelationships AppAdviceRelationships = {
	.message = @"message",
};

const struct AppAdviceFetchedProperties AppAdviceFetchedProperties = {
};

@implementation AppAdviceID
@end

@implementation _AppAdvice

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"AppAdvice" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"AppAdvice";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"AppAdvice" inManagedObjectContext:moc_];
}

- (AppAdviceID*)objectID {
	return (AppAdviceID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"buttonTextIdValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"buttonTextId"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"endVersionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"endVersion"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idAppAdviceValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idAppAdvice"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idMessageValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idMessage"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"platformValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"platform"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"startVersionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"startVersion"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"statusValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"status"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"visibleButtonValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"visibleButton"];
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




@dynamic buttonAction;






@dynamic buttonData;






@dynamic buttonTextId;



- (int32_t)buttonTextIdValue {
	NSNumber *result = [self buttonTextId];
	return [result intValue];
}

- (void)setButtonTextIdValue:(int32_t)value_ {
	[self setButtonTextId:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveButtonTextIdValue {
	NSNumber *result = [self primitiveButtonTextId];
	return [result intValue];
}

- (void)setPrimitiveButtonTextIdValue:(int32_t)value_ {
	[self setPrimitiveButtonTextId:[NSNumber numberWithInt:value_]];
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






@dynamic endVersion;



- (int32_t)endVersionValue {
	NSNumber *result = [self endVersion];
	return [result intValue];
}

- (void)setEndVersionValue:(int32_t)value_ {
	[self setEndVersion:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveEndVersionValue {
	NSNumber *result = [self primitiveEndVersion];
	return [result intValue];
}

- (void)setPrimitiveEndVersionValue:(int32_t)value_ {
	[self setPrimitiveEndVersion:[NSNumber numberWithInt:value_]];
}





@dynamic idAppAdvice;



- (int64_t)idAppAdviceValue {
	NSNumber *result = [self idAppAdvice];
	return [result longLongValue];
}

- (void)setIdAppAdviceValue:(int64_t)value_ {
	[self setIdAppAdvice:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdAppAdviceValue {
	NSNumber *result = [self primitiveIdAppAdvice];
	return [result longLongValue];
}

- (void)setPrimitiveIdAppAdviceValue:(int64_t)value_ {
	[self setPrimitiveIdAppAdvice:[NSNumber numberWithLongLong:value_]];
}





@dynamic idMessage;



- (int32_t)idMessageValue {
	NSNumber *result = [self idMessage];
	return [result intValue];
}

- (void)setIdMessageValue:(int32_t)value_ {
	[self setIdMessage:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdMessageValue {
	NSNumber *result = [self primitiveIdMessage];
	return [result intValue];
}

- (void)setPrimitiveIdMessageValue:(int32_t)value_ {
	[self setPrimitiveIdMessage:[NSNumber numberWithInt:value_]];
}





@dynamic path;






@dynamic platform;



- (int16_t)platformValue {
	NSNumber *result = [self platform];
	return [result shortValue];
}

- (void)setPlatformValue:(int16_t)value_ {
	[self setPlatform:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitivePlatformValue {
	NSNumber *result = [self primitivePlatform];
	return [result shortValue];
}

- (void)setPrimitivePlatformValue:(int16_t)value_ {
	[self setPrimitivePlatform:[NSNumber numberWithShort:value_]];
}





@dynamic startDate;






@dynamic startVersion;



- (int32_t)startVersionValue {
	NSNumber *result = [self startVersion];
	return [result intValue];
}

- (void)setStartVersionValue:(int32_t)value_ {
	[self setStartVersion:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveStartVersionValue {
	NSNumber *result = [self primitiveStartVersion];
	return [result intValue];
}

- (void)setPrimitiveStartVersionValue:(int32_t)value_ {
	[self setPrimitiveStartVersion:[NSNumber numberWithInt:value_]];
}





@dynamic status;



- (BOOL)statusValue {
	NSNumber *result = [self status];
	return [result boolValue];
}

- (void)setStatusValue:(BOOL)value_ {
	[self setStatus:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveStatusValue {
	NSNumber *result = [self primitiveStatus];
	return [result boolValue];
}

- (void)setPrimitiveStatusValue:(BOOL)value_ {
	[self setPrimitiveStatus:[NSNumber numberWithBool:value_]];
}





@dynamic visibleButton;



- (BOOL)visibleButtonValue {
	NSNumber *result = [self visibleButton];
	return [result boolValue];
}

- (void)setVisibleButtonValue:(BOOL)value_ {
	[self setVisibleButton:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveVisibleButtonValue {
	NSNumber *result = [self primitiveVisibleButton];
	return [result boolValue];
}

- (void)setPrimitiveVisibleButtonValue:(BOOL)value_ {
	[self setPrimitiveVisibleButton:[NSNumber numberWithBool:value_]];
}





@dynamic weight;



- (int32_t)weightValue {
	NSNumber *result = [self weight];
	return [result intValue];
}

- (void)setWeightValue:(int32_t)value_ {
	[self setWeight:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveWeightValue {
	NSNumber *result = [self primitiveWeight];
	return [result intValue];
}

- (void)setPrimitiveWeightValue:(int32_t)value_ {
	[self setPrimitiveWeight:[NSNumber numberWithInt:value_]];
}





@dynamic message;

	






@end
