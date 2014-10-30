// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Message.m instead.

#import "_Message.h"

const struct MessageAttributes MessageAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idMessage = @"idMessage",
	.locale = @"locale",
	.message = @"message",
	.platform = @"platform",
};

const struct MessageRelationships MessageRelationships = {
	.advice = @"advice",
};

@implementation MessageID
@end

@implementation _Message

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Message" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Message";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Message" inManagedObjectContext:moc_];
}

- (MessageID*)objectID {
	return (MessageID*)[super objectID];
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

@dynamic idMessage;

- (int64_t)idMessageValue {
	NSNumber *result = [self idMessage];
	return [result longLongValue];
}

- (void)setIdMessageValue:(int64_t)value_ {
	[self setIdMessage:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdMessageValue {
	NSNumber *result = [self primitiveIdMessage];
	return [result longLongValue];
}

- (void)setPrimitiveIdMessageValue:(int64_t)value_ {
	[self setPrimitiveIdMessage:[NSNumber numberWithLongLong:value_]];
}

@dynamic locale;

@dynamic message;

@dynamic platform;

- (int32_t)platformValue {
	NSNumber *result = [self platform];
	return [result intValue];
}

- (void)setPlatformValue:(int32_t)value_ {
	[self setPlatform:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitivePlatformValue {
	NSNumber *result = [self primitivePlatform];
	return [result intValue];
}

- (void)setPrimitivePlatformValue:(int32_t)value_ {
	[self setPrimitivePlatform:[NSNumber numberWithInt:value_]];
}

@dynamic advice;

- (NSMutableSet*)adviceSet {
	[self willAccessValueForKey:@"advice"];

	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"advice"];

	[self didAccessValueForKey:@"advice"];
	return result;
}

@end

