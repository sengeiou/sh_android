// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Shot.m instead.

#import "_Shot.h"

const struct ShotAttributes ShotAttributes = {
	.comment = @"comment",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idShot = @"idShot",
};

const struct ShotRelationships ShotRelationships = {
	.user = @"user",
};

@implementation ShotID
@end

@implementation _Shot

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Shot" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Shot";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Shot" inManagedObjectContext:moc_];
}

- (ShotID*)objectID {
	return (ShotID*)[super objectID];
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
	if ([key isEqualToString:@"idShotValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idShot"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
}

@dynamic comment;

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

@dynamic idShot;

- (int64_t)idShotValue {
	NSNumber *result = [self idShot];
	return [result longLongValue];
}

- (void)setIdShotValue:(int64_t)value_ {
	[self setIdShot:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdShotValue {
	NSNumber *result = [self primitiveIdShot];
	return [result longLongValue];
}

- (void)setPrimitiveIdShotValue:(int64_t)value_ {
	[self setPrimitiveIdShot:[NSNumber numberWithLongLong:value_]];
}

@dynamic user;

@end

