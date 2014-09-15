// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Player.h instead.

#import <CoreData/CoreData.h>


extern const struct PlayerAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *eMail;
	__unsafe_unretained NSString *idFavouriteTeam;
	__unsafe_unretained NSString *idUser;
	__unsafe_unretained NSString *name;
	__unsafe_unretained NSString *nick;
	__unsafe_unretained NSString *password;
	__unsafe_unretained NSString *photo;
	__unsafe_unretained NSString *sessionToken;
} PlayerAttributes;

extern const struct PlayerRelationships {
	__unsafe_unretained NSString *device;
} PlayerRelationships;

extern const struct PlayerFetchedProperties {
} PlayerFetchedProperties;

@class Device;















@interface PlayerID : NSManagedObjectID {}
@end

@interface _Player : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (PlayerID*)objectID;





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





@property (nonatomic, strong) NSString* eMail;



//- (BOOL)validateEMail:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idFavouriteTeam;



@property int64_t idFavouriteTeamValue;
- (int64_t)idFavouriteTeamValue;
- (void)setIdFavouriteTeamValue:(int64_t)value_;

//- (BOOL)validateIdFavouriteTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idUser;



@property int64_t idUserValue;
- (int64_t)idUserValue;
- (void)setIdUserValue:(int64_t)value_;

//- (BOOL)validateIdUser:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* name;



//- (BOOL)validateName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* nick;



//- (BOOL)validateNick:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* password;



//- (BOOL)validatePassword:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* photo;



//- (BOOL)validatePhoto:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* sessionToken;



//- (BOOL)validateSessionToken:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) Device *device;

//- (BOOL)validateDevice:(id*)value_ error:(NSError**)error_;





@end

@interface _Player (CoreDataGeneratedAccessors)

@end

@interface _Player (CoreDataGeneratedPrimitiveAccessors)


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




- (NSString*)primitiveEMail;
- (void)setPrimitiveEMail:(NSString*)value;




- (NSNumber*)primitiveIdFavouriteTeam;
- (void)setPrimitiveIdFavouriteTeam:(NSNumber*)value;

- (int64_t)primitiveIdFavouriteTeamValue;
- (void)setPrimitiveIdFavouriteTeamValue:(int64_t)value_;




- (NSNumber*)primitiveIdUser;
- (void)setPrimitiveIdUser:(NSNumber*)value;

- (int64_t)primitiveIdUserValue;
- (void)setPrimitiveIdUserValue:(int64_t)value_;




- (NSString*)primitiveName;
- (void)setPrimitiveName:(NSString*)value;




- (NSString*)primitiveNick;
- (void)setPrimitiveNick:(NSString*)value;




- (NSString*)primitivePassword;
- (void)setPrimitivePassword:(NSString*)value;




- (NSString*)primitivePhoto;
- (void)setPrimitivePhoto:(NSString*)value;




- (NSString*)primitiveSessionToken;
- (void)setPrimitiveSessionToken:(NSString*)value;





- (Device*)primitiveDevice;
- (void)setPrimitiveDevice:(Device*)value;


@end
