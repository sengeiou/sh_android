// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to MatchBetType.h instead.

#import <CoreData/CoreData.h>


extern const struct MatchBetTypeAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idMatchBetType;
} MatchBetTypeAttributes;

extern const struct MatchBetTypeRelationships {
	__unsafe_unretained NSString *betType;
	__unsafe_unretained NSString *betTypeOdds;
	__unsafe_unretained NSString *match;
	__unsafe_unretained NSString *provider;
} MatchBetTypeRelationships;

extern const struct MatchBetTypeFetchedProperties {
} MatchBetTypeFetchedProperties;

@class BetType;
@class BetTypeOdd;
@class Match;
@class Provider;








@interface MatchBetTypeID : NSManagedObjectID {}
@end

@interface _MatchBetType : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (MatchBetTypeID*)objectID;





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





@property (nonatomic, strong) NSNumber* idMatchBetType;



@property int64_t idMatchBetTypeValue;
- (int64_t)idMatchBetTypeValue;
- (void)setIdMatchBetTypeValue:(int64_t)value_;

//- (BOOL)validateIdMatchBetType:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) BetType *betType;

//- (BOOL)validateBetType:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) NSSet *betTypeOdds;

- (NSMutableSet*)betTypeOddsSet;




@property (nonatomic, strong) Match *match;

//- (BOOL)validateMatch:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Provider *provider;

//- (BOOL)validateProvider:(id*)value_ error:(NSError**)error_;





@end

@interface _MatchBetType (CoreDataGeneratedAccessors)

- (void)addBetTypeOdds:(NSSet*)value_;
- (void)removeBetTypeOdds:(NSSet*)value_;
- (void)addBetTypeOddsObject:(BetTypeOdd*)value_;
- (void)removeBetTypeOddsObject:(BetTypeOdd*)value_;

@end

@interface _MatchBetType (CoreDataGeneratedPrimitiveAccessors)


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




- (NSNumber*)primitiveIdMatchBetType;
- (void)setPrimitiveIdMatchBetType:(NSNumber*)value;

- (int64_t)primitiveIdMatchBetTypeValue;
- (void)setPrimitiveIdMatchBetTypeValue:(int64_t)value_;





- (BetType*)primitiveBetType;
- (void)setPrimitiveBetType:(BetType*)value;



- (NSMutableSet*)primitiveBetTypeOdds;
- (void)setPrimitiveBetTypeOdds:(NSMutableSet*)value;



- (Match*)primitiveMatch;
- (void)setPrimitiveMatch:(Match*)value;



- (Provider*)primitiveProvider;
- (void)setPrimitiveProvider:(Provider*)value;


@end
