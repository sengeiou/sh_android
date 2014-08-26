// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to LineUp.h instead.

#import <CoreData/CoreData.h>


extern const struct LineUpAttributes {
	__unsafe_unretained NSString *coach;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *defenders;
	__unsafe_unretained NSString *formation;
	__unsafe_unretained NSString *goalkeeper;
	__unsafe_unretained NSString *idLineUp;
	__unsafe_unretained NSString *midfielder01;
	__unsafe_unretained NSString *midfielder02;
	__unsafe_unretained NSString *reserve;
	__unsafe_unretained NSString *striker;
} LineUpAttributes;

extern const struct LineUpRelationships {
	__unsafe_unretained NSString *matchAsLocal;
	__unsafe_unretained NSString *matchAsVisitor;
	__unsafe_unretained NSString *team;
} LineUpRelationships;

extern const struct LineUpFetchedProperties {
} LineUpFetchedProperties;

@class Match;
@class Match;
@class Team;
















@interface LineUpID : NSManagedObjectID {}
@end

@interface _LineUp : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (LineUpID*)objectID;





@property (nonatomic, strong) NSString* coach;



//- (BOOL)validateCoach:(id*)value_ error:(NSError**)error_;





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





@property (nonatomic, strong) NSString* defenders;



//- (BOOL)validateDefenders:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* formation;



//- (BOOL)validateFormation:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* goalkeeper;



//- (BOOL)validateGoalkeeper:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idLineUp;



@property int64_t idLineUpValue;
- (int64_t)idLineUpValue;
- (void)setIdLineUpValue:(int64_t)value_;

//- (BOOL)validateIdLineUp:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* midfielder01;



//- (BOOL)validateMidfielder01:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* midfielder02;



//- (BOOL)validateMidfielder02:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* reserve;



//- (BOOL)validateReserve:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* striker;



//- (BOOL)validateStriker:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) Match *matchAsLocal;

//- (BOOL)validateMatchAsLocal:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Match *matchAsVisitor;

//- (BOOL)validateMatchAsVisitor:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Team *team;

//- (BOOL)validateTeam:(id*)value_ error:(NSError**)error_;





@end

@interface _LineUp (CoreDataGeneratedAccessors)

@end

@interface _LineUp (CoreDataGeneratedPrimitiveAccessors)


- (NSString*)primitiveCoach;
- (void)setPrimitiveCoach:(NSString*)value;




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




- (NSString*)primitiveDefenders;
- (void)setPrimitiveDefenders:(NSString*)value;




- (NSString*)primitiveFormation;
- (void)setPrimitiveFormation:(NSString*)value;




- (NSString*)primitiveGoalkeeper;
- (void)setPrimitiveGoalkeeper:(NSString*)value;




- (NSNumber*)primitiveIdLineUp;
- (void)setPrimitiveIdLineUp:(NSNumber*)value;

- (int64_t)primitiveIdLineUpValue;
- (void)setPrimitiveIdLineUpValue:(int64_t)value_;




- (NSString*)primitiveMidfielder01;
- (void)setPrimitiveMidfielder01:(NSString*)value;




- (NSString*)primitiveMidfielder02;
- (void)setPrimitiveMidfielder02:(NSString*)value;




- (NSString*)primitiveReserve;
- (void)setPrimitiveReserve:(NSString*)value;




- (NSString*)primitiveStriker;
- (void)setPrimitiveStriker:(NSString*)value;





- (Match*)primitiveMatchAsLocal;
- (void)setPrimitiveMatchAsLocal:(Match*)value;



- (Match*)primitiveMatchAsVisitor;
- (void)setPrimitiveMatchAsVisitor:(Match*)value;



- (Team*)primitiveTeam;
- (void)setPrimitiveTeam:(Team*)value;


@end
