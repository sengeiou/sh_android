// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to User.m instead.

#import "_User.h"

const struct UserAttributes UserAttributes = {
	.bio = @"bio",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.eMail = @"eMail",
	.idFavouriteTeam = @"idFavouriteTeam",
	.idUser = @"idUser",
	.name = @"name",
	.numFollowers = @"numFollowers",
	.numFollowing = @"numFollowing",
	.photo = @"photo",
	.points = @"points",
	.rank = @"rank",
	.sessionToken = @"sessionToken",
	.userName = @"userName",
	.website = @"website",
};

const struct UserRelationships UserRelationships = {
	.device = @"device",
	.shots = @"shots",
	.team = @"team",
};

const struct UserFetchedProperties UserFetchedProperties = {
};

@implementation UserID
@end

@implementation _User

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"User" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"User";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"User" inManagedObjectContext:moc_];
}

- (UserID*)objectID {
	return (UserID*)[super objectID];
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
	if ([key isEqualToString:@"idFavouriteTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idFavouriteTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idUserValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idUser"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"numFollowersValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"numFollowers"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"numFollowingValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"numFollowing"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"pointsValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"points"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"rankValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"rank"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
}




@dynamic bio;






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






@dynamic eMail;






@dynamic idFavouriteTeam;



- (int64_t)idFavouriteTeamValue {
	NSNumber *result = [self idFavouriteTeam];
	return [result longLongValue];
}

- (void)setIdFavouriteTeamValue:(int64_t)value_ {
	[self setIdFavouriteTeam:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdFavouriteTeamValue {
	NSNumber *result = [self primitiveIdFavouriteTeam];
	return [result longLongValue];
}

- (void)setPrimitiveIdFavouriteTeamValue:(int64_t)value_ {
	[self setPrimitiveIdFavouriteTeam:[NSNumber numberWithLongLong:value_]];
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





@dynamic name;






@dynamic numFollowers;



- (int32_t)numFollowersValue {
	NSNumber *result = [self numFollowers];
	return [result intValue];
}

- (void)setNumFollowersValue:(int32_t)value_ {
	[self setNumFollowers:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveNumFollowersValue {
	NSNumber *result = [self primitiveNumFollowers];
	return [result intValue];
}

- (void)setPrimitiveNumFollowersValue:(int32_t)value_ {
	[self setPrimitiveNumFollowers:[NSNumber numberWithInt:value_]];
}





@dynamic numFollowing;



- (int32_t)numFollowingValue {
	NSNumber *result = [self numFollowing];
	return [result intValue];
}

- (void)setNumFollowingValue:(int32_t)value_ {
	[self setNumFollowing:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveNumFollowingValue {
	NSNumber *result = [self primitiveNumFollowing];
	return [result intValue];
}

- (void)setPrimitiveNumFollowingValue:(int32_t)value_ {
	[self setPrimitiveNumFollowing:[NSNumber numberWithInt:value_]];
}





@dynamic photo;






@dynamic points;



- (int64_t)pointsValue {
	NSNumber *result = [self points];
	return [result longLongValue];
}

- (void)setPointsValue:(int64_t)value_ {
	[self setPoints:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitivePointsValue {
	NSNumber *result = [self primitivePoints];
	return [result longLongValue];
}

- (void)setPrimitivePointsValue:(int64_t)value_ {
	[self setPrimitivePoints:[NSNumber numberWithLongLong:value_]];
}





@dynamic rank;



- (int64_t)rankValue {
	NSNumber *result = [self rank];
	return [result longLongValue];
}

- (void)setRankValue:(int64_t)value_ {
	[self setRank:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveRankValue {
	NSNumber *result = [self primitiveRank];
	return [result longLongValue];
}

- (void)setPrimitiveRankValue:(int64_t)value_ {
	[self setPrimitiveRank:[NSNumber numberWithLongLong:value_]];
}





@dynamic sessionToken;






@dynamic userName;






@dynamic website;






@dynamic device;

	

@dynamic shots;

	
- (NSMutableSet*)shotsSet {
	[self willAccessValueForKey:@"shots"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"shots"];
  
	[self didAccessValueForKey:@"shots"];
	return result;
}
	

@dynamic team;

	






@end
