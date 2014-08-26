// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to EventOfMatch.h instead.

#import <CoreData/CoreData.h>


extern const struct EventOfMatchAttributes {
	__unsafe_unretained NSString *actorInTransmitterName;
	__unsafe_unretained NSString *actorReceptorName;
	__unsafe_unretained NSString *actorTransmitterName;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *dateIn;
	__unsafe_unretained NSString *idEvent;
	__unsafe_unretained NSString *idEventOfMatch;
	__unsafe_unretained NSString *idMatch;
	__unsafe_unretained NSString *idPeriod;
	__unsafe_unretained NSString *idTeam;
	__unsafe_unretained NSString *isOwnGoal;
	__unsafe_unretained NSString *isPenaltyGoal;
	__unsafe_unretained NSString *localScore;
	__unsafe_unretained NSString *matchMinute;
	__unsafe_unretained NSString *status;
	__unsafe_unretained NSString *twittGoalDone;
	__unsafe_unretained NSString *visitorScore;
} EventOfMatchAttributes;

extern const struct EventOfMatchRelationships {
	__unsafe_unretained NSString *event;
	__unsafe_unretained NSString *eventTeam;
	__unsafe_unretained NSString *match;
} EventOfMatchRelationships;

extern const struct EventOfMatchFetchedProperties {
} EventOfMatchFetchedProperties;

@class Event;
@class Team;
@class Match;























@interface EventOfMatchID : NSManagedObjectID {}
@end

@interface _EventOfMatch : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (EventOfMatchID*)objectID;





@property (nonatomic, strong) NSString* actorInTransmitterName;



//- (BOOL)validateActorInTransmitterName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* actorReceptorName;



//- (BOOL)validateActorReceptorName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* actorTransmitterName;



//- (BOOL)validateActorTransmitterName:(id*)value_ error:(NSError**)error_;





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





@property (nonatomic, strong) NSDate* dateIn;



//- (BOOL)validateDateIn:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idEvent;



@property int32_t idEventValue;
- (int32_t)idEventValue;
- (void)setIdEventValue:(int32_t)value_;

//- (BOOL)validateIdEvent:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idEventOfMatch;



@property int64_t idEventOfMatchValue;
- (int64_t)idEventOfMatchValue;
- (void)setIdEventOfMatchValue:(int64_t)value_;

//- (BOOL)validateIdEventOfMatch:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idMatch;



@property int32_t idMatchValue;
- (int32_t)idMatchValue;
- (void)setIdMatchValue:(int32_t)value_;

//- (BOOL)validateIdMatch:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idPeriod;



@property int16_t idPeriodValue;
- (int16_t)idPeriodValue;
- (void)setIdPeriodValue:(int16_t)value_;

//- (BOOL)validateIdPeriod:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idTeam;



@property int32_t idTeamValue;
- (int32_t)idTeamValue;
- (void)setIdTeamValue:(int32_t)value_;

//- (BOOL)validateIdTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* isOwnGoal;



@property BOOL isOwnGoalValue;
- (BOOL)isOwnGoalValue;
- (void)setIsOwnGoalValue:(BOOL)value_;

//- (BOOL)validateIsOwnGoal:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* isPenaltyGoal;



@property BOOL isPenaltyGoalValue;
- (BOOL)isPenaltyGoalValue;
- (void)setIsPenaltyGoalValue:(BOOL)value_;

//- (BOOL)validateIsPenaltyGoal:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* localScore;



@property int16_t localScoreValue;
- (int16_t)localScoreValue;
- (void)setLocalScoreValue:(int16_t)value_;

//- (BOOL)validateLocalScore:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* matchMinute;



@property int16_t matchMinuteValue;
- (int16_t)matchMinuteValue;
- (void)setMatchMinuteValue:(int16_t)value_;

//- (BOOL)validateMatchMinute:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* status;



@property int16_t statusValue;
- (int16_t)statusValue;
- (void)setStatusValue:(int16_t)value_;

//- (BOOL)validateStatus:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* twittGoalDone;



@property BOOL twittGoalDoneValue;
- (BOOL)twittGoalDoneValue;
- (void)setTwittGoalDoneValue:(BOOL)value_;

//- (BOOL)validateTwittGoalDone:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* visitorScore;



@property int16_t visitorScoreValue;
- (int16_t)visitorScoreValue;
- (void)setVisitorScoreValue:(int16_t)value_;

//- (BOOL)validateVisitorScore:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) Event *event;

//- (BOOL)validateEvent:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Team *eventTeam;

//- (BOOL)validateEventTeam:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Match *match;

//- (BOOL)validateMatch:(id*)value_ error:(NSError**)error_;





@end

@interface _EventOfMatch (CoreDataGeneratedAccessors)

@end

@interface _EventOfMatch (CoreDataGeneratedPrimitiveAccessors)


- (NSString*)primitiveActorInTransmitterName;
- (void)setPrimitiveActorInTransmitterName:(NSString*)value;




- (NSString*)primitiveActorReceptorName;
- (void)setPrimitiveActorReceptorName:(NSString*)value;




- (NSString*)primitiveActorTransmitterName;
- (void)setPrimitiveActorTransmitterName:(NSString*)value;




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




- (NSDate*)primitiveDateIn;
- (void)setPrimitiveDateIn:(NSDate*)value;




- (NSNumber*)primitiveIdEvent;
- (void)setPrimitiveIdEvent:(NSNumber*)value;

- (int32_t)primitiveIdEventValue;
- (void)setPrimitiveIdEventValue:(int32_t)value_;




- (NSNumber*)primitiveIdEventOfMatch;
- (void)setPrimitiveIdEventOfMatch:(NSNumber*)value;

- (int64_t)primitiveIdEventOfMatchValue;
- (void)setPrimitiveIdEventOfMatchValue:(int64_t)value_;




- (NSNumber*)primitiveIdMatch;
- (void)setPrimitiveIdMatch:(NSNumber*)value;

- (int32_t)primitiveIdMatchValue;
- (void)setPrimitiveIdMatchValue:(int32_t)value_;




- (NSNumber*)primitiveIdPeriod;
- (void)setPrimitiveIdPeriod:(NSNumber*)value;

- (int16_t)primitiveIdPeriodValue;
- (void)setPrimitiveIdPeriodValue:(int16_t)value_;




- (NSNumber*)primitiveIdTeam;
- (void)setPrimitiveIdTeam:(NSNumber*)value;

- (int32_t)primitiveIdTeamValue;
- (void)setPrimitiveIdTeamValue:(int32_t)value_;




- (NSNumber*)primitiveIsOwnGoal;
- (void)setPrimitiveIsOwnGoal:(NSNumber*)value;

- (BOOL)primitiveIsOwnGoalValue;
- (void)setPrimitiveIsOwnGoalValue:(BOOL)value_;




- (NSNumber*)primitiveIsPenaltyGoal;
- (void)setPrimitiveIsPenaltyGoal:(NSNumber*)value;

- (BOOL)primitiveIsPenaltyGoalValue;
- (void)setPrimitiveIsPenaltyGoalValue:(BOOL)value_;




- (NSNumber*)primitiveLocalScore;
- (void)setPrimitiveLocalScore:(NSNumber*)value;

- (int16_t)primitiveLocalScoreValue;
- (void)setPrimitiveLocalScoreValue:(int16_t)value_;




- (NSNumber*)primitiveMatchMinute;
- (void)setPrimitiveMatchMinute:(NSNumber*)value;

- (int16_t)primitiveMatchMinuteValue;
- (void)setPrimitiveMatchMinuteValue:(int16_t)value_;




- (NSNumber*)primitiveStatus;
- (void)setPrimitiveStatus:(NSNumber*)value;

- (int16_t)primitiveStatusValue;
- (void)setPrimitiveStatusValue:(int16_t)value_;




- (NSNumber*)primitiveTwittGoalDone;
- (void)setPrimitiveTwittGoalDone:(NSNumber*)value;

- (BOOL)primitiveTwittGoalDoneValue;
- (void)setPrimitiveTwittGoalDoneValue:(BOOL)value_;




- (NSNumber*)primitiveVisitorScore;
- (void)setPrimitiveVisitorScore:(NSNumber*)value;

- (int16_t)primitiveVisitorScoreValue;
- (void)setPrimitiveVisitorScoreValue:(int16_t)value_;





- (Event*)primitiveEvent;
- (void)setPrimitiveEvent:(Event*)value;



- (Team*)primitiveEventTeam;
- (void)setPrimitiveEventTeam:(Team*)value;



- (Match*)primitiveMatch;
- (void)setPrimitiveMatch:(Match*)value;


@end
