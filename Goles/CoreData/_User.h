// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to User.h instead.

#import <CoreData/CoreData.h>


extern const struct UserAttributes {
	__unsafe_unretained NSString *bio;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *eMail;
	__unsafe_unretained NSString *idFavouriteTeam;
	__unsafe_unretained NSString *idUser;
	__unsafe_unretained NSString *name;
	__unsafe_unretained NSString *numFollowers;
	__unsafe_unretained NSString *numFollowing;
	__unsafe_unretained NSString *photo;
	__unsafe_unretained NSString *points;
	__unsafe_unretained NSString *rank;
	__unsafe_unretained NSString *sessionToken;
	__unsafe_unretained NSString *userName;
	__unsafe_unretained NSString *website;
} UserAttributes;

extern const struct UserRelationships {
	__unsafe_unretained NSString *device;
	__unsafe_unretained NSString *shots;
	__unsafe_unretained NSString *team;
} UserRelationships;

extern const struct UserFetchedProperties {
} UserFetchedProperties;

@class Device;
@class Shot;
@class Team;




















@interface UserID : NSManagedObjectID {}
@end

@interface _User : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (UserID*)objectID;





@property (nonatomic, strong) NSString* bio;



//- (BOOL)validateBio:(id*)value_ error:(NSError**)error_;





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





@property (nonatomic, strong) NSNumber* numFollowers;



@property int32_t numFollowersValue;
- (int32_t)numFollowersValue;
- (void)setNumFollowersValue:(int32_t)value_;

//- (BOOL)validateNumFollowers:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* numFollowing;



@property int32_t numFollowingValue;
- (int32_t)numFollowingValue;
- (void)setNumFollowingValue:(int32_t)value_;

//- (BOOL)validateNumFollowing:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* photo;



//- (BOOL)validatePhoto:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* points;



@property int64_t pointsValue;
- (int64_t)pointsValue;
- (void)setPointsValue:(int64_t)value_;

//- (BOOL)validatePoints:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* rank;



@property int64_t rankValue;
- (int64_t)rankValue;
- (void)setRankValue:(int64_t)value_;

//- (BOOL)validateRank:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* sessionToken;



//- (BOOL)validateSessionToken:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* userName;



//- (BOOL)validateUserName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* website;



//- (BOOL)validateWebsite:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) Device *device;

//- (BOOL)validateDevice:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) NSSet *shots;

- (NSMutableSet*)shotsSet;




@property (nonatomic, strong) Team *team;

//- (BOOL)validateTeam:(id*)value_ error:(NSError**)error_;





@end

@interface _User (CoreDataGeneratedAccessors)

- (void)addShots:(NSSet*)value_;
- (void)removeShots:(NSSet*)value_;
- (void)addShotsObject:(Shot*)value_;
- (void)removeShotsObject:(Shot*)value_;

@end

@interface _User (CoreDataGeneratedPrimitiveAccessors)


- (NSString*)primitiveBio;
- (void)setPrimitiveBio:(NSString*)value;




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




- (NSNumber*)primitiveNumFollowers;
- (void)setPrimitiveNumFollowers:(NSNumber*)value;

- (int32_t)primitiveNumFollowersValue;
- (void)setPrimitiveNumFollowersValue:(int32_t)value_;




- (NSNumber*)primitiveNumFollowing;
- (void)setPrimitiveNumFollowing:(NSNumber*)value;

- (int32_t)primitiveNumFollowingValue;
- (void)setPrimitiveNumFollowingValue:(int32_t)value_;




- (NSString*)primitivePhoto;
- (void)setPrimitivePhoto:(NSString*)value;




- (NSNumber*)primitivePoints;
- (void)setPrimitivePoints:(NSNumber*)value;

- (int64_t)primitivePointsValue;
- (void)setPrimitivePointsValue:(int64_t)value_;




- (NSNumber*)primitiveRank;
- (void)setPrimitiveRank:(NSNumber*)value;

- (int64_t)primitiveRankValue;
- (void)setPrimitiveRankValue:(int64_t)value_;




- (NSString*)primitiveSessionToken;
- (void)setPrimitiveSessionToken:(NSString*)value;




- (NSString*)primitiveUserName;
- (void)setPrimitiveUserName:(NSString*)value;




- (NSString*)primitiveWebsite;
- (void)setPrimitiveWebsite:(NSString*)value;





- (Device*)primitiveDevice;
- (void)setPrimitiveDevice:(Device*)value;



- (NSMutableSet*)primitiveShots;
- (void)setPrimitiveShots:(NSMutableSet*)value;



- (Team*)primitiveTeam;
- (void)setPrimitiveTeam:(Team*)value;


@end
