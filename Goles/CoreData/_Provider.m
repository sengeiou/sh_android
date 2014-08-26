// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Provider.m instead.

#import "_Provider.h"

const struct ProviderAttributes ProviderAttributes = {
	.activePorra = @"activePorra",
	.comment = @"comment",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.disclaimer = @"disclaimer",
	.idProvider = @"idProvider",
	.name = @"name",
	.registryURL = @"registryURL",
	.uniqueKey = @"uniqueKey",
	.visibleIOS = @"visibleIOS",
	.weight = @"weight",
};

const struct ProviderRelationships ProviderRelationships = {
	.matchBetType = @"matchBetType",
	.playerProvider = @"playerProvider",
};

const struct ProviderFetchedProperties ProviderFetchedProperties = {
};

@implementation ProviderID
@end

@implementation _Provider

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Provider" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Provider";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Provider" inManagedObjectContext:moc_];
}

- (ProviderID*)objectID {
	return (ProviderID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"activePorraValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"activePorra"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idProviderValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idProvider"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"visibleIOSValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"visibleIOS"];
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




@dynamic activePorra;



- (BOOL)activePorraValue {
	NSNumber *result = [self activePorra];
	return [result boolValue];
}

- (void)setActivePorraValue:(BOOL)value_ {
	[self setActivePorra:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveActivePorraValue {
	NSNumber *result = [self primitiveActivePorra];
	return [result boolValue];
}

- (void)setPrimitiveActivePorraValue:(BOOL)value_ {
	[self setPrimitiveActivePorra:[NSNumber numberWithBool:value_]];
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






@dynamic disclaimer;






@dynamic idProvider;



- (int64_t)idProviderValue {
	NSNumber *result = [self idProvider];
	return [result longLongValue];
}

- (void)setIdProviderValue:(int64_t)value_ {
	[self setIdProvider:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdProviderValue {
	NSNumber *result = [self primitiveIdProvider];
	return [result longLongValue];
}

- (void)setPrimitiveIdProviderValue:(int64_t)value_ {
	[self setPrimitiveIdProvider:[NSNumber numberWithLongLong:value_]];
}





@dynamic name;






@dynamic registryURL;






@dynamic uniqueKey;






@dynamic visibleIOS;



- (BOOL)visibleIOSValue {
	NSNumber *result = [self visibleIOS];
	return [result boolValue];
}

- (void)setVisibleIOSValue:(BOOL)value_ {
	[self setVisibleIOS:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveVisibleIOSValue {
	NSNumber *result = [self primitiveVisibleIOS];
	return [result boolValue];
}

- (void)setPrimitiveVisibleIOSValue:(BOOL)value_ {
	[self setPrimitiveVisibleIOS:[NSNumber numberWithBool:value_]];
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





@dynamic matchBetType;

	
- (NSMutableSet*)matchBetTypeSet {
	[self willAccessValueForKey:@"matchBetType"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"matchBetType"];
  
	[self didAccessValueForKey:@"matchBetType"];
	return result;
}
	

@dynamic playerProvider;

	
- (NSMutableSet*)playerProviderSet {
	[self willAccessValueForKey:@"playerProvider"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"playerProvider"];
  
	[self didAccessValueForKey:@"playerProvider"];
	return result;
}
	






@end
