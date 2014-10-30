// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to SML.m instead.

#import "_SML.h"

const struct SMLAttributes SMLAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idSML = @"idSML",
	.language = @"language",
	.message = @"message",
	.sound = @"sound",
};

const struct SMLRelationships SMLRelationships = {
};

const struct SMLFetchedProperties SMLFetchedProperties = {
};

@implementation SMLID
@end

@implementation _SML

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"SML" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"SML";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"SML" inManagedObjectContext:moc_];
}

- (SMLID*)objectID {
	return (SMLID*)[super objectID];
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
	if ([key isEqualToString:@"idSMLValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idSML"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"languageValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"language"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"messageValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"message"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"soundValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"sound"];
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






@dynamic idSML;



- (int64_t)idSMLValue {
	NSNumber *result = [self idSML];
	return [result longLongValue];
}

- (void)setIdSMLValue:(int64_t)value_ {
	[self setIdSML:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdSMLValue {
	NSNumber *result = [self primitiveIdSML];
	return [result longLongValue];
}

- (void)setPrimitiveIdSMLValue:(int64_t)value_ {
	[self setPrimitiveIdSML:[NSNumber numberWithLongLong:value_]];
}





@dynamic language;



- (int16_t)languageValue {
	NSNumber *result = [self language];
	return [result shortValue];
}

- (void)setLanguageValue:(int16_t)value_ {
	[self setLanguage:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveLanguageValue {
	NSNumber *result = [self primitiveLanguage];
	return [result shortValue];
}

- (void)setPrimitiveLanguageValue:(int16_t)value_ {
	[self setPrimitiveLanguage:[NSNumber numberWithShort:value_]];
}





@dynamic message;



- (int16_t)messageValue {
	NSNumber *result = [self message];
	return [result shortValue];
}

- (void)setMessageValue:(int16_t)value_ {
	[self setMessage:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveMessageValue {
	NSNumber *result = [self primitiveMessage];
	return [result shortValue];
}

- (void)setPrimitiveMessageValue:(int16_t)value_ {
	[self setPrimitiveMessage:[NSNumber numberWithShort:value_]];
}





@dynamic sound;



- (int16_t)soundValue {
	NSNumber *result = [self sound];
	return [result shortValue];
}

- (void)setSoundValue:(int16_t)value_ {
	[self setSound:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveSoundValue {
	NSNumber *result = [self primitiveSound];
	return [result shortValue];
}

- (void)setPrimitiveSoundValue:(int16_t)value_ {
	[self setPrimitiveSound:[NSNumber numberWithShort:value_]];
}










@end
