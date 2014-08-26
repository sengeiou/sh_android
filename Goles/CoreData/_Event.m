// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Event.m instead.

#import "_Event.h"

const struct EventAttributes EventAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.idEvent = @"idEvent",
	.name = @"name",
};

const struct EventRelationships EventRelationships = {
	.eventsOfMatch = @"eventsOfMatch",
};

const struct EventFetchedProperties EventFetchedProperties = {
};

@implementation EventID
@end

@implementation _Event

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Event" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Event";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Event" inManagedObjectContext:moc_];
}

- (EventID*)objectID {
	return (EventID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idEventValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idEvent"];
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






@dynamic idEvent;



- (int64_t)idEventValue {
	NSNumber *result = [self idEvent];
	return [result longLongValue];
}

- (void)setIdEventValue:(int64_t)value_ {
	[self setIdEvent:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdEventValue {
	NSNumber *result = [self primitiveIdEvent];
	return [result longLongValue];
}

- (void)setPrimitiveIdEventValue:(int64_t)value_ {
	[self setPrimitiveIdEvent:[NSNumber numberWithLongLong:value_]];
}





@dynamic name;






@dynamic eventsOfMatch;

	
- (NSMutableSet*)eventsOfMatchSet {
	[self willAccessValueForKey:@"eventsOfMatch"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"eventsOfMatch"];
  
	[self didAccessValueForKey:@"eventsOfMatch"];
	return result;
}
	






@end
