// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to MatchBetType.m instead.

#import "_MatchBetType.h"

const struct MatchBetTypeAttributes MatchBetTypeAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idMatchBetType = @"idMatchBetType",
};

const struct MatchBetTypeRelationships MatchBetTypeRelationships = {
	.betType = @"betType",
	.betTypeOdds = @"betTypeOdds",
	.match = @"match",
	.provider = @"provider",
};

const struct MatchBetTypeFetchedProperties MatchBetTypeFetchedProperties = {
};

@implementation MatchBetTypeID
@end

@implementation _MatchBetType

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"MatchBetType" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"MatchBetType";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"MatchBetType" inManagedObjectContext:moc_];
}

- (MatchBetTypeID*)objectID {
	return (MatchBetTypeID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idMatchBetTypeValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idMatchBetType"];
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






@dynamic idMatchBetType;



- (int64_t)idMatchBetTypeValue {
	NSNumber *result = [self idMatchBetType];
	return [result longLongValue];
}

- (void)setIdMatchBetTypeValue:(int64_t)value_ {
	[self setIdMatchBetType:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdMatchBetTypeValue {
	NSNumber *result = [self primitiveIdMatchBetType];
	return [result longLongValue];
}

- (void)setPrimitiveIdMatchBetTypeValue:(int64_t)value_ {
	[self setPrimitiveIdMatchBetType:[NSNumber numberWithLongLong:value_]];
}





@dynamic betType;

	

@dynamic betTypeOdds;

	
- (NSMutableSet*)betTypeOddsSet {
	[self willAccessValueForKey:@"betTypeOdds"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"betTypeOdds"];
  
	[self didAccessValueForKey:@"betTypeOdds"];
	return result;
}
	

@dynamic match;

	

@dynamic provider;

	






@end
