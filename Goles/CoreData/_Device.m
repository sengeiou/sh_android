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
	.idUser = @"idUser",
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
	.user = @"user",
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
	if ([key isEqualToString:@"idDeviceValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idDevice"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idUserValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idUser"];
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





@dynamic idUser;



- (int64_t)idUserValue {
	NSNumber *result = [self idUser];
	return [result longLongValue];
}

- (void)setIdUserValue:(int64_t)value_ {
	[self setIdUser:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdUserValue {
	NSNumber *result = [self primitiveIdUser];
	return [result longLongValue];
}

- (void)setPrimitiveIdUserValue:(int64_t)value_ {
	[self setPrimitiveIdUser:[NSNumber numberWithLongLong:value_]];
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






@dynamic user;

	






@end
