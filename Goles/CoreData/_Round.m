// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Round.m instead.

#import "_Round.h"

const struct RoundAttributes RoundAttributes = {
	.campaign = @"campaign",
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.endDate = @"endDate",
	.idCampaign = @"idCampaign",
	.idRound = @"idRound",
	.name = @"name",
	.order = @"order",
	.roundType = @"roundType",
	.startDate = @"startDate",
};

const struct RoundRelationships RoundRelationships = {
	.matchesList = @"matchesList",
	.tournament = @"tournament",
};

const struct RoundFetchedProperties RoundFetchedProperties = {
};

@implementation RoundID
@end

@implementation _Round

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Round" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Round";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Round" inManagedObjectContext:moc_];
}

- (RoundID*)objectID {
	return (RoundID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"endDateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"endDate"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idCampaignValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idCampaign"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idRoundValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idRound"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"orderValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"order"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"roundTypeValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"roundType"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"startDateValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"startDate"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}

	return keyPaths;
}




@dynamic campaign;






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






@dynamic endDate;



- (int64_t)endDateValue {
	NSNumber *result = [self endDate];
	return [result longLongValue];
}

- (void)setEndDateValue:(int64_t)value_ {
	[self setEndDate:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveEndDateValue {
	NSNumber *result = [self primitiveEndDate];
	return [result longLongValue];
}

- (void)setPrimitiveEndDateValue:(int64_t)value_ {
	[self setPrimitiveEndDate:[NSNumber numberWithLongLong:value_]];
}





@dynamic idCampaign;



- (int32_t)idCampaignValue {
	NSNumber *result = [self idCampaign];
	return [result intValue];
}

- (void)setIdCampaignValue:(int32_t)value_ {
	[self setIdCampaign:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdCampaignValue {
	NSNumber *result = [self primitiveIdCampaign];
	return [result intValue];
}

- (void)setPrimitiveIdCampaignValue:(int32_t)value_ {
	[self setPrimitiveIdCampaign:[NSNumber numberWithInt:value_]];
}





@dynamic idRound;



- (int64_t)idRoundValue {
	NSNumber *result = [self idRound];
	return [result longLongValue];
}

- (void)setIdRoundValue:(int64_t)value_ {
	[self setIdRound:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdRoundValue {
	NSNumber *result = [self primitiveIdRound];
	return [result longLongValue];
}

- (void)setPrimitiveIdRoundValue:(int64_t)value_ {
	[self setPrimitiveIdRound:[NSNumber numberWithLongLong:value_]];
}





@dynamic name;






@dynamic order;



- (int64_t)orderValue {
	NSNumber *result = [self order];
	return [result longLongValue];
}

- (void)setOrderValue:(int64_t)value_ {
	[self setOrder:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveOrderValue {
	NSNumber *result = [self primitiveOrder];
	return [result longLongValue];
}

- (void)setPrimitiveOrderValue:(int64_t)value_ {
	[self setPrimitiveOrder:[NSNumber numberWithLongLong:value_]];
}





@dynamic roundType;



- (int16_t)roundTypeValue {
	NSNumber *result = [self roundType];
	return [result shortValue];
}

- (void)setRoundTypeValue:(int16_t)value_ {
	[self setRoundType:[NSNumber numberWithShort:value_]];
}

- (int16_t)primitiveRoundTypeValue {
	NSNumber *result = [self primitiveRoundType];
	return [result shortValue];
}

- (void)setPrimitiveRoundTypeValue:(int16_t)value_ {
	[self setPrimitiveRoundType:[NSNumber numberWithShort:value_]];
}





@dynamic startDate;



- (int64_t)startDateValue {
	NSNumber *result = [self startDate];
	return [result longLongValue];
}

- (void)setStartDateValue:(int64_t)value_ {
	[self setStartDate:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveStartDateValue {
	NSNumber *result = [self primitiveStartDate];
	return [result longLongValue];
}

- (void)setPrimitiveStartDateValue:(int64_t)value_ {
	[self setPrimitiveStartDate:[NSNumber numberWithLongLong:value_]];
}





@dynamic matchesList;

	
- (NSMutableSet*)matchesListSet {
	[self willAccessValueForKey:@"matchesList"];
  
	NSMutableSet *result = (NSMutableSet*)[self mutableSetValueForKey:@"matchesList"];
  
	[self didAccessValueForKey:@"matchesList"];
	return result;
}
	

@dynamic tournament;

	






@end
