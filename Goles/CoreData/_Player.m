// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Player.m instead.

#import "_Player.h"

const struct PlayerAttributes PlayerAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.eMail = @"eMail",
	.idFavouriteTeam = @"idFavouriteTeam",
	.idUser = @"idUser",
	.name = @"name",
	.nick = @"nick",
	.password = @"password",
	.photo = @"photo",
	.sessionToken = @"sessionToken",
};

const struct PlayerRelationships PlayerRelationships = {
	.device = @"device",
};

const struct PlayerFetchedProperties PlayerFetchedProperties = {
};

@implementation PlayerID
@end

@implementation _Player

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

- (PlayerID*)objectID {
	return (PlayerID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
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






@dynamic nick;






@dynamic password;






@dynamic photo;






@dynamic sessionToken;






@dynamic device;

	






@end
