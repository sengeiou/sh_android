// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to AppAdvice.h instead.

#import <CoreData/CoreData.h>

extern const struct AppAdviceAttributes {
	__unsafe_unretained NSString *buttonAction;
	__unsafe_unretained NSString *buttonData;
	__unsafe_unretained NSString *buttonTextId;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *endDate;
	__unsafe_unretained NSString *endVersion;
	__unsafe_unretained NSString *idAppAdvice;
	__unsafe_unretained NSString *idMessage;
	__unsafe_unretained NSString *path;
	__unsafe_unretained NSString *platform;
	__unsafe_unretained NSString *startDate;
	__unsafe_unretained NSString *startVersion;
	__unsafe_unretained NSString *status;
	__unsafe_unretained NSString *visibleButton;
	__unsafe_unretained NSString *weight;
} AppAdviceAttributes;

extern const struct AppAdviceRelationships {
	__unsafe_unretained NSString *message;
} AppAdviceRelationships;

@class Message;

@interface AppAdviceID : NSManagedObjectID {}
@end

@interface _AppAdvice : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
@property (nonatomic, readonly, strong) AppAdviceID* objectID;

@property (nonatomic, strong) NSString* buttonAction;

//- (BOOL)validateButtonAction:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSString* buttonData;

//- (BOOL)validateButtonData:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* buttonTextId;

@property (atomic) int32_t buttonTextIdValue;
- (int32_t)buttonTextIdValue;
- (void)setButtonTextIdValue:(int32_t)value_;

//- (BOOL)validateButtonTextId:(id*)value_ error:(NSError**)error_;

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

@property (nonatomic, strong) NSDate* endDate;

//- (BOOL)validateEndDate:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* endVersion;

@property (atomic) int32_t endVersionValue;
- (int32_t)endVersionValue;
- (void)setEndVersionValue:(int32_t)value_;

//- (BOOL)validateEndVersion:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* idAppAdvice;

@property (atomic) int64_t idAppAdviceValue;
- (int64_t)idAppAdviceValue;
- (void)setIdAppAdviceValue:(int64_t)value_;

//- (BOOL)validateIdAppAdvice:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* idMessage;

@property (atomic) int32_t idMessageValue;
- (int32_t)idMessageValue;
- (void)setIdMessageValue:(int32_t)value_;

//- (BOOL)validateIdMessage:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSString* path;

//- (BOOL)validatePath:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* platform;

@property (atomic) int16_t platformValue;
- (int16_t)platformValue;
- (void)setPlatformValue:(int16_t)value_;

//- (BOOL)validatePlatform:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSDate* startDate;

//- (BOOL)validateStartDate:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* startVersion;

@property (atomic) int32_t startVersionValue;
- (int32_t)startVersionValue;
- (void)setStartVersionValue:(int32_t)value_;

//- (BOOL)validateStartVersion:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* status;

@property (atomic) BOOL statusValue;
- (BOOL)statusValue;
- (void)setStatusValue:(BOOL)value_;

//- (BOOL)validateStatus:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* visibleButton;

@property (atomic) BOOL visibleButtonValue;
- (BOOL)visibleButtonValue;
- (void)setVisibleButtonValue:(BOOL)value_;

//- (BOOL)validateVisibleButton:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* weight;

@property (atomic) int32_t weightValue;
- (int32_t)weightValue;
- (void)setWeightValue:(int32_t)value_;

//- (BOOL)validateWeight:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) Message *message;

//- (BOOL)validateMessage:(id*)value_ error:(NSError**)error_;

@end

@interface _AppAdvice (CoreDataGeneratedPrimitiveAccessors)

- (NSString*)primitiveButtonAction;
- (void)setPrimitiveButtonAction:(NSString*)value;

- (NSString*)primitiveButtonData;
- (void)setPrimitiveButtonData:(NSString*)value;

- (NSNumber*)primitiveButtonTextId;
- (void)setPrimitiveButtonTextId:(NSNumber*)value;

- (int32_t)primitiveButtonTextIdValue;
- (void)setPrimitiveButtonTextIdValue:(int32_t)value_;

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

- (NSDate*)primitiveEndDate;
- (void)setPrimitiveEndDate:(NSDate*)value;

- (NSNumber*)primitiveEndVersion;
- (void)setPrimitiveEndVersion:(NSNumber*)value;

- (int32_t)primitiveEndVersionValue;
- (void)setPrimitiveEndVersionValue:(int32_t)value_;

- (NSNumber*)primitiveIdAppAdvice;
- (void)setPrimitiveIdAppAdvice:(NSNumber*)value;

- (int64_t)primitiveIdAppAdviceValue;
- (void)setPrimitiveIdAppAdviceValue:(int64_t)value_;

- (NSNumber*)primitiveIdMessage;
- (void)setPrimitiveIdMessage:(NSNumber*)value;

- (int32_t)primitiveIdMessageValue;
- (void)setPrimitiveIdMessageValue:(int32_t)value_;

- (NSString*)primitivePath;
- (void)setPrimitivePath:(NSString*)value;

- (NSNumber*)primitivePlatform;
- (void)setPrimitivePlatform:(NSNumber*)value;

- (int16_t)primitivePlatformValue;
- (void)setPrimitivePlatformValue:(int16_t)value_;

- (NSDate*)primitiveStartDate;
- (void)setPrimitiveStartDate:(NSDate*)value;

- (NSNumber*)primitiveStartVersion;
- (void)setPrimitiveStartVersion:(NSNumber*)value;

- (int32_t)primitiveStartVersionValue;
- (void)setPrimitiveStartVersionValue:(int32_t)value_;

- (NSNumber*)primitiveStatus;
- (void)setPrimitiveStatus:(NSNumber*)value;

- (BOOL)primitiveStatusValue;
- (void)setPrimitiveStatusValue:(BOOL)value_;

- (NSNumber*)primitiveVisibleButton;
- (void)setPrimitiveVisibleButton:(NSNumber*)value;

- (BOOL)primitiveVisibleButtonValue;
- (void)setPrimitiveVisibleButtonValue:(BOOL)value_;

- (NSNumber*)primitiveWeight;
- (void)setPrimitiveWeight:(NSNumber*)value;

- (int32_t)primitiveWeightValue;
- (void)setPrimitiveWeightValue:(int32_t)value_;

- (Message*)primitiveMessage;
- (void)setPrimitiveMessage:(Message*)value;

@end
