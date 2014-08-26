// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Subscription.h instead.

#import <CoreData/CoreData.h>


extern const struct SubscriptionAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idAllEvents;
	__unsafe_unretained NSString *idDevice;
	__unsafe_unretained NSString *idSubscription;
	__unsafe_unretained NSString *negation;
	__unsafe_unretained NSString *tipus;
} SubscriptionAttributes;

extern const struct SubscriptionRelationships {
	__unsafe_unretained NSString *match;
	__unsafe_unretained NSString *sml;
	__unsafe_unretained NSString *team;
} SubscriptionRelationships;

extern const struct SubscriptionFetchedProperties {
} SubscriptionFetchedProperties;

@class Match;
@class SML;
@class Team;












@interface SubscriptionID : NSManagedObjectID {}
@end

@interface _Subscription : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (SubscriptionID*)objectID;





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





@property (nonatomic, strong) NSNumber* idAllEvents;



@property int32_t idAllEventsValue;
- (int32_t)idAllEventsValue;
- (void)setIdAllEventsValue:(int32_t)value_;

//- (BOOL)validateIdAllEvents:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idDevice;



@property int64_t idDeviceValue;
- (int64_t)idDeviceValue;
- (void)setIdDeviceValue:(int64_t)value_;

//- (BOOL)validateIdDevice:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idSubscription;



@property int64_t idSubscriptionValue;
- (int64_t)idSubscriptionValue;
- (void)setIdSubscriptionValue:(int64_t)value_;

//- (BOOL)validateIdSubscription:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* negation;



@property BOOL negationValue;
- (BOOL)negationValue;
- (void)setNegationValue:(BOOL)value_;

//- (BOOL)validateNegation:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* tipus;



@property int32_t tipusValue;
- (int32_t)tipusValue;
- (void)setTipusValue:(int32_t)value_;

//- (BOOL)validateTipus:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) Match *match;

//- (BOOL)validateMatch:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) SML *sml;

//- (BOOL)validateSml:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) Team *team;

//- (BOOL)validateTeam:(id*)value_ error:(NSError**)error_;





@end

@interface _Subscription (CoreDataGeneratedAccessors)

@end

@interface _Subscription (CoreDataGeneratedPrimitiveAccessors)


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




- (NSNumber*)primitiveIdAllEvents;
- (void)setPrimitiveIdAllEvents:(NSNumber*)value;

- (int32_t)primitiveIdAllEventsValue;
- (void)setPrimitiveIdAllEventsValue:(int32_t)value_;




- (NSNumber*)primitiveIdDevice;
- (void)setPrimitiveIdDevice:(NSNumber*)value;

- (int64_t)primitiveIdDeviceValue;
- (void)setPrimitiveIdDeviceValue:(int64_t)value_;




- (NSNumber*)primitiveIdSubscription;
- (void)setPrimitiveIdSubscription:(NSNumber*)value;

- (int64_t)primitiveIdSubscriptionValue;
- (void)setPrimitiveIdSubscriptionValue:(int64_t)value_;




- (NSNumber*)primitiveNegation;
- (void)setPrimitiveNegation:(NSNumber*)value;

- (BOOL)primitiveNegationValue;
- (void)setPrimitiveNegationValue:(BOOL)value_;




- (NSNumber*)primitiveTipus;
- (void)setPrimitiveTipus:(NSNumber*)value;

- (int32_t)primitiveTipusValue;
- (void)setPrimitiveTipusValue:(int32_t)value_;





- (Match*)primitiveMatch;
- (void)setPrimitiveMatch:(Match*)value;



- (SML*)primitiveSml;
- (void)setPrimitiveSml:(SML*)value;



- (Team*)primitiveTeam;
- (void)setPrimitiveTeam:(Team*)value;


@end
