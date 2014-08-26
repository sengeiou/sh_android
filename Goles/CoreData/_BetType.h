// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to BetType.h instead.

#import <CoreData/CoreData.h>


extern const struct BetTypeAttributes {
	__unsafe_unretained NSString *alwaysVisible;
	__unsafe_unretained NSString *comment;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idBetType;
	__unsafe_unretained NSString *name;
	__unsafe_unretained NSString *title;
	__unsafe_unretained NSString *uniqueKey;
	__unsafe_unretained NSString *weight;
} BetTypeAttributes;

extern const struct BetTypeRelationships {
	__unsafe_unretained NSString *matchBetType;
} BetTypeRelationships;

extern const struct BetTypeFetchedProperties {
} BetTypeFetchedProperties;

@class MatchBetType;














@interface BetTypeID : NSManagedObjectID {}
@end

@interface _BetType : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (BetTypeID*)objectID;





@property (nonatomic, strong) NSNumber* alwaysVisible;



@property BOOL alwaysVisibleValue;
- (BOOL)alwaysVisibleValue;
- (void)setAlwaysVisibleValue:(BOOL)value_;

//- (BOOL)validateAlwaysVisible:(id*)value_ error:(NSError**)error_;





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





@property (nonatomic, strong) NSNumber* idBetType;



@property int64_t idBetTypeValue;
- (int64_t)idBetTypeValue;
- (void)setIdBetTypeValue:(int64_t)value_;

//- (BOOL)validateIdBetType:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* name;



//- (BOOL)validateName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* title;



//- (BOOL)validateTitle:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* uniqueKey;



//- (BOOL)validateUniqueKey:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* weight;



@property int16_t weightValue;
- (int16_t)weightValue;
- (void)setWeightValue:(int16_t)value_;

//- (BOOL)validateWeight:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSSet *matchBetType;

- (NSMutableSet*)matchBetTypeSet;





@end

@interface _BetType (CoreDataGeneratedAccessors)

- (void)addMatchBetType:(NSSet*)value_;
- (void)removeMatchBetType:(NSSet*)value_;
- (void)addMatchBetTypeObject:(MatchBetType*)value_;
- (void)removeMatchBetTypeObject:(MatchBetType*)value_;

@end

@interface _BetType (CoreDataGeneratedPrimitiveAccessors)


- (NSNumber*)primitiveAlwaysVisible;
- (void)setPrimitiveAlwaysVisible:(NSNumber*)value;

- (BOOL)primitiveAlwaysVisibleValue;
- (void)setPrimitiveAlwaysVisibleValue:(BOOL)value_;




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




- (NSNumber*)primitiveIdBetType;
- (void)setPrimitiveIdBetType:(NSNumber*)value;

- (int64_t)primitiveIdBetTypeValue;
- (void)setPrimitiveIdBetTypeValue:(int64_t)value_;




- (NSString*)primitiveName;
- (void)setPrimitiveName:(NSString*)value;




- (NSString*)primitiveTitle;
- (void)setPrimitiveTitle:(NSString*)value;




- (NSString*)primitiveUniqueKey;
- (void)setPrimitiveUniqueKey:(NSString*)value;




- (NSNumber*)primitiveWeight;
- (void)setPrimitiveWeight:(NSNumber*)value;

- (int16_t)primitiveWeightValue;
- (void)setPrimitiveWeightValue:(int16_t)value_;





- (NSMutableSet*)primitiveMatchBetType;
- (void)setPrimitiveMatchBetType:(NSMutableSet*)value;


@end
