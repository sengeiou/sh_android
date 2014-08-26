// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Player.m instead.

#import "_Player.h"

const struct PlayerAttributes PlayerAttributes = {
	.anonymousUser = @"anonymousUser",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.displayName = @"displayName",
	.eMail = @"eMail",
	.favoriteTeam = @"favoriteTeam",
	.idPlayer = @"idPlayer",
	.index = @"index",
	.phoneNumber = @"phoneNumber",
	.photoTimeStamp = @"photoTimeStamp",
	.photoUrl = @"photoUrl",
	.pointsTotalAvailable = @"pointsTotalAvailable",
	.pointsWon = @"pointsWon",
	.sessionFacebook = @"sessionFacebook",
	.tokenFacebook = @"tokenFacebook",
	.userName = @"userName",
};

const struct PlayerRelationships PlayerRelationships = {
	.device = @"device",
	.playerProvider = @"playerProvider",
};

const struct PlayerFetchedProperties PlayerFetchedProperties = {
};

@implementation PlayerID
@end

@implementation _Player

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Player" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Player";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Player" inManagedObjectContext:moc_];
}

- (PlayerID*)objectID {
	return (PlayerID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"anonymousUserValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"anonymousUser"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"favoriteTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"favoriteTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idPlayerValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idPlayer"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"indexValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"index"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"photoTimeStampValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"photoTimeStamp"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"pointsTotalAvailableValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"pointsTotalAvailable"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"pointsWonValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"pointsWon"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"sessionFacebookValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"sessionFacebook"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
}




@dynamic anonymousUser;



- (BOOL)anonymousUserValue {
	NSNumber *result = [self anonymousUser];
	return [result boolValue];
}

- (void)setAnonymousUserValue:(BOOL)value_ {
	[self setAnonymousUser:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveAnonymousUserValue {
	NSNumber *result = [self primitiveAnonymousUser];
	return [result boolValue];
}

- (void)setPrimitiveAnonymousUserValue:(BOOL)value_ {
	[self setPrimitiveAnonymousUser:[NSNumber numberWithBool:value_]];
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






@dynamic displayName;






@dynamic eMail;






@dynamic favoriteTeam;



- (int64_t)favoriteTeamValue {
	NSNumber *result = [self favoriteTeam];
	return [result longLongValue];
}

- (void)setFavoriteTeamValue:(int64_t)value_ {
	[self setFavoriteTeam:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveFavoriteTeamValue {
	NSNumber *result = [self primitiveFavoriteTeam];
	return [result longLongValue];
}

- (void)setPrimitiveFavoriteTeamValue:(int64_t)value_ {
	[self setPrimitiveFavoriteTeam:[NSNumber numberWithLongLong:value_]];
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





@dynamic index;



- (int64_t)indexValue {
	NSNumber *result = [self index];
	return [result longLongValue];
}

- (void)setIndexValue:(int64_t)value_ {
	[self setIndex:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIndexValue {
	NSNumber *result = [self primitiveIndex];
	return [result longLongValue];
}

- (void)setPrimitiveIndexValue:(int64_t)value_ {
	[self setPrimitiveIndex:[NSNumber numberWithLongLong:value_]];
}





@dynamic phoneNumber;






@dynamic photoTimeStamp;



- (int64_t)photoTimeStampValue {
	NSNumber *result = [self photoTimeStamp];
	return [result longLongValue];
}

- (void)setPhotoTimeStampValue:(int64_t)value_ {
	[self setPhotoTimeStamp:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitivePhotoTimeStampValue {
	NSNumber *result = [self primitivePhotoTimeStamp];
	return [result longLongValue];
}

- (void)setPrimitivePhotoTimeStampValue:(int64_t)value_ {
	[self setPrimitivePhotoTimeStamp:[NSNumber numberWithLongLong:value_]];
}





@dynamic photoUrl;






@dynamic pointsTotalAvailable;



- (int32_t)pointsTotalAvailableValue {
	NSNumber *result = [self pointsTotalAvailable];
	return [result intValue];
}

- (void)setPointsTotalAvailableValue:(int32_t)value_ {
	[self setPointsTotalAvailable:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitivePointsTotalAvailableValue {
	NSNumber *result = [self primitivePointsTotalAvailable];
	return [result intValue];
}

- (void)setPrimitivePointsTotalAvailableValue:(int32_t)value_ {
	[self setPrimitivePointsTotalAvailable:[NSNumber numberWithInt:value_]];
}





@dynamic pointsWon;



- (int32_t)pointsWonValue {
	NSNumber *result = [self pointsWon];
	return [result intValue];
}

- (void)setPointsWonValue:(int32_t)value_ {
	[self setPointsWon:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitivePointsWonValue {
	NSNumber *result = [self primitivePointsWon];
	return [result intValue];
}

- (void)setPrimitivePointsWonValue:(int32_t)value_ {
	[self setPrimitivePointsWon:[NSNumber numberWithInt:value_]];
}





@dynamic sessionFacebook;



- (BOOL)sessionFacebookValue {
	NSNumber *result = [self sessionFacebook];
	return [result boolValue];
}

- (void)setSessionFacebookValue:(BOOL)value_ {
	[self setSessionFacebook:[NSNumber numberWithBool:value_]];
}

- (BOOL)primitiveSessionFacebookValue {
	NSNumber *result = [self primitiveSessionFacebook];
	return [result boolValue];
}

- (void)setPrimitiveSessionFacebookValue:(BOOL)value_ {
	[self setPrimitiveSessionFacebook:[NSNumber numberWithBool:value_]];
}





@dynamic tokenFacebook;






@dynamic userName;






@dynamic device;

	

@dynamic playerProvider;

	
- (NSMutableSet*)playerProviderSet {
	[self willAccessValueForKey:@"playerProvider"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"playerProvider"];
  
	[self didAccessValueForKey:@"playerProvider"];
	return result;
}
	






@end
