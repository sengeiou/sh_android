// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Message.h instead.

#import <CoreData/CoreData.h>

extern const struct MessageAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idMessage;
	__unsafe_unretained NSString *locale;
	__unsafe_unretained NSString *message;
	__unsafe_unretained NSString *platform;
} MessageAttributes;

extern const struct MessageRelationships {
	__unsafe_unretained NSString *advice;
} MessageRelationships;

@class AppAdvice;

@interface MessageID : NSManagedObjectID {}
@end

@interface _Message : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
@property (nonatomic, readonly, strong) MessageID* objectID;

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

@property (nonatomic, strong) NSNumber* idMessage;

@property (atomic) int64_t idMessageValue;
- (int64_t)idMessageValue;
- (void)setIdMessageValue:(int64_t)value_;

//- (BOOL)validateIdMessage:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSString* locale;

//- (BOOL)validateLocale:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSString* message;

//- (BOOL)validateMessage:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* platform;

@property (atomic) int32_t platformValue;
- (int32_t)platformValue;
- (void)setPlatformValue:(int32_t)value_;

//- (BOOL)validatePlatform:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSSet *advice;

- (NSMutableSet*)adviceSet;

@end

@interface _Message (AdviceCoreDataGeneratedAccessors)
- (void)addAdvice:(NSSet*)value_;
- (void)removeAdvice:(NSSet*)value_;
- (void)addAdviceObject:(AppAdvice*)value_;
- (void)removeAdviceObject:(AppAdvice*)value_;

@end

@interface _Message (CoreDataGeneratedPrimitiveAccessors)

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

- (NSNumber*)primitiveIdMessage;
- (void)setPrimitiveIdMessage:(NSNumber*)value;

- (int64_t)primitiveIdMessageValue;
- (void)setPrimitiveIdMessageValue:(int64_t)value_;

- (NSString*)primitiveLocale;
- (void)setPrimitiveLocale:(NSString*)value;

- (NSString*)primitiveMessage;
- (void)setPrimitiveMessage:(NSString*)value;

- (NSNumber*)primitivePlatform;
- (void)setPrimitivePlatform:(NSNumber*)value;

- (int32_t)primitivePlatformValue;
- (void)setPrimitivePlatformValue:(int32_t)value_;

- (NSMutableSet*)primitiveAdvice;
- (void)setPrimitiveAdvice:(NSMutableSet*)value;

@end
