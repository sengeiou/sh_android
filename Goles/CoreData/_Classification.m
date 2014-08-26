// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Classification.m instead.

#import "_Classification.h"

const struct ClassificationAttributes ClassificationAttributes = {
	.csys_birth = @"csys_birth",
	.csys_deleted = @"csys_deleted",
	.csys_modified = @"csys_modified",
	.csys_revision = @"csys_revision",
	.csys_syncronized = @"csys_syncronized",
	.dl = @"dl",
	.dv = @"dv",
	.gal = @"gal",
	.gav = @"gav",
	.gfl = @"gfl",
	.gfv = @"gfv",
	.idClassification = @"idClassification",
	.idTeam = @"idTeam",
	.idTournament = @"idTournament",
	.ll = @"ll",
	.lv = @"lv",
	.pl = @"pl",
	.points = @"points",
	.pv = @"pv",
	.weight = @"weight",
	.wl = @"wl",
	.wv = @"wv",
};

const struct ClassificationRelationships ClassificationRelationships = {
	.team = @"team",
	.tournament = @"tournament",
};

const struct ClassificationFetchedProperties ClassificationFetchedProperties = {
};

@implementation ClassificationID
@end

@implementation _Classification

+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription insertNewObjectForEntityForName:@"Classification" inManagedObjectContext:moc_];
}

+ (NSString*)entityName {
	return @"Classification";
}

+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_ {
	NSParameterAssert(moc_);
	return [NSEntityDescription entityForName:@"Classification" inManagedObjectContext:moc_];
}

- (ClassificationID*)objectID {
	return (ClassificationID*)[super objectID];
}

+ (NSSet*)keyPathsForValuesAffectingValueForKey:(NSString*)key {
	NSSet *keyPaths = [super keyPathsForValuesAffectingValueForKey:key];
	
	if ([key isEqualToString:@"csys_revisionValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"csys_revision"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"dlValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"dl"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"dvValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"dv"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"galValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"gal"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"gavValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"gav"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"gflValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"gfl"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"gfvValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"gfv"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idClassificationValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idClassification"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idTeamValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idTeam"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"idTournamentValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"idTournament"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"llValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"ll"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"lvValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"lv"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"plValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"pl"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"pointsValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"points"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"pvValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"pv"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"weightValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"weight"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"wlValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"wl"];
		keyPaths = [keyPaths setByAddingObjectsFromSet:affectingKey];
		return keyPaths;
	}
	if ([key isEqualToString:@"wvValue"]) {
		NSSet *affectingKey = [NSSet setWithObject:@"wv"];
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






@dynamic dl;



- (int32_t)dlValue {
	NSNumber *result = [self dl];
	return [result intValue];
}

- (void)setDlValue:(int32_t)value_ {
	[self setDl:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveDlValue {
	NSNumber *result = [self primitiveDl];
	return [result intValue];
}

- (void)setPrimitiveDlValue:(int32_t)value_ {
	[self setPrimitiveDl:[NSNumber numberWithInt:value_]];
}





@dynamic dv;



- (int32_t)dvValue {
	NSNumber *result = [self dv];
	return [result intValue];
}

- (void)setDvValue:(int32_t)value_ {
	[self setDv:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveDvValue {
	NSNumber *result = [self primitiveDv];
	return [result intValue];
}

- (void)setPrimitiveDvValue:(int32_t)value_ {
	[self setPrimitiveDv:[NSNumber numberWithInt:value_]];
}





@dynamic gal;



- (int32_t)galValue {
	NSNumber *result = [self gal];
	return [result intValue];
}

- (void)setGalValue:(int32_t)value_ {
	[self setGal:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveGalValue {
	NSNumber *result = [self primitiveGal];
	return [result intValue];
}

- (void)setPrimitiveGalValue:(int32_t)value_ {
	[self setPrimitiveGal:[NSNumber numberWithInt:value_]];
}





@dynamic gav;



- (int32_t)gavValue {
	NSNumber *result = [self gav];
	return [result intValue];
}

- (void)setGavValue:(int32_t)value_ {
	[self setGav:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveGavValue {
	NSNumber *result = [self primitiveGav];
	return [result intValue];
}

- (void)setPrimitiveGavValue:(int32_t)value_ {
	[self setPrimitiveGav:[NSNumber numberWithInt:value_]];
}





@dynamic gfl;



- (int32_t)gflValue {
	NSNumber *result = [self gfl];
	return [result intValue];
}

- (void)setGflValue:(int32_t)value_ {
	[self setGfl:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveGflValue {
	NSNumber *result = [self primitiveGfl];
	return [result intValue];
}

- (void)setPrimitiveGflValue:(int32_t)value_ {
	[self setPrimitiveGfl:[NSNumber numberWithInt:value_]];
}





@dynamic gfv;



- (int32_t)gfvValue {
	NSNumber *result = [self gfv];
	return [result intValue];
}

- (void)setGfvValue:(int32_t)value_ {
	[self setGfv:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveGfvValue {
	NSNumber *result = [self primitiveGfv];
	return [result intValue];
}

- (void)setPrimitiveGfvValue:(int32_t)value_ {
	[self setPrimitiveGfv:[NSNumber numberWithInt:value_]];
}





@dynamic idClassification;



- (int64_t)idClassificationValue {
	NSNumber *result = [self idClassification];
	return [result longLongValue];
}

- (void)setIdClassificationValue:(int64_t)value_ {
	[self setIdClassification:[NSNumber numberWithLongLong:value_]];
}

- (int64_t)primitiveIdClassificationValue {
	NSNumber *result = [self primitiveIdClassification];
	return [result longLongValue];
}

- (void)setPrimitiveIdClassificationValue:(int64_t)value_ {
	[self setPrimitiveIdClassification:[NSNumber numberWithLongLong:value_]];
}





@dynamic idTeam;



- (int32_t)idTeamValue {
	NSNumber *result = [self idTeam];
	return [result intValue];
}

- (void)setIdTeamValue:(int32_t)value_ {
	[self setIdTeam:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdTeamValue {
	NSNumber *result = [self primitiveIdTeam];
	return [result intValue];
}

- (void)setPrimitiveIdTeamValue:(int32_t)value_ {
	[self setPrimitiveIdTeam:[NSNumber numberWithInt:value_]];
}





@dynamic idTournament;



- (int32_t)idTournamentValue {
	NSNumber *result = [self idTournament];
	return [result intValue];
}

- (void)setIdTournamentValue:(int32_t)value_ {
	[self setIdTournament:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveIdTournamentValue {
	NSNumber *result = [self primitiveIdTournament];
	return [result intValue];
}

- (void)setPrimitiveIdTournamentValue:(int32_t)value_ {
	[self setPrimitiveIdTournament:[NSNumber numberWithInt:value_]];
}





@dynamic ll;



- (int32_t)llValue {
	NSNumber *result = [self ll];
	return [result intValue];
}

- (void)setLlValue:(int32_t)value_ {
	[self setLl:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveLlValue {
	NSNumber *result = [self primitiveLl];
	return [result intValue];
}

- (void)setPrimitiveLlValue:(int32_t)value_ {
	[self setPrimitiveLl:[NSNumber numberWithInt:value_]];
}





@dynamic lv;



- (int32_t)lvValue {
	NSNumber *result = [self lv];
	return [result intValue];
}

- (void)setLvValue:(int32_t)value_ {
	[self setLv:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveLvValue {
	NSNumber *result = [self primitiveLv];
	return [result intValue];
}

- (void)setPrimitiveLvValue:(int32_t)value_ {
	[self setPrimitiveLv:[NSNumber numberWithInt:value_]];
}





@dynamic pl;



- (int32_t)plValue {
	NSNumber *result = [self pl];
	return [result intValue];
}

- (void)setPlValue:(int32_t)value_ {
	[self setPl:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitivePlValue {
	NSNumber *result = [self primitivePl];
	return [result intValue];
}

- (void)setPrimitivePlValue:(int32_t)value_ {
	[self setPrimitivePl:[NSNumber numberWithInt:value_]];
}





@dynamic points;



- (int32_t)pointsValue {
	NSNumber *result = [self points];
	return [result intValue];
}

- (void)setPointsValue:(int32_t)value_ {
	[self setPoints:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitivePointsValue {
	NSNumber *result = [self primitivePoints];
	return [result intValue];
}

- (void)setPrimitivePointsValue:(int32_t)value_ {
	[self setPrimitivePoints:[NSNumber numberWithInt:value_]];
}





@dynamic pv;



- (int32_t)pvValue {
	NSNumber *result = [self pv];
	return [result intValue];
}

- (void)setPvValue:(int32_t)value_ {
	[self setPv:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitivePvValue {
	NSNumber *result = [self primitivePv];
	return [result intValue];
}

- (void)setPrimitivePvValue:(int32_t)value_ {
	[self setPrimitivePv:[NSNumber numberWithInt:value_]];
}





@dynamic weight;



- (int32_t)weightValue {
	NSNumber *result = [self weight];
	return [result intValue];
}

- (void)setWeightValue:(int32_t)value_ {
	[self setWeight:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveWeightValue {
	NSNumber *result = [self primitiveWeight];
	return [result intValue];
}

- (void)setPrimitiveWeightValue:(int32_t)value_ {
	[self setPrimitiveWeight:[NSNumber numberWithInt:value_]];
}





@dynamic wl;



- (int32_t)wlValue {
	NSNumber *result = [self wl];
	return [result intValue];
}

- (void)setWlValue:(int32_t)value_ {
	[self setWl:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveWlValue {
	NSNumber *result = [self primitiveWl];
	return [result intValue];
}

- (void)setPrimitiveWlValue:(int32_t)value_ {
	[self setPrimitiveWl:[NSNumber numberWithInt:value_]];
}





@dynamic wv;



- (int32_t)wvValue {
	NSNumber *result = [self wv];
	return [result intValue];
}

- (void)setWvValue:(int32_t)value_ {
	[self setWv:[NSNumber numberWithInt:value_]];
}

- (int32_t)primitiveWvValue {
	NSNumber *result = [self primitiveWv];
	return [result intValue];
}

- (void)setPrimitiveWvValue:(int32_t)value_ {
	[self setPrimitiveWv:[NSNumber numberWithInt:value_]];
}





@dynamic team;

	

@dynamic tournament;

	






@end
