// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Follow.h instead.

#import <CoreData/CoreData.h>


extern const struct FollowAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idUser;
	__unsafe_unretained NSString *idUserFollowed;
} FollowAttributes;

extern const struct FollowRelationships {
} FollowRelationships;

extern const struct FollowFetchedProperties {
} FollowFetchedProperties;










@interface FollowID : NSManagedObjectID {}
@end

@interface _Follow : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (FollowID*)objectID;





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





@property (nonatomic, strong) NSNumber* idUser;



@property int64_t idUserValue;
- (int64_t)idUserValue;
- (void)setIdUserValue:(int64_t)value_;

//- (BOOL)validateIdUser:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idUserFollowed;



@property int64_t idUserFollowedValue;
- (int64_t)idUserFollowedValue;
- (void)setIdUserFollowedValue:(int64_t)value_;

//- (BOOL)validateIdUserFollowed:(id*)value_ error:(NSError**)error_;






@end

@interface _Follow (CoreDataGeneratedAccessors)

@end

@interface _Follow (CoreDataGeneratedPrimitiveAccessors)


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




- (NSNumber*)primitiveIdUser;
- (void)setPrimitiveIdUser:(NSNumber*)value;

- (int64_t)primitiveIdUserValue;
- (void)setPrimitiveIdUserValue:(int64_t)value_;




- (NSNumber*)primitiveIdUserFollowed;
- (void)setPrimitiveIdUserFollowed:(NSNumber*)value;

- (int64_t)primitiveIdUserFollowedValue;
- (void)setPrimitiveIdUserFollowedValue:(int64_t)value_;




@end
