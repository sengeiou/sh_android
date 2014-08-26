// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Subscription.m instead.

#import "_Subscription.h"

const struct SubscriptionAttributes SubscriptionAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idAllEvents = @"idAllEvents",
	.idDevice = @"idDevice",
	.idSubscription = @"idSubscription",
	.negation = @"negation",
	.tipus = @"tipus",
};

const struct SubscriptionRelationships SubscriptionRelationships = {
	.match = @"match",
	.sml = @"sml",
	.team = @"team",
};

const struct SubscriptionFetchedProperties SubscriptionFetchedProperties = {
};

@implementation SubscriptionID
@end

@implementation _Subscription

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Subscription" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Subscription";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Subscription" inManagedObjectContext:moc_];
}

- (SubscriptionID*)objectID {
	return (SubscriptionID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idAllEventsValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idAllEvents"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idDeviceValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idDevice"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idSubscriptionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idSubscription"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"negationValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"negation"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"tipusValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"tipus"];
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






@dynamic idAllEvents;



- (int32_t)idAllEventsValue {
	NSNumber *result = [self idAllEvents];
	return [result intValue];
}

- (void)setIdAllEventsValue:(int32_t)value_ {
	[self setIdAllEvents:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdAllEventsValue {
	NSNumber *result = [self primitiveIdAllEvents];
	return [result intValue];
}

- (void)setPrimitiveIdAllEventsValue:(int32_t)value_ {
	[self setPrimitiveIdAllEvents:[NSNumber numberWithInt:value_]];
}





@dynamic idDevice;



- (int64_t)idDeviceValue {
	NSNumber *result = [self idDevice];
	return [result longLongValue];
}

- (void)setIdDeviceValue:(int64_t)value_ {
	[self setIdDevice:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdDeviceValue {
	NSNumber *result = [self primitiveIdDevice];
	return [result longLongValue];
}

- (void)setPrimitiveIdDeviceValue:(int64_t)value_ {
	[self setPrimitiveIdDevice:[NSNumber numberWithLongLong:value_]];
}





@dynamic idSubscription;



- (int64_t)idSubscriptionValue {
	NSNumber *result = [self idSubscription];
	return [result longLongValue];
}

- (void)setIdSubscriptionValue:(int64_t)value_ {
	[self setIdSubscription:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdSubscriptionValue {
	NSNumber *result = [self primitiveIdSubscription];
	return [result longLongValue];
}

- (void)setPrimitiveIdSubscriptionValue:(int64_t)value_ {
	[self setPrimitiveIdSubscription:[NSNumber numberWithLongLong:value_]];
}





@dynamic negation;



- (BOOL)negationValue {
	NSNumber *result = [self negation];
	return [result boolValue];
}

- (void)setNegationValue:(BOOL)value_ {
	[self setNegation:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveNegationValue {
	NSNumber *result = [self primitiveNegation];
	return [result boolValue];
}

- (void)setPrimitiveNegationValue:(BOOL)value_ {
	[self setPrimitiveNegation:[NSNumber numberWithBool:value_]];
}





@dynamic tipus;



- (int32_t)tipusValue {
	NSNumber *result = [self tipus];
	return [result intValue];
}

- (void)setTipusValue:(int32_t)value_ {
	[self setTipus:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveTipusValue {
	NSNumber *result = [self primitiveTipus];
	return [result intValue];
}

- (void)setPrimitiveTipusValue:(int32_t)value_ {
	[self setPrimitiveTipus:[NSNumber numberWithInt:value_]];
}





@dynamic match;

	

@dynamic sml;

	

@dynamic team;

	






@end
