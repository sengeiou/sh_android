// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to SyncControl.m instead.

#import "_SyncControl.h"

const struct SyncControlAttributes SyncControlAttributes = {
	.aliasView = @"aliasView",
	.lastCall = @"lastCall",
	.lastServerDate = @"lastServerDate",
	.nameEntity = @"nameEntity",
	.updatePriority = @"updatePriority",
};

@implementation SyncControlID
@end

@implementation _SyncControl

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"SyncControl" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"SyncControl";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"SyncControl" inManagedObjectContext:moc_];
}

- (SyncControlID*)objectID {
	return (SyncControlID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];

	if ([key isEqualToString:@"lastCallValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"lastCall"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"lastServerDateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"lastServerDate"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"updatePriorityValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"updatePriority"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
}

@dynamic aliasView;

@dynamic lastCall;

- (int64_t)lastCallValue {
	NSNumber *result = [self lastCall];
	return [result longLongValue];
}

- (void)setLastCallValue:(int64_t)value_ {
	[self setLastCall:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveLastCallValue {
	NSNumber *result = [self primitiveLastCall];
	return [result longLongValue];
}

- (void)setPrimitiveLastCallValue:(int64_t)value_ {
	[self setPrimitiveLastCall:[NSNumber numberWithLongLong:value_]];
}

@dynamic lastServerDate;

- (int64_t)lastServerDateValue {
	NSNumber *result = [self lastServerDate];
	return [result longLongValue];
}

- (void)setLastServerDateValue:(int64_t)value_ {
	[self setLastServerDate:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveLastServerDateValue {
	NSNumber *result = [self primitiveLastServerDate];
	return [result longLongValue];
}

- (void)setPrimitiveLastServerDateValue:(int64_t)value_ {
	[self setPrimitiveLastServerDate:[NSNumber numberWithLongLong:value_]];
}

@dynamic nameEntity;

@dynamic updatePriority;

- (int64_t)updatePriorityValue {
	NSNumber *result = [self updatePriority];
	return [result longLongValue];
}

- (void)setUpdatePriorityValue:(int64_t)value_ {
	[self setUpdatePriority:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveUpdatePriorityValue {
	NSNumber *result = [self primitiveUpdatePriority];
	return [result longLongValue];
}

- (void)setPrimitiveUpdatePriorityValue:(int64_t)value_ {
	[self setPrimitiveUpdatePriority:[NSNumber numberWithLongLong:value_]];
}

@end

