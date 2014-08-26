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

extern const struct MessageFetchedProperties {
} MessageFetchedProperties;

@class AppAdvice;











@interface MessageID : NSManagedObjectID {}
@end

@interface _Message : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (MessageID*)objectID;





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





@property (nonatomic, strong) NSNumber* idMessage;



@property int64_t idMessageValue;
- (int64_t)idMessageValue;
- (void)setIdMessageValue:(int64_t)value_;

//- (BOOL)validateIdMessage:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* locale;



//- (BOOL)validateLocale:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* message;



//- (BOOL)validateMessage:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* platform;



@property int32_t platformValue;
- (int32_t)platformValue;
- (void)setPlatformValue:(int32_t)value_;

//- (BOOL)validatePlatform:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSSet *advice;

- (NSMutableSet*)adviceSet;





@end

@interface _Message (CoreDataGeneratedAccessors)

- (void)addAdvice:(NSSet*)value_;
- (void)removeAdvice:(NSSet*)value_;
- (void)addAdviceObject:(AppAdvice*)value_;
- (void)removeAdviceObject:(AppAdvice*)value_;

@end

@interface _Message (CoreDataGeneratedPrimitiveAccessors)


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
