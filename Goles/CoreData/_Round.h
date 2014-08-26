// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Round.h instead.

#import <CoreData/CoreData.h>


extern const struct RoundAttributes {
	__unsafe_unretained NSString *campaign;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *endDate;
	__unsafe_unretained NSString *idCampaign;
	__unsafe_unretained NSString *idRound;
	__unsafe_unretained NSString *name;
	__unsafe_unretained NSString *order;
	__unsafe_unretained NSString *roundType;
	__unsafe_unretained NSString *startDate;
} RoundAttributes;

extern const struct RoundRelationships {
	__unsafe_unretained NSString *matchesList;
	__unsafe_unretained NSString *tournament;
} RoundRelationships;

extern const struct RoundFetchedProperties {
} RoundFetchedProperties;

@class Match;
@class Tournament;















@interface RoundID : NSManagedObjectID {}
@end

@interface _Round : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (RoundID*)objectID;





@property (nonatomic, strong) NSString* campaign;



//- (BOOL)validateCampaign:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSDate* csys_birth;



//- (BOOL)validateCsys_birth:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSDate* csys_deleted;



//- (BOOL)validateCsys_deleted:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSDate* csys_modified;



//- (BOOL)validateCsys_modified:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* csys_revision;



@property int64_t csys_revisionValue;
- (int64_t)csys_revisionValue;
- (void)setCsys_revisionValue:(int64_t)value_;

//- (BOOL)validateCsys_revision:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* csys_syncronized;



//- (BOOL)validateCsys_syncronized:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* endDate;



@property int64_t endDateValue;
- (int64_t)endDateValue;
- (void)setEndDateValue:(int64_t)value_;

//- (BOOL)validateEndDate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idCampaign;



@property int32_t idCampaignValue;
- (int32_t)idCampaignValue;
- (void)setIdCampaignValue:(int32_t)value_;

//- (BOOL)validateIdCampaign:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idRound;



@property int64_t idRoundValue;
- (int64_t)idRoundValue;
- (void)setIdRoundValue:(int64_t)value_;

//- (BOOL)validateIdRound:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* name;



//- (BOOL)validateName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* order;



@property int64_t orderValue;
- (int64_t)orderValue;
- (void)setOrderValue:(int64_t)value_;

//- (BOOL)validateOrder:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* roundType;



@property int16_t roundTypeValue;
- (int16_t)roundTypeValue;
- (void)setRoundTypeValue:(int16_t)value_;

//- (BOOL)validateRoundType:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* startDate;



@property int64_t startDateValue;
- (int64_t)startDateValue;
- (void)setStartDateValue:(int64_t)value_;

//- (BOOL)validateStartDate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSSet *matchesList;

- (NSMutableSet*)matchesListSet;




@property (nonatomic, strong) Tournament *tournament;

//- (BOOL)validateTournament:(id*)value_ error:(NSError**)error_;





@end

@interface _Round (CoreDataGeneratedAccessors)

- (void)addMatchesList:(NSSet*)value_;
- (void)removeMatchesList:(NSSet*)value_;
- (void)addMatchesListObject:(Match*)value_;
- (void)removeMatchesListObject:(Match*)value_;

@end

@interface _Round (CoreDataGeneratedPrimitiveAccessors)


- (NSString*)primitiveCampaign;
- (void)setPrimitiveCampaign:(NSString*)value;




- (NSDate*)primitiveCsys_birth;
- (void)setPrimitiveCsys_birth:(NSDate*)value;




- (NSDate*)primitiveCsys_deleted;
- (void)setPrimitiveCsys_deleted:(NSDate*)value;




- (NSDate*)primitiveCsys_modified;
- (void)setPrimitiveCsys_modified:(NSDate*)value;




- (NSNumber*)primitiveCsys_revision;
- (void)setPrimitiveCsys_revision:(NSNumber*)value;

- (int64_t)primitiveCsys_revisionValue;
- (void)setPrimitiveCsys_revisionValue:(int64_t)value_;




- (NSString*)primitiveCsys_syncronized;
- (void)setPrimitiveCsys_syncronized:(NSString*)value;




- (NSNumber*)primitiveEndDate;
- (void)setPrimitiveEndDate:(NSNumber*)value;

- (int64_t)primitiveEndDateValue;
- (void)setPrimitiveEndDateValue:(int64_t)value_;




- (NSNumber*)primitiveIdCampaign;
- (void)setPrimitiveIdCampaign:(NSNumber*)value;

- (int32_t)primitiveIdCampaignValue;
- (void)setPrimitiveIdCampaignValue:(int32_t)value_;




- (NSNumber*)primitiveIdRound;
- (void)setPrimitiveIdRound:(NSNumber*)value;

- (int64_t)primitiveIdRoundValue;
- (void)setPrimitiveIdRoundValue:(int64_t)value_;




- (NSString*)primitiveName;
- (void)setPrimitiveName:(NSString*)value;




- (NSNumber*)primitiveOrder;
- (void)setPrimitiveOrder:(NSNumber*)value;

- (int64_t)primitiveOrderValue;
- (void)setPrimitiveOrderValue:(int64_t)value_;




- (NSNumber*)primitiveRoundType;
- (void)setPrimitiveRoundType:(NSNumber*)value;

- (int16_t)primitiveRoundTypeValue;
- (void)setPrimitiveRoundTypeValue:(int16_t)value_;




- (NSNumber*)primitiveStartDate;
- (void)setPrimitiveStartDate:(NSNumber*)value;

- (int64_t)primitiveStartDateValue;
- (void)setPrimitiveStartDateValue:(int64_t)value_;





- (NSMutableSet*)primitiveMatchesList;
- (void)setPrimitiveMatchesList:(NSMutableSet*)value;



- (Tournament*)primitiveTournament;
- (void)setPrimitiveTournament:(Tournament*)value;


@end
