// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Match.h instead.

#import <CoreData/CoreData.h>


extern const struct MatchAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *elapsedMinutes;
	__unsafe_unretained NSString *endFinal;
	__unsafe_unretained NSString *idLocalTeam;
	__unsafe_unretained NSString *idMatch;
	__unsafe_unretained NSString *idPreviousMatch;
	__unsafe_unretained NSString *idRound;
	__unsafe_unretained NSString *idVisitorTeam;
	__unsafe_unretained NSString *idWinnerTeam;
	__unsafe_unretained NSString *listTV;
	__unsafe_unretained NSString *localScore;
	__unsafe_unretained NSString *matchDate;
	__unsafe_unretained NSString *matchState;
	__unsafe_unretained NSString *matchSubstate;
	__unsafe_unretained NSString *matchType;
	__unsafe_unretained NSString *notConfirmedMatchDate;
	__unsafe_unretained NSString *order;
	__unsafe_unretained NSString *overTimeStartDate;
	__unsafe_unretained NSString *previousMatchScore;
	__unsafe_unretained NSString *scorePenaltiesLocalTeam;
	__unsafe_unretained NSString *scorePenaltiesVisitorTeam;
	__unsafe_unretained NSString *secondHalfStartDate;
	__unsafe_unretained NSString *startDate;
	__unsafe_unretained NSString *twitterLocal;
	__unsafe_unretained NSString *twitterVisitor;
	__unsafe_unretained NSString *visitorScore;
} MatchAttributes;

extern const struct MatchRelationships {
	__unsafe_unretained NSString *eventsOfMatch;
	__unsafe_unretained NSString *lineUpLocal;
	__unsafe_unretained NSString *lineUpVisitor;
	__unsafe_unretained NSString *matchBetType;
	__unsafe_unretained NSString *round;
	__unsafe_unretained NSString *subscription;
	__unsafe_unretained NSString *teamLocal;
	__unsafe_unretained NSString *teamVisitor;
} MatchRelationships;

extern const struct MatchFetchedProperties {
} MatchFetchedProperties;

@class EventOfMatch;
@class LineUp;
@class LineUp;
@class MatchBetType;
@class Round;
@class Subscription;
@class Team;
@class Team;
































@interface MatchID : NSManagedObjectID {}
@end

@interface _Match : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (MatchID*)objectID;





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





@property (nonatomic, strong) NSNumber* elapsedMinutes;



@property int32_t elapsedMinutesValue;
- (int32_t)elapsedMinutesValue;
- (void)setElapsedMinutesValue:(int32_t)value_;

//- (BOOL)validateElapsedMinutes:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* endFinal;



@property int64_t endFinalValue;
- (int64_t)endFinalValue;
- (void)setEndFinalValue:(int64_t)value_;

//- (BOOL)validateEndFinal:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idLocalTeam;



@property int32_t idLocalTeamValue;
- (int32_t)idLocalTeamValue;
- (void)setIdLocalTeamValue:(int32_t)value_;

//- (BOOL)validateIdLocalTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idMatch;



@property int64_t idMatchValue;
- (int64_t)idMatchValue;
- (void)setIdMatchValue:(int64_t)value_;

//- (BOOL)validateIdMatch:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idPreviousMatch;



@property int64_t idPreviousMatchValue;
- (int64_t)idPreviousMatchValue;
- (void)setIdPreviousMatchValue:(int64_t)value_;

//- (BOOL)validateIdPreviousMatch:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idRound;



@property int32_t idRoundValue;
- (int32_t)idRoundValue;
- (void)setIdRoundValue:(int32_t)value_;

//- (BOOL)validateIdRound:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idVisitorTeam;



@property int32_t idVisitorTeamValue;
- (int32_t)idVisitorTeamValue;
- (void)setIdVisitorTeamValue:(int32_t)value_;

//- (BOOL)validateIdVisitorTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idWinnerTeam;



@property int32_t idWinnerTeamValue;
- (int32_t)idWinnerTeamValue;
- (void)setIdWinnerTeamValue:(int32_t)value_;

//- (BOOL)validateIdWinnerTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* listTV;



//- (BOOL)validateListTV:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* localScore;



@property int16_t localScoreValue;
- (int16_t)localScoreValue;
- (void)setLocalScoreValue:(int16_t)value_;

//- (BOOL)validateLocalScore:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* matchDate;



@property int64_t matchDateValue;
- (int64_t)matchDateValue;
- (void)setMatchDateValue:(int64_t)value_;

//- (BOOL)validateMatchDate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* matchState;



@property int16_t matchStateValue;
- (int16_t)matchStateValue;
- (void)setMatchStateValue:(int16_t)value_;

//- (BOOL)validateMatchState:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* matchSubstate;



@property int16_t matchSubstateValue;
- (int16_t)matchSubstateValue;
- (void)setMatchSubstateValue:(int16_t)value_;

//- (BOOL)validateMatchSubstate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* matchType;



@property int16_t matchTypeValue;
- (int16_t)matchTypeValue;
- (void)setMatchTypeValue:(int16_t)value_;

//- (BOOL)validateMatchType:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* notConfirmedMatchDate;



@property BOOL notConfirmedMatchDateValue;
- (BOOL)notConfirmedMatchDateValue;
- (void)setNotConfirmedMatchDateValue:(BOOL)value_;

//- (BOOL)validateNotConfirmedMatchDate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* order;



@property int64_t orderValue;
- (int64_t)orderValue;
- (void)setOrderValue:(int64_t)value_;

//- (BOOL)validateOrder:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* overTimeStartDate;



@property int64_t overTimeStartDateValue;
- (int64_t)overTimeStartDateValue;
- (void)setOverTimeStartDateValue:(int64_t)value_;

//- (BOOL)validateOverTimeStartDate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* previousMatchScore;



//- (BOOL)validatePreviousMatchScore:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* scorePenaltiesLocalTeam;



@property int64_t scorePenaltiesLocalTeamValue;
- (int64_t)scorePenaltiesLocalTeamValue;
- (void)setScorePenaltiesLocalTeamValue:(int64_t)value_;

//- (BOOL)validateScorePenaltiesLocalTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* scorePenaltiesVisitorTeam;



@property int64_t scorePenaltiesVisitorTeamValue;
- (int64_t)scorePenaltiesVisitorTeamValue;
- (void)setScorePenaltiesVisitorTeamValue:(int64_t)value_;

//- (BOOL)validateScorePenaltiesVisitorTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* secondHalfStartDate;



@property int64_t secondHalfStartDateValue;
- (int64_t)secondHalfStartDateValue;
- (void)setSecondHalfStartDateValue:(int64_t)value_;

//- (BOOL)validateSecondHalfStartDate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* startDate;



@property int64_t startDateValue;
- (int64_t)startDateValue;
- (void)setStartDateValue:(int64_t)value_;

//- (BOOL)validateStartDate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* twitterLocal;



@property BOOL twitterLocalValue;
- (BOOL)twitterLocalValue;
- (void)setTwitterLocalValue:(BOOL)value_;

//- (BOOL)validateTwitterLocal:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* twitterVisitor;



@property BOOL twitterVisitorValue;
- (BOOL)twitterVisitorValue;
- (void)setTwitterVisitorValue:(BOOL)value_;

//- (BOOL)validateTwitterVisitor:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* visitorScore;



@property int16_t visitorScoreValue;
- (int16_t)visitorScoreValue;
- (void)setVisitorScoreValue:(int16_t)value_;

//- (BOOL)validateVisitorScore:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSSet *eventsOfMatch;

- (NSMutableSet*)eventsOfMatchSet;




@property (nonatomic, strong) LineUp *lineUpLocal;

//- (BOOL)validateLineUpLocal:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) LineUp *lineUpVisitor;

//- (BOOL)validateLineUpVisitor:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) NSSet *matchBetType;

- (NSMutableSet*)matchBetTypeSet;




@property (nonatomic, strong) Round *round;

//- (BOOL)validateRound:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Subscription *subscription;

//- (BOOL)validateSubscription:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Team *teamLocal;

//- (BOOL)validateTeamLocal:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Team *teamVisitor;

//- (BOOL)validateTeamVisitor:(id*)value_ error:(NSError**)error_;





@end

@interface _Match (CoreDataGeneratedAccessors)

- (void)addEventsOfMatch:(NSSet*)value_;
- (void)removeEventsOfMatch:(NSSet*)value_;
- (void)addEventsOfMatchObject:(EventOfMatch*)value_;
- (void)removeEventsOfMatchObject:(EventOfMatch*)value_;

- (void)addMatchBetType:(NSSet*)value_;
- (void)removeMatchBetType:(NSSet*)value_;
- (void)addMatchBetTypeObject:(MatchBetType*)value_;
- (void)removeMatchBetTypeObject:(MatchBetType*)value_;

@end

@interface _Match (CoreDataGeneratedPrimitiveAccessors)


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




- (NSNumber*)primitiveElapsedMinutes;
- (void)setPrimitiveElapsedMinutes:(NSNumber*)value;

- (int32_t)primitiveElapsedMinutesValue;
- (void)setPrimitiveElapsedMinutesValue:(int32_t)value_;




- (NSNumber*)primitiveEndFinal;
- (void)setPrimitiveEndFinal:(NSNumber*)value;

- (int64_t)primitiveEndFinalValue;
- (void)setPrimitiveEndFinalValue:(int64_t)value_;




- (NSNumber*)primitiveIdLocalTeam;
- (void)setPrimitiveIdLocalTeam:(NSNumber*)value;

- (int32_t)primitiveIdLocalTeamValue;
- (void)setPrimitiveIdLocalTeamValue:(int32_t)value_;




- (NSNumber*)primitiveIdMatch;
- (void)setPrimitiveIdMatch:(NSNumber*)value;

- (int64_t)primitiveIdMatchValue;
- (void)setPrimitiveIdMatchValue:(int64_t)value_;




- (NSNumber*)primitiveIdPreviousMatch;
- (void)setPrimitiveIdPreviousMatch:(NSNumber*)value;

- (int64_t)primitiveIdPreviousMatchValue;
- (void)setPrimitiveIdPreviousMatchValue:(int64_t)value_;




- (NSNumber*)primitiveIdRound;
- (void)setPrimitiveIdRound:(NSNumber*)value;

- (int32_t)primitiveIdRoundValue;
- (void)setPrimitiveIdRoundValue:(int32_t)value_;




- (NSNumber*)primitiveIdVisitorTeam;
- (void)setPrimitiveIdVisitorTeam:(NSNumber*)value;

- (int32_t)primitiveIdVisitorTeamValue;
- (void)setPrimitiveIdVisitorTeamValue:(int32_t)value_;




- (NSNumber*)primitiveIdWinnerTeam;
- (void)setPrimitiveIdWinnerTeam:(NSNumber*)value;

- (int32_t)primitiveIdWinnerTeamValue;
- (void)setPrimitiveIdWinnerTeamValue:(int32_t)value_;




- (NSString*)primitiveListTV;
- (void)setPrimitiveListTV:(NSString*)value;




- (NSNumber*)primitiveLocalScore;
- (void)setPrimitiveLocalScore:(NSNumber*)value;

- (int16_t)primitiveLocalScoreValue;
- (void)setPrimitiveLocalScoreValue:(int16_t)value_;




- (NSNumber*)primitiveMatchDate;
- (void)setPrimitiveMatchDate:(NSNumber*)value;

- (int64_t)primitiveMatchDateValue;
- (void)setPrimitiveMatchDateValue:(int64_t)value_;




- (NSNumber*)primitiveMatchState;
- (void)setPrimitiveMatchState:(NSNumber*)value;

- (int16_t)primitiveMatchStateValue;
- (void)setPrimitiveMatchStateValue:(int16_t)value_;




- (NSNumber*)primitiveMatchSubstate;
- (void)setPrimitiveMatchSubstate:(NSNumber*)value;

- (int16_t)primitiveMatchSubstateValue;
- (void)setPrimitiveMatchSubstateValue:(int16_t)value_;




- (NSNumber*)primitiveMatchType;
- (void)setPrimitiveMatchType:(NSNumber*)value;

- (int16_t)primitiveMatchTypeValue;
- (void)setPrimitiveMatchTypeValue:(int16_t)value_;




- (NSNumber*)primitiveNotConfirmedMatchDate;
- (void)setPrimitiveNotConfirmedMatchDate:(NSNumber*)value;

- (BOOL)primitiveNotConfirmedMatchDateValue;
- (void)setPrimitiveNotConfirmedMatchDateValue:(BOOL)value_;




- (NSNumber*)primitiveOrder;
- (void)setPrimitiveOrder:(NSNumber*)value;

- (int64_t)primitiveOrderValue;
- (void)setPrimitiveOrderValue:(int64_t)value_;




- (NSNumber*)primitiveOverTimeStartDate;
- (void)setPrimitiveOverTimeStartDate:(NSNumber*)value;

- (int64_t)primitiveOverTimeStartDateValue;
- (void)setPrimitiveOverTimeStartDateValue:(int64_t)value_;




- (NSString*)primitivePreviousMatchScore;
- (void)setPrimitivePreviousMatchScore:(NSString*)value;




- (NSNumber*)primitiveScorePenaltiesLocalTeam;
- (void)setPrimitiveScorePenaltiesLocalTeam:(NSNumber*)value;

- (int64_t)primitiveScorePenaltiesLocalTeamValue;
- (void)setPrimitiveScorePenaltiesLocalTeamValue:(int64_t)value_;




- (NSNumber*)primitiveScorePenaltiesVisitorTeam;
- (void)setPrimitiveScorePenaltiesVisitorTeam:(NSNumber*)value;

- (int64_t)primitiveScorePenaltiesVisitorTeamValue;
- (void)setPrimitiveScorePenaltiesVisitorTeamValue:(int64_t)value_;




- (NSNumber*)primitiveSecondHalfStartDate;
- (void)setPrimitiveSecondHalfStartDate:(NSNumber*)value;

- (int64_t)primitiveSecondHalfStartDateValue;
- (void)setPrimitiveSecondHalfStartDateValue:(int64_t)value_;




- (NSNumber*)primitiveStartDate;
- (void)setPrimitiveStartDate:(NSNumber*)value;

- (int64_t)primitiveStartDateValue;
- (void)setPrimitiveStartDateValue:(int64_t)value_;




- (NSNumber*)primitiveTwitterLocal;
- (void)setPrimitiveTwitterLocal:(NSNumber*)value;

- (BOOL)primitiveTwitterLocalValue;
- (void)setPrimitiveTwitterLocalValue:(BOOL)value_;




- (NSNumber*)primitiveTwitterVisitor;
- (void)setPrimitiveTwitterVisitor:(NSNumber*)value;

- (BOOL)primitiveTwitterVisitorValue;
- (void)setPrimitiveTwitterVisitorValue:(BOOL)value_;




- (NSNumber*)primitiveVisitorScore;
- (void)setPrimitiveVisitorScore:(NSNumber*)value;

- (int16_t)primitiveVisitorScoreValue;
- (void)setPrimitiveVisitorScoreValue:(int16_t)value_;





- (NSMutableSet*)primitiveEventsOfMatch;
- (void)setPrimitiveEventsOfMatch:(NSMutableSet*)value;



- (LineUp*)primitiveLineUpLocal;
- (void)setPrimitiveLineUpLocal:(LineUp*)value;



- (LineUp*)primitiveLineUpVisitor;
- (void)setPrimitiveLineUpVisitor:(LineUp*)value;



- (NSMutableSet*)primitiveMatchBetType;
- (void)setPrimitiveMatchBetType:(NSMutableSet*)value;



- (Round*)primitiveRound;
- (void)setPrimitiveRound:(Round*)value;



- (Subscription*)primitiveSubscription;
- (void)setPrimitiveSubscription:(Subscription*)value;



- (Team*)primitiveTeamLocal;
- (void)setPrimitiveTeamLocal:(Team*)value;



- (Team*)primitiveTeamVisitor;
- (void)setPrimitiveTeamVisitor:(Team*)value;


@end
