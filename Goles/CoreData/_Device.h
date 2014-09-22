// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Device.h instead.

#import <CoreData/CoreData.h>


extern const struct DeviceAttributes {
	__unsafe_unretained NSString *appVer;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idDevice;
	__unsafe_unretained NSString *idUser;
	__unsafe_unretained NSString *language;
	__unsafe_unretained NSString *locale;
	__unsafe_unretained NSString *model;
	__unsafe_unretained NSString *osVer;
	__unsafe_unretained NSString *platform;
	__unsafe_unretained NSString *status;
	__unsafe_unretained NSString *timeZone;
	__unsafe_unretained NSString *token;
} DeviceAttributes;

extern const struct DeviceRelationships {
	__unsafe_unretained NSString *user;
} DeviceRelationships;

extern const struct DeviceFetchedProperties {
} DeviceFetchedProperties;

@class User;


















@interface DeviceID : NSManagedObjectID {}
@end

@interface _Device : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (DeviceID*)objectID;





@property (nonatomic, strong) NSString* appVer;



//- (BOOL)validateAppVer:(id*)value_ error:(NSError**)error_;





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





@property (nonatomic, strong) NSNumber* idDevice;



@property int64_t idDeviceValue;
- (int64_t)idDeviceValue;
- (void)setIdDeviceValue:(int64_t)value_;

//- (BOOL)validateIdDevice:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idUser;



@property int64_t idUserValue;
- (int64_t)idUserValue;
- (void)setIdUserValue:(int64_t)value_;

//- (BOOL)validateIdUser:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* language;



//- (BOOL)validateLanguage:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* locale;



//- (BOOL)validateLocale:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* model;



//- (BOOL)validateModel:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* osVer;



//- (BOOL)validateOsVer:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* platform;



//- (BOOL)validatePlatform:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* status;



@property BOOL statusValue;
- (BOOL)statusValue;
- (void)setStatusValue:(BOOL)value_;

//- (BOOL)validateStatus:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* timeZone;



//- (BOOL)validateTimeZone:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* token;



//- (BOOL)validateToken:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) User *user;

//- (BOOL)validateUser:(id*)value_ error:(NSError**)error_;





@end

@interface _Device (CoreDataGeneratedAccessors)

@end

@interface _Device (CoreDataGeneratedPrimitiveAccessors)


- (NSString*)primitiveAppVer;
- (void)setPrimitiveAppVer:(NSString*)value;




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




- (NSNumber*)primitiveIdDevice;
- (void)setPrimitiveIdDevice:(NSNumber*)value;

- (int64_t)primitiveIdDeviceValue;
- (void)setPrimitiveIdDeviceValue:(int64_t)value_;




- (NSNumber*)primitiveIdUser;
- (void)setPrimitiveIdUser:(NSNumber*)value;

- (int64_t)primitiveIdUserValue;
- (void)setPrimitiveIdUserValue:(int64_t)value_;




- (NSString*)primitiveLanguage;
- (void)setPrimitiveLanguage:(NSString*)value;




- (NSString*)primitiveLocale;
- (void)setPrimitiveLocale:(NSString*)value;




- (NSString*)primitiveModel;
- (void)setPrimitiveModel:(NSString*)value;




- (NSString*)primitiveOsVer;
- (void)setPrimitiveOsVer:(NSString*)value;




- (NSString*)primitivePlatform;
- (void)setPrimitivePlatform:(NSString*)value;




- (NSNumber*)primitiveStatus;
- (void)setPrimitiveStatus:(NSNumber*)value;

- (BOOL)primitiveStatusValue;
- (void)setPrimitiveStatusValue:(BOOL)value_;




- (NSString*)primitiveTimeZone;
- (void)setPrimitiveTimeZone:(NSString*)value;




- (NSString*)primitiveToken;
- (void)setPrimitiveToken:(NSString*)value;





- (User*)primitiveUser;
- (void)setPrimitiveUser:(User*)value;


@end
