// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Device.m instead.

#import "_Device.h"

const struct DeviceAttributes DeviceAttributes = {
	.appVer = @"appVer",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idDevice = @"idDevice",
	.idPlayer = @"idPlayer",
	.language = @"language",
	.locale = @"locale",
	.model = @"model",
	.osVer = @"osVer",
	.platform = @"platform",
	.status = @"status",
	.timeZone = @"timeZone",
	.token = @"token",
};

const struct DeviceRelationships DeviceRelationships = {
	.modes = @"modes",
	.player = @"player",
};

const struct DeviceFetchedProperties DeviceFetchedProperties = {
};

@implementation DeviceID
@end

@implementation _Device

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Device" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Device";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Device" inManagedObjectContext:moc_];
}

- (DeviceID*)objectID {
	return (DeviceID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idDeviceValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idDevice"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idPlayerValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idPlayer"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"statusValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"status"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
}




@dynamic appVer;






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





@dynamic idPlayer;



- (int64_t)idPlayerValue {
	NSNumber *result = [self idPlayer];
	return [result longLongValue];
}

- (void)setIdPlayerValue:(int64_t)value_ {
	[self setIdPlayer:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdPlayerValue {
	NSNumber *result = [self primitiveIdPlayer];
	return [result longLongValue];
}

- (void)setPrimitiveIdPlayerValue:(int64_t)value_ {
	[self setPrimitiveIdPlayer:[NSNumber numberWithLongLong:value_]];
}





@dynamic language;






@dynamic locale;






@dynamic model;






@dynamic osVer;






@dynamic platform;






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





@dynamic timeZone;






@dynamic token;






@dynamic modes;

	
- (NSMutableSet*)modesSet {
	[self willAccessValueForKey:@"modes"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"modes"];
  
	[self didAccessValueForKey:@"modes"];
	return result;
}
	

@dynamic player;

	






@end
