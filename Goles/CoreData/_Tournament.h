// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Tournament.h instead.

#import <CoreData/CoreData.h>


extern const struct TournamentAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *endDate;
	__unsafe_unretained NSString *firstC;
	__unsafe_unretained NSString *fourthC;
	__unsafe_unretained NSString *idTournament;
	__unsafe_unretained NSString *imageName;
	__unsafe_unretained NSString *isLeague;
	__unsafe_unretained NSString *name;
	__unsafe_unretained NSString *orderBy;
	__unsafe_unretained NSString *secondC;
	__unsafe_unretained NSString *startDate;
	__unsafe_unretained NSString *status;
	__unsafe_unretained NSString *thirdC;
	__unsafe_unretained NSString *visibleApp;
	__unsafe_unretained NSString *year;
} TournamentAttributes;

extern const struct TournamentRelationships {
	__unsafe_unretained NSString *classifications;
	__unsafe_unretained NSString *modes;
	__unsafe_unretained NSString *rounds;
} TournamentRelationships;

extern const struct TournamentFetchedProperties {
} TournamentFetchedProperties;

@class Classification;
@class Mode;
@class Round;





















@interface TournamentID : NSManagedObjectID {}
@end

@interface _Tournament : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (TournamentID*)objectID;





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





@property (nonatomic, strong) NSDate* endDate;



//- (BOOL)validateEndDate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* firstC;



@property int16_t firstCValue;
- (int16_t)firstCValue;
- (void)setFirstCValue:(int16_t)value_;

//- (BOOL)validateFirstC:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* fourthC;



@property int16_t fourthCValue;
- (int16_t)fourthCValue;
- (void)setFourthCValue:(int16_t)value_;

//- (BOOL)validateFourthC:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idTournament;



@property int64_t idTournamentValue;
- (int64_t)idTournamentValue;
- (void)setIdTournamentValue:(int64_t)value_;

//- (BOOL)validateIdTournament:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* imageName;



//- (BOOL)validateImageName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* isLeague;



@property BOOL isLeagueValue;
- (BOOL)isLeagueValue;
- (void)setIsLeagueValue:(BOOL)value_;

//- (BOOL)validateIsLeague:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* name;



//- (BOOL)validateName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* orderBy;



@property int32_t orderByValue;
- (int32_t)orderByValue;
- (void)setOrderByValue:(int32_t)value_;

//- (BOOL)validateOrderBy:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* secondC;



@property int16_t secondCValue;
- (int16_t)secondCValue;
- (void)setSecondCValue:(int16_t)value_;

//- (BOOL)validateSecondC:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSDate* startDate;



//- (BOOL)validateStartDate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* status;



@property int16_t statusValue;
- (int16_t)statusValue;
- (void)setStatusValue:(int16_t)value_;

//- (BOOL)validateStatus:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* thirdC;



@property int16_t thirdCValue;
- (int16_t)thirdCValue;
- (void)setThirdCValue:(int16_t)value_;

//- (BOOL)validateThirdC:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* visibleApp;



@property int16_t visibleAppValue;
- (int16_t)visibleAppValue;
- (void)setVisibleAppValue:(int16_t)value_;

//- (BOOL)validateVisibleApp:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* year;



@property int64_t yearValue;
- (int64_t)yearValue;
- (void)setYearValue:(int64_t)value_;

//- (BOOL)validateYear:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSSet *classifications;

- (NSMutableSet*)classificationsSet;




@property (nonatomic, strong) NSSet *modes;

- (NSMutableSet*)modesSet;




@property (nonatomic, strong) NSSet *rounds;

- (NSMutableSet*)roundsSet;





@end

@interface _Tournament (CoreDataGeneratedAccessors)

- (void)addClassifications:(NSSet*)value_;
- (void)removeClassifications:(NSSet*)value_;
- (void)addClassificationsObject:(Classification*)value_;
- (void)removeClassificationsObject:(Classification*)value_;

- (void)addModes:(NSSet*)value_;
- (void)removeModes:(NSSet*)value_;
- (void)addModesObject:(Mode*)value_;
- (void)removeModesObject:(Mode*)value_;

- (void)addRounds:(NSSet*)value_;
- (void)removeRounds:(NSSet*)value_;
- (void)addRoundsObject:(Round*)value_;
- (void)removeRoundsObject:(Round*)value_;

@end

@interface _Tournament (CoreDataGeneratedPrimitiveAccessors)


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




- (NSDate*)primitiveEndDate;
- (void)setPrimitiveEndDate:(NSDate*)value;




- (NSNumber*)primitiveFirstC;
- (void)setPrimitiveFirstC:(NSNumber*)value;

- (int16_t)primitiveFirstCValue;
- (void)setPrimitiveFirstCValue:(int16_t)value_;




- (NSNumber*)primitiveFourthC;
- (void)setPrimitiveFourthC:(NSNumber*)value;

- (int16_t)primitiveFourthCValue;
- (void)setPrimitiveFourthCValue:(int16_t)value_;




- (NSNumber*)primitiveIdTournament;
- (void)setPrimitiveIdTournament:(NSNumber*)value;

- (int64_t)primitiveIdTournamentValue;
- (void)setPrimitiveIdTournamentValue:(int64_t)value_;




- (NSString*)primitiveImageName;
- (void)setPrimitiveImageName:(NSString*)value;




- (NSNumber*)primitiveIsLeague;
- (void)setPrimitiveIsLeague:(NSNumber*)value;

- (BOOL)primitiveIsLeagueValue;
- (void)setPrimitiveIsLeagueValue:(BOOL)value_;




- (NSString*)primitiveName;
- (void)setPrimitiveName:(NSString*)value;




- (NSNumber*)primitiveOrderBy;
- (void)setPrimitiveOrderBy:(NSNumber*)value;

- (int32_t)primitiveOrderByValue;
- (void)setPrimitiveOrderByValue:(int32_t)value_;




- (NSNumber*)primitiveSecondC;
- (void)setPrimitiveSecondC:(NSNumber*)value;

- (int16_t)primitiveSecondCValue;
- (void)setPrimitiveSecondCValue:(int16_t)value_;




- (NSDate*)primitiveStartDate;
- (void)setPrimitiveStartDate:(NSDate*)value;




- (NSNumber*)primitiveStatus;
- (void)setPrimitiveStatus:(NSNumber*)value;

- (int16_t)primitiveStatusValue;
- (void)setPrimitiveStatusValue:(int16_t)value_;




- (NSNumber*)primitiveThirdC;
- (void)setPrimitiveThirdC:(NSNumber*)value;

- (int16_t)primitiveThirdCValue;
- (void)setPrimitiveThirdCValue:(int16_t)value_;




- (NSNumber*)primitiveVisibleApp;
- (void)setPrimitiveVisibleApp:(NSNumber*)value;

- (int16_t)primitiveVisibleAppValue;
- (void)setPrimitiveVisibleAppValue:(int16_t)value_;




- (NSNumber*)primitiveYear;
- (void)setPrimitiveYear:(NSNumber*)value;

- (int64_t)primitiveYearValue;
- (void)setPrimitiveYearValue:(int64_t)value_;





- (NSMutableSet*)primitiveClassifications;
- (void)setPrimitiveClassifications:(NSMutableSet*)value;



- (NSMutableSet*)primitiveModes;
- (void)setPrimitiveModes:(NSMutableSet*)value;



- (NSMutableSet*)primitiveRounds;
- (void)setPrimitiveRounds:(NSMutableSet*)value;


@end
