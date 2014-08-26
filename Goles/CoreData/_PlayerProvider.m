// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to PlayerProvider.m instead.

#import "_PlayerProvider.h"

const struct PlayerProviderAttributes PlayerProviderAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idPlayerProvider = @"idPlayerProvider",
	.status = @"status",
	.visible = @"visible",
	.weight = @"weight",
};

const struct PlayerProviderRelationships PlayerProviderRelationships = {
	.player = @"player",
	.provider = @"provider",
};

const struct PlayerProviderFetchedProperties PlayerProviderFetchedProperties = {
};

@implementation PlayerProviderID
@end

@implementation _PlayerProvider

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"PlayerProvider" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"PlayerProvider";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"PlayerProvider" inManagedObjectContext:moc_];
}

- (PlayerProviderID*)objectID {
	return (PlayerProviderID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idPlayerProviderValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idPlayerProvider"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"statusValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"status"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"visibleValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"visible"];
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






@dynamic idPlayerProvider;



- (int64_t)idPlayerProviderValue {
	NSNumber *result = [self idPlayerProvider];
	return [result longLongValue];
}

- (void)setIdPlayerProviderValue:(int64_t)value_ {
	[self setIdPlayerProvider:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdPlayerProviderValue {
	NSNumber *result = [self primitiveIdPlayerProvider];
	return [result longLongValue];
}

- (void)setPrimitiveIdPlayerProviderValue:(int64_t)value_ {
	[self setPrimitiveIdPlayerProvider:[NSNumber numberWithLongLong:value_]];
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





@dynamic visible;



- (BOOL)visibleValue {
	NSNumber *result = [self visible];
	return [result boolValue];
}

- (void)setVisibleValue:(BOOL)value_ {
	[self setVisible:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveVisibleValue {
	NSNumber *result = [self primitiveVisible];
	return [result boolValue];
}

- (void)setPrimitiveVisibleValue:(BOOL)value_ {
	[self setPrimitiveVisible:[NSNumber numberWithBool:value_]];
}





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





@dynamic player;

	

@dynamic provider;

	






@end
