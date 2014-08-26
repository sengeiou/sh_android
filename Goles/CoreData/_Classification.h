// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Classification.h instead.

#import <CoreData/CoreData.h>


extern const struct ClassificationAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *dl;
	__unsafe_unretained NSString *dv;
	__unsafe_unretained NSString *gal;
	__unsafe_unretained NSString *gav;
	__unsafe_unretained NSString *gfl;
	__unsafe_unretained NSString *gfv;
	__unsafe_unretained NSString *idClassification;
	__unsafe_unretained NSString *idTeam;
	__unsafe_unretained NSString *idTournament;
	__unsafe_unretained NSString *ll;
	__unsafe_unretained NSString *lv;
	__unsafe_unretained NSString *pl;
	__unsafe_unretained NSString *points;
	__unsafe_unretained NSString *pv;
	__unsafe_unretained NSString *weight;
	__unsafe_unretained NSString *wl;
	__unsafe_unretained NSString *wv;
} ClassificationAttributes;

extern const struct ClassificationRelationships {
	__unsafe_unretained NSString *team;
	__unsafe_unretained NSString *tournament;
} ClassificationRelationships;

extern const struct ClassificationFetchedProperties {
} ClassificationFetchedProperties;

@class Team;
@class Tournament;
























@interface ClassificationID : NSManagedObjectID {}
@end

@interface _Classification : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (ClassificationID*)objectID;





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





@property (nonatomic, strong) NSNumber* dl;



@property int32_t dlValue;
- (int32_t)dlValue;
- (void)setDlValue:(int32_t)value_;

//- (BOOL)validateDl:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* dv;



@property int32_t dvValue;
- (int32_t)dvValue;
- (void)setDvValue:(int32_t)value_;

//- (BOOL)validateDv:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* gal;



@property int32_t galValue;
- (int32_t)galValue;
- (void)setGalValue:(int32_t)value_;

//- (BOOL)validateGal:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* gav;



@property int32_t gavValue;
- (int32_t)gavValue;
- (void)setGavValue:(int32_t)value_;

//- (BOOL)validateGav:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* gfl;



@property int32_t gflValue;
- (int32_t)gflValue;
- (void)setGflValue:(int32_t)value_;

//- (BOOL)validateGfl:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* gfv;



@property int32_t gfvValue;
- (int32_t)gfvValue;
- (void)setGfvValue:(int32_t)value_;

//- (BOOL)validateGfv:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idClassification;



@property int64_t idClassificationValue;
- (int64_t)idClassificationValue;
- (void)setIdClassificationValue:(int64_t)value_;

//- (BOOL)validateIdClassification:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idTeam;



@property int32_t idTeamValue;
- (int32_t)idTeamValue;
- (void)setIdTeamValue:(int32_t)value_;

//- (BOOL)validateIdTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idTournament;



@property int32_t idTournamentValue;
- (int32_t)idTournamentValue;
- (void)setIdTournamentValue:(int32_t)value_;

//- (BOOL)validateIdTournament:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* ll;



@property int32_t llValue;
- (int32_t)llValue;
- (void)setLlValue:(int32_t)value_;

//- (BOOL)validateLl:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* lv;



@property int32_t lvValue;
- (int32_t)lvValue;
- (void)setLvValue:(int32_t)value_;

//- (BOOL)validateLv:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* pl;



@property int32_t plValue;
- (int32_t)plValue;
- (void)setPlValue:(int32_t)value_;

//- (BOOL)validatePl:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* points;



@property int32_t pointsValue;
- (int32_t)pointsValue;
- (void)setPointsValue:(int32_t)value_;

//- (BOOL)validatePoints:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* pv;



@property int32_t pvValue;
- (int32_t)pvValue;
- (void)setPvValue:(int32_t)value_;

//- (BOOL)validatePv:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* weight;



@property int32_t weightValue;
- (int32_t)weightValue;
- (void)setWeightValue:(int32_t)value_;

//- (BOOL)validateWeight:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* wl;



@property int32_t wlValue;
- (int32_t)wlValue;
- (void)setWlValue:(int32_t)value_;

//- (BOOL)validateWl:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* wv;



@property int32_t wvValue;
- (int32_t)wvValue;
- (void)setWvValue:(int32_t)value_;

//- (BOOL)validateWv:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) Team *team;

//- (BOOL)validateTeam:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Tournament *tournament;

//- (BOOL)validateTournament:(id*)value_ error:(NSError**)error_;





@end

@interface _Classification (CoreDataGeneratedAccessors)

@end

@interface _Classification (CoreDataGeneratedPrimitiveAccessors)


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




- (NSNumber*)primitiveDl;
- (void)setPrimitiveDl:(NSNumber*)value;

- (int32_t)primitiveDlValue;
- (void)setPrimitiveDlValue:(int32_t)value_;




- (NSNumber*)primitiveDv;
- (void)setPrimitiveDv:(NSNumber*)value;

- (int32_t)primitiveDvValue;
- (void)setPrimitiveDvValue:(int32_t)value_;




- (NSNumber*)primitiveGal;
- (void)setPrimitiveGal:(NSNumber*)value;

- (int32_t)primitiveGalValue;
- (void)setPrimitiveGalValue:(int32_t)value_;




- (NSNumber*)primitiveGav;
- (void)setPrimitiveGav:(NSNumber*)value;

- (int32_t)primitiveGavValue;
- (void)setPrimitiveGavValue:(int32_t)value_;




- (NSNumber*)primitiveGfl;
- (void)setPrimitiveGfl:(NSNumber*)value;

- (int32_t)primitiveGflValue;
- (void)setPrimitiveGflValue:(int32_t)value_;




- (NSNumber*)primitiveGfv;
- (void)setPrimitiveGfv:(NSNumber*)value;

- (int32_t)primitiveGfvValue;
- (void)setPrimitiveGfvValue:(int32_t)value_;




- (NSNumber*)primitiveIdClassification;
- (void)setPrimitiveIdClassification:(NSNumber*)value;

- (int64_t)primitiveIdClassificationValue;
- (void)setPrimitiveIdClassificationValue:(int64_t)value_;




- (NSNumber*)primitiveIdTeam;
- (void)setPrimitiveIdTeam:(NSNumber*)value;

- (int32_t)primitiveIdTeamValue;
- (void)setPrimitiveIdTeamValue:(int32_t)value_;




- (NSNumber*)primitiveIdTournament;
- (void)setPrimitiveIdTournament:(NSNumber*)value;

- (int32_t)primitiveIdTournamentValue;
- (void)setPrimitiveIdTournamentValue:(int32_t)value_;




- (NSNumber*)primitiveLl;
- (void)setPrimitiveLl:(NSNumber*)value;

- (int32_t)primitiveLlValue;
- (void)setPrimitiveLlValue:(int32_t)value_;




- (NSNumber*)primitiveLv;
- (void)setPrimitiveLv:(NSNumber*)value;

- (int32_t)primitiveLvValue;
- (void)setPrimitiveLvValue:(int32_t)value_;




- (NSNumber*)primitivePl;
- (void)setPrimitivePl:(NSNumber*)value;

- (int32_t)primitivePlValue;
- (void)setPrimitivePlValue:(int32_t)value_;




- (NSNumber*)primitivePoints;
- (void)setPrimitivePoints:(NSNumber*)value;

- (int32_t)primitivePointsValue;
- (void)setPrimitivePointsValue:(int32_t)value_;




- (NSNumber*)primitivePv;
- (void)setPrimitivePv:(NSNumber*)value;

- (int32_t)primitivePvValue;
- (void)setPrimitivePvValue:(int32_t)value_;




- (NSNumber*)primitiveWeight;
- (void)setPrimitiveWeight:(NSNumber*)value;

- (int32_t)primitiveWeightValue;
- (void)setPrimitiveWeightValue:(int32_t)value_;




- (NSNumber*)primitiveWl;
- (void)setPrimitiveWl:(NSNumber*)value;

- (int32_t)primitiveWlValue;
- (void)setPrimitiveWlValue:(int32_t)value_;




- (NSNumber*)primitiveWv;
- (void)setPrimitiveWv:(NSNumber*)value;

- (int32_t)primitiveWvValue;
- (void)setPrimitiveWvValue:(int32_t)value_;





- (Team*)primitiveTeam;
- (void)setPrimitiveTeam:(Team*)value;



- (Tournament*)primitiveTournament;
- (void)setPrimitiveTournament:(Tournament*)value;


@end
