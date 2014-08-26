// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to LineUp.m instead.

#import "_LineUp.h"

const struct LineUpAttributes LineUpAttributes = {
	.coach = @"coach",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.defenders = @"defenders",
	.formation = @"formation",
	.goalkeeper = @"goalkeeper",
	.idLineUp = @"idLineUp",
	.midfielder01 = @"midfielder01",
	.midfielder02 = @"midfielder02",
	.reserve = @"reserve",
	.striker = @"striker",
};

const struct LineUpRelationships LineUpRelationships = {
	.matchAsLocal = @"matchAsLocal",
	.matchAsVisitor = @"matchAsVisitor",
	.team = @"team",
};

const struct LineUpFetchedProperties LineUpFetchedProperties = {
};

@implementation LineUpID
@end

@implementation _LineUp

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"LineUp" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"LineUp";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"LineUp" inManagedObjectContext:moc_];
}

- (LineUpID*)objectID {
	return (LineUpID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idLineUpValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idLineUp"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
}




@dynamic coach;






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






@dynamic defenders;






@dynamic formation;






@dynamic goalkeeper;






@dynamic idLineUp;



- (int64_t)idLineUpValue {
	NSNumber *result = [self idLineUp];
	return [result longLongValue];
}

- (void)setIdLineUpValue:(int64_t)value_ {
	[self setIdLineUp:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdLineUpValue {
	NSNumber *result = [self primitiveIdLineUp];
	return [result longLongValue];
}

- (void)setPrimitiveIdLineUpValue:(int64_t)value_ {
	[self setPrimitiveIdLineUp:[NSNumber numberWithLongLong:value_]];
}





@dynamic midfielder01;






@dynamic midfielder02;






@dynamic reserve;






@dynamic striker;






@dynamic matchAsLocal;

	

@dynamic matchAsVisitor;

	

@dynamic team;

	






@end
