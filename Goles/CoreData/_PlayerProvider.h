// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to PlayerProvider.h instead.

#import <CoreData/CoreData.h>


extern const struct PlayerProviderAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idPlayerProvider;
	__unsafe_unretained NSString *status;
	__unsafe_unretained NSString *visible;
	__unsafe_unretained NSString *weight;
} PlayerProviderAttributes;

extern const struct PlayerProviderRelationships {
	__unsafe_unretained NSString *player;
	__unsafe_unretained NSString *provider;
} PlayerProviderRelationships;

extern const struct PlayerProviderFetchedProperties {
} PlayerProviderFetchedProperties;

@class Player;
@class Provider;











@interface PlayerProviderID : NSManagedObjectID {}
@end

@interface _PlayerProvider : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (PlayerProviderID*)objectID;





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





@property (nonatomic, strong) NSNumber* idPlayerProvider;



@property int64_t idPlayerProviderValue;
- (int64_t)idPlayerProviderValue;
- (void)setIdPlayerProviderValue:(int64_t)value_;

//- (BOOL)validateIdPlayerProvider:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* status;



@property int16_t statusValue;
- (int16_t)statusValue;
- (void)setStatusValue:(int16_t)value_;

//- (BOOL)validateStatus:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* visible;



@property BOOL visibleValue;
- (BOOL)visibleValue;
- (void)setVisibleValue:(BOOL)value_;

//- (BOOL)validateVisible:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* weight;



@property int16_t weightValue;
- (int16_t)weightValue;
- (void)setWeightValue:(int16_t)value_;

//- (BOOL)validateWeight:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) Player *player;

//- (BOOL)validatePlayer:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Provider *provider;

//- (BOOL)validateProvider:(id*)value_ error:(NSError**)error_;





@end

@interface _PlayerProvider (CoreDataGeneratedAccessors)

@end

@interface _PlayerProvider (CoreDataGeneratedPrimitiveAccessors)


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




- (NSNumber*)primitiveIdPlayerProvider;
- (void)setPrimitiveIdPlayerProvider:(NSNumber*)value;

- (int64_t)primitiveIdPlayerProviderValue;
- (void)setPrimitiveIdPlayerProviderValue:(int64_t)value_;




- (NSNumber*)primitiveStatus;
- (void)setPrimitiveStatus:(NSNumber*)value;

- (int16_t)primitiveStatusValue;
- (void)setPrimitiveStatusValue:(int16_t)value_;




- (NSNumber*)primitiveVisible;
- (void)setPrimitiveVisible:(NSNumber*)value;

- (BOOL)primitiveVisibleValue;
- (void)setPrimitiveVisibleValue:(BOOL)value_;




- (NSNumber*)primitiveWeight;
- (void)setPrimitiveWeight:(NSNumber*)value;

- (int16_t)primitiveWeightValue;
- (void)setPrimitiveWeightValue:(int16_t)value_;





- (Player*)primitivePlayer;
- (void)setPrimitivePlayer:(Player*)value;



- (Provider*)primitiveProvider;
- (void)setPrimitiveProvider:(Provider*)value;


@end
