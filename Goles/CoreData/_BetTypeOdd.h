// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to BetTypeOdd.h instead.

#import <CoreData/CoreData.h>


extern const struct BetTypeOddAttributes {
	__unsafe_unretained NSString *comment;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idBetTypeOdd;
	__unsafe_unretained NSString *idMatchBetType;
	__unsafe_unretained NSString *name;
	__unsafe_unretained NSString *readyToDelete;
	__unsafe_unretained NSString *url;
	__unsafe_unretained NSString *value;
} BetTypeOddAttributes;

extern const struct BetTypeOddRelationships {
	__unsafe_unretained NSString *matchBetType;
} BetTypeOddRelationships;

extern const struct BetTypeOddFetchedProperties {
} BetTypeOddFetchedProperties;

@class MatchBetType;














@interface BetTypeOddID : NSManagedObjectID {}
@end

@interface _BetTypeOdd : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (BetTypeOddID*)objectID;





@property (nonatomic, strong) NSString* comment;



//- (BOOL)validateComment:(id*)value_ error:(NSError**)error_;





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





@property (nonatomic, strong) NSNumber* idBetTypeOdd;



@property int64_t idBetTypeOddValue;
- (int64_t)idBetTypeOddValue;
- (void)setIdBetTypeOddValue:(int64_t)value_;

//- (BOOL)validateIdBetTypeOdd:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idMatchBetType;



@property int32_t idMatchBetTypeValue;
- (int32_t)idMatchBetTypeValue;
- (void)setIdMatchBetTypeValue:(int32_t)value_;

//- (BOOL)validateIdMatchBetType:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* name;



//- (BOOL)validateName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* readyToDelete;



@property BOOL readyToDeleteValue;
- (BOOL)readyToDeleteValue;
- (void)setReadyToDeleteValue:(BOOL)value_;

//- (BOOL)validateReadyToDelete:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* url;



//- (BOOL)validateUrl:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* value;



@property float valueValue;
- (float)valueValue;
- (void)setValueValue:(float)value_;

//- (BOOL)validateValue:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) MatchBetType *matchBetType;

//- (BOOL)validateMatchBetType:(id*)value_ error:(NSError**)error_;





@end

@interface _BetTypeOdd (CoreDataGeneratedAccessors)

@end

@interface _BetTypeOdd (CoreDataGeneratedPrimitiveAccessors)


- (NSString*)primitiveComment;
- (void)setPrimitiveComment:(NSString*)value;




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




- (NSNumber*)primitiveIdBetTypeOdd;
- (void)setPrimitiveIdBetTypeOdd:(NSNumber*)value;

- (int64_t)primitiveIdBetTypeOddValue;
- (void)setPrimitiveIdBetTypeOddValue:(int64_t)value_;




- (NSNumber*)primitiveIdMatchBetType;
- (void)setPrimitiveIdMatchBetType:(NSNumber*)value;

- (int32_t)primitiveIdMatchBetTypeValue;
- (void)setPrimitiveIdMatchBetTypeValue:(int32_t)value_;




- (NSString*)primitiveName;
- (void)setPrimitiveName:(NSString*)value;




- (NSNumber*)primitiveReadyToDelete;
- (void)setPrimitiveReadyToDelete:(NSNumber*)value;

- (BOOL)primitiveReadyToDeleteValue;
- (void)setPrimitiveReadyToDeleteValue:(BOOL)value_;




- (NSString*)primitiveUrl;
- (void)setPrimitiveUrl:(NSString*)value;




- (NSNumber*)primitiveValue;
- (void)setPrimitiveValue:(NSNumber*)value;

- (float)primitiveValueValue;
- (void)setPrimitiveValueValue:(float)value_;





- (MatchBetType*)primitiveMatchBetType;
- (void)setPrimitiveMatchBetType:(MatchBetType*)value;


@end
