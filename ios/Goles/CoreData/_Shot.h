// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Shot.h instead.

#import <CoreData/CoreData.h>

extern const struct ShotAttributes {
	__unsafe_unretained NSString *comment;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idShot;
} ShotAttributes;

extern const struct ShotRelationships {
	__unsafe_unretained NSString *user;
} ShotRelationships;

@class User;

@interface ShotID : NSManagedObjectID {}
@end

@interface _Shot : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
@property (nonatomic, readonly, strong) ShotID* objectID;

@property (nonatomic, strong) NSString* comment;

//- (BOOL)validateComment:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* csys_birth;

@property (atomic) int64_t csys_birthValue;
- (int64_t)csys_birthValue;
- (void)setCsys_birthValue:(int64_t)value_;

//- (BOOL)validateCsys_birth:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* csys_deleted;

@property (atomic) int64_t csys_deletedValue;
- (int64_t)csys_deletedValue;
- (void)setCsys_deletedValue:(int64_t)value_;

//- (BOOL)validateCsys_deleted:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* csys_modified;

@property (atomic) int64_t csys_modifiedValue;
- (int64_t)csys_modifiedValue;
- (void)setCsys_modifiedValue:(int64_t)value_;

//- (BOOL)validateCsys_modified:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* csys_revision;

@property (atomic) int64_t csys_revisionValue;
- (int64_t)csys_revisionValue;
- (void)setCsys_revisionValue:(int64_t)value_;

//- (BOOL)validateCsys_revision:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSString* csys_syncronized;

//- (BOOL)validateCsys_syncronized:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* idShot;

@property (atomic) int64_t idShotValue;
- (int64_t)idShotValue;
- (void)setIdShotValue:(int64_t)value_;

//- (BOOL)validateIdShot:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) User *user;

//- (BOOL)validateUser:(id*)value_ error:(NSError**)error_;

@end

@interface _Shot (CoreDataGeneratedPrimitiveAccessors)

- (NSString*)primitiveComment;
- (void)setPrimitiveComment:(NSString*)value;

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

- (NSNumber*)primitiveIdShot;
- (void)setPrimitiveIdShot:(NSNumber*)value;

- (int64_t)primitiveIdShotValue;
- (void)setPrimitiveIdShotValue:(int64_t)value_;

- (User*)primitiveUser;
- (void)setPrimitiveUser:(User*)value;

@end
