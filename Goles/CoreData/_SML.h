// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to SML.h instead.

#import <CoreData/CoreData.h>


extern const struct SMLAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idSML;
	__unsafe_unretained NSString *language;
	__unsafe_unretained NSString *message;
	__unsafe_unretained NSString *sound;
} SMLAttributes;

extern const struct SMLRelationships {
} SMLRelationships;

extern const struct SMLFetchedProperties {
} SMLFetchedProperties;












@interface SMLID : NSManagedObjectID {}
@end

@interface _SML : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (SMLID*)objectID;





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





@property (nonatomic, strong) NSNumber* idSML;



@property int64_t idSMLValue;
- (int64_t)idSMLValue;
- (void)setIdSMLValue:(int64_t)value_;

//- (BOOL)validateIdSML:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* language;



@property int16_t languageValue;
- (int16_t)languageValue;
- (void)setLanguageValue:(int16_t)value_;

//- (BOOL)validateLanguage:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* message;



@property int16_t messageValue;
- (int16_t)messageValue;
- (void)setMessageValue:(int16_t)value_;

//- (BOOL)validateMessage:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* sound;



@property int16_t soundValue;
- (int16_t)soundValue;
- (void)setSoundValue:(int16_t)value_;

//- (BOOL)validateSound:(id*)value_ error:(NSError**)error_;






@end

@interface _SML (CoreDataGeneratedAccessors)

@end

@interface _SML (CoreDataGeneratedPrimitiveAccessors)


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




- (NSNumber*)primitiveIdSML;
- (void)setPrimitiveIdSML:(NSNumber*)value;

- (int64_t)primitiveIdSMLValue;
- (void)setPrimitiveIdSMLValue:(int64_t)value_;




- (NSNumber*)primitiveLanguage;
- (void)setPrimitiveLanguage:(NSNumber*)value;

- (int16_t)primitiveLanguageValue;
- (void)setPrimitiveLanguageValue:(int16_t)value_;




- (NSNumber*)primitiveMessage;
- (void)setPrimitiveMessage:(NSNumber*)value;

- (int16_t)primitiveMessageValue;
- (void)setPrimitiveMessageValue:(int16_t)value_;




- (NSNumber*)primitiveSound;
- (void)setPrimitiveSound:(NSNumber*)value;

- (int16_t)primitiveSoundValue;
- (void)setPrimitiveSoundValue:(int16_t)value_;




@end
