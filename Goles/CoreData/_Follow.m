// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Follow.m instead.

#import "_Follow.h"

const struct FollowAttributes FollowAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idUser = @"idUser",
	.idUserFollowed = @"idUserFollowed",
};

const struct FollowRelationships FollowRelationships = {
};

const struct FollowFetchedProperties FollowFetchedProperties = {
};

@implementation FollowID
@end

@implementation _Follow

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Follow" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Follow";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Follow" inManagedObjectContext:moc_];
}

- (FollowID*)objectID {
	return (FollowID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idUserValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idUser"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idUserFollowedValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idUserFollowed"];
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





@dynamic idUserFollowed;



- (int64_t)idUserFollowedValue {
	NSNumber *result = [self idUserFollowed];
	return [result longLongValue];
}

- (void)setIdUserFollowedValue:(int64_t)value_ {
	[self setIdUserFollowed:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdUserFollowedValue {
	NSNumber *result = [self primitiveIdUserFollowed];
	return [result longLongValue];
}

- (void)setPrimitiveIdUserFollowedValue:(int64_t)value_ {
	[self setPrimitiveIdUserFollowed:[NSNumber numberWithLongLong:value_]];
}










@end
