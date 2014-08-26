// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Provider.h instead.

#import <CoreData/CoreData.h>


extern const struct ProviderAttributes {
	__unsafe_unretained NSString *activePorra;
	__unsafe_unretained NSString *comment;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *disclaimer;
	__unsafe_unretained NSString *idProvider;
	__unsafe_unretained NSString *name;
	__unsafe_unretained NSString *registryURL;
	__unsafe_unretained NSString *uniqueKey;
	__unsafe_unretained NSString *visibleIOS;
	__unsafe_unretained NSString *weight;
} ProviderAttributes;

extern const struct ProviderRelationships {
	__unsafe_unretained NSString *matchBetType;
	__unsafe_unretained NSString *playerProvider;
} ProviderRelationships;

extern const struct ProviderFetchedProperties {
} ProviderFetchedProperties;

@class MatchBetType;
@class PlayerProvider;
















@interface ProviderID : NSManagedObjectID {}
@end

@interface _Provider : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (ProviderID*)objectID;





@property (nonatomic, strong) NSNumber* activePorra;



@property BOOL activePorraValue;
- (BOOL)activePorraValue;
- (void)setActivePorraValue:(BOOL)value_;

//- (BOOL)validateActivePorra:(id*)value_ error:(NSError**)error_;





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





@property (nonatomic, strong) NSString* disclaimer;



//- (BOOL)validateDisclaimer:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idProvider;



@property int64_t idProviderValue;
- (int64_t)idProviderValue;
- (void)setIdProviderValue:(int64_t)value_;

//- (BOOL)validateIdProvider:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* name;



//- (BOOL)validateName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* registryURL;



//- (BOOL)validateRegistryURL:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* uniqueKey;



//- (BOOL)validateUniqueKey:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* visibleIOS;



@property BOOL visibleIOSValue;
- (BOOL)visibleIOSValue;
- (void)setVisibleIOSValue:(BOOL)value_;

//- (BOOL)validateVisibleIOS:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* weight;



@property int16_t weightValue;
- (int16_t)weightValue;
- (void)setWeightValue:(int16_t)value_;

//- (BOOL)validateWeight:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSSet *matchBetType;

- (NSMutableSet*)matchBetTypeSet;




@property (nonatomic, strong) NSSet *playerProvider;

- (NSMutableSet*)playerProviderSet;





@end

@interface _Provider (CoreDataGeneratedAccessors)

- (void)addMatchBetType:(NSSet*)value_;
- (void)removeMatchBetType:(NSSet*)value_;
- (void)addMatchBetTypeObject:(MatchBetType*)value_;
- (void)removeMatchBetTypeObject:(MatchBetType*)value_;

- (void)addPlayerProvider:(NSSet*)value_;
- (void)removePlayerProvider:(NSSet*)value_;
- (void)addPlayerProviderObject:(PlayerProvider*)value_;
- (void)removePlayerProviderObject:(PlayerProvider*)value_;

@end

@interface _Provider (CoreDataGeneratedPrimitiveAccessors)


- (NSNumber*)primitiveActivePorra;
- (void)setPrimitiveActivePorra:(NSNumber*)value;

- (BOOL)primitiveActivePorraValue;
- (void)setPrimitiveActivePorraValue:(BOOL)value_;




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




- (NSString*)primitiveDisclaimer;
- (void)setPrimitiveDisclaimer:(NSString*)value;




- (NSNumber*)primitiveIdProvider;
- (void)setPrimitiveIdProvider:(NSNumber*)value;

- (int64_t)primitiveIdProviderValue;
- (void)setPrimitiveIdProviderValue:(int64_t)value_;




- (NSString*)primitiveName;
- (void)setPrimitiveName:(NSString*)value;




- (NSString*)primitiveRegistryURL;
- (void)setPrimitiveRegistryURL:(NSString*)value;




- (NSString*)primitiveUniqueKey;
- (void)setPrimitiveUniqueKey:(NSString*)value;




- (NSNumber*)primitiveVisibleIOS;
- (void)setPrimitiveVisibleIOS:(NSNumber*)value;

- (BOOL)primitiveVisibleIOSValue;
- (void)setPrimitiveVisibleIOSValue:(BOOL)value_;




- (NSNumber*)primitiveWeight;
- (void)setPrimitiveWeight:(NSNumber*)value;

- (int16_t)primitiveWeightValue;
- (void)setPrimitiveWeightValue:(int16_t)value_;





- (NSMutableSet*)primitiveMatchBetType;
- (void)setPrimitiveMatchBetType:(NSMutableSet*)value;



- (NSMutableSet*)primitivePlayerProvider;
- (void)setPrimitivePlayerProvider:(NSMutableSet*)value;


@end
