// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Watch.h instead.

#import <CoreData/CoreData.h>


extern const struct WatchAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *status;
} WatchAttributes;

extern const struct WatchRelationships {
	__unsafe_unretained NSString *match;
	__unsafe_unretained NSString *user;
} WatchRelationships;

extern const struct WatchFetchedProperties {
} WatchFetchedProperties;

@class Match;
@class User;








@interface WatchID : NSManagedObjectID {}
@end

@interface _Watch : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (WatchID*)objectID;





@property (nonatomic, strong) NSNumber* csys_birth;



@property int64_t csys_birthValue;
- (int64_t)csys_birthValue;
- (void)setCsys_birthValue:(int64_t)value_;

//- (BOOL)validateCsys_birth:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* csys_deleted;



@property int64_t csys_deletedValue;
- (int64_t)csys_deletedValue;
- (void)setCsys_deletedValue:(int64_t)value_;

//- (BOOL)validateCsys_deleted:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* csys_modified;



@property int64_t csys_modifiedValue;
- (int64_t)csys_modifiedValue;
- (void)setCsys_modifiedValue:(int64_t)value_;

//- (BOOL)validateCsys_modified:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* csys_revision;



@property int64_t csys_revisionValue;
- (int64_t)csys_revisionValue;
- (void)setCsys_revisionValue:(int64_t)value_;

//- (BOOL)validateCsys_revision:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* csys_syncronized;



//- (BOOL)validateCsys_syncronized:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* status;



@property int16_t statusValue;
- (int16_t)statusValue;
- (void)setStatusValue:(int16_t)value_;

//- (BOOL)validateStatus:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) Match *match;

//- (BOOL)validateMatch:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) User *user;

//- (BOOL)validateUser:(id*)value_ error:(NSError**)error_;





@end

@interface _Watch (CoreDataGeneratedAccessors)

@end

@interface _Watch (CoreDataGeneratedPrimitiveAccessors)


- (NSNumber*)primitiveCsys_birth;
- (void)setPrimitiveCsys_birth:(NSNumber*)value;

- (int64_t)primitiveCsys_birthValue;
- (void)setPrimitiveCsys_birthValue:(int64_t)value_;




- (NSNumber*)primitiveCsys_deleted;
- (void)setPrimitiveCsys_deleted:(NSNumber*)value;

- (int64_t)primitiveCsys_deletedValue;
- (void)setPrimitiveCsys_deletedValue:(int64_t)value_;




- (NSNumber*)primitiveCsys_modified;
- (void)setPrimitiveCsys_modified:(NSNumber*)value;

- (int64_t)primitiveCsys_modifiedValue;
- (void)setPrimitiveCsys_modifiedValue:(int64_t)value_;




- (NSNumber*)primitiveCsys_revision;
- (void)setPrimitiveCsys_revision:(NSNumber*)value;

- (int64_t)primitiveCsys_revisionValue;
- (void)setPrimitiveCsys_revisionValue:(int64_t)value_;




- (NSString*)primitiveCsys_syncronized;
- (void)setPrimitiveCsys_syncronized:(NSString*)value;




- (NSNumber*)primitiveStatus;
- (void)setPrimitiveStatus:(NSNumber*)value;

- (int16_t)primitiveStatusValue;
- (void)setPrimitiveStatusValue:(int16_t)value_;





- (Match*)primitiveMatch;
- (void)setPrimitiveMatch:(Match*)value;



- (User*)primitiveUser;
- (void)setPrimitiveUser:(User*)value;


@end
