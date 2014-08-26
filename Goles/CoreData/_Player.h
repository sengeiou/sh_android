// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Player.h instead.

#import <CoreData/CoreData.h>


extern const struct PlayerAttributes {
	__unsafe_unretained NSString *anonymousUser;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *displayName;
	__unsafe_unretained NSString *eMail;
	__unsafe_unretained NSString *favoriteTeam;
	__unsafe_unretained NSString *idPlayer;
	__unsafe_unretained NSString *index;
	__unsafe_unretained NSString *phoneNumber;
	__unsafe_unretained NSString *photoTimeStamp;
	__unsafe_unretained NSString *photoUrl;
	__unsafe_unretained NSString *pointsTotalAvailable;
	__unsafe_unretained NSString *pointsWon;
	__unsafe_unretained NSString *sessionFacebook;
	__unsafe_unretained NSString *tokenFacebook;
	__unsafe_unretained NSString *userName;
} PlayerAttributes;

extern const struct PlayerRelationships {
	__unsafe_unretained NSString *device;
	__unsafe_unretained NSString *playerProvider;
} PlayerRelationships;

extern const struct PlayerFetchedProperties {
} PlayerFetchedProperties;

@class Device;
@class PlayerProvider;





















@interface PlayerID : NSManagedObjectID {}
@end

@interface _Player : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (PlayerID*)objectID;





@property (nonatomic, strong) NSNumber* anonymousUser;



@property BOOL anonymousUserValue;
- (BOOL)anonymousUserValue;
- (void)setAnonymousUserValue:(BOOL)value_;

//- (BOOL)validateAnonymousUser:(id*)value_ error:(NSError**)error_;





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





@property (nonatomic, strong) NSString* displayName;



//- (BOOL)validateDisplayName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* eMail;



//- (BOOL)validateEMail:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* favoriteTeam;



@property int64_t favoriteTeamValue;
- (int64_t)favoriteTeamValue;
- (void)setFavoriteTeamValue:(int64_t)value_;

//- (BOOL)validateFavoriteTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* idPlayer;



@property int64_t idPlayerValue;
- (int64_t)idPlayerValue;
- (void)setIdPlayerValue:(int64_t)value_;

//- (BOOL)validateIdPlayer:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* index;



@property int64_t indexValue;
- (int64_t)indexValue;
- (void)setIndexValue:(int64_t)value_;

//- (BOOL)validateIndex:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* phoneNumber;



//- (BOOL)validatePhoneNumber:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* photoTimeStamp;



@property int64_t photoTimeStampValue;
- (int64_t)photoTimeStampValue;
- (void)setPhotoTimeStampValue:(int64_t)value_;

//- (BOOL)validatePhotoTimeStamp:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* photoUrl;



//- (BOOL)validatePhotoUrl:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* pointsTotalAvailable;



@property int32_t pointsTotalAvailableValue;
- (int32_t)pointsTotalAvailableValue;
- (void)setPointsTotalAvailableValue:(int32_t)value_;

//- (BOOL)validatePointsTotalAvailable:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* pointsWon;



@property int32_t pointsWonValue;
- (int32_t)pointsWonValue;
- (void)setPointsWonValue:(int32_t)value_;

//- (BOOL)validatePointsWon:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* sessionFacebook;



@property BOOL sessionFacebookValue;
- (BOOL)sessionFacebookValue;
- (void)setSessionFacebookValue:(BOOL)value_;

//- (BOOL)validateSessionFacebook:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* tokenFacebook;



//- (BOOL)validateTokenFacebook:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* userName;



//- (BOOL)validateUserName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) Device *device;

//- (BOOL)validateDevice:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) NSSet *playerProvider;

- (NSMutableSet*)playerProviderSet;





@end

@interface _Player (CoreDataGeneratedAccessors)

- (void)addPlayerProvider:(NSSet*)value_;
- (void)removePlayerProvider:(NSSet*)value_;
- (void)addPlayerProviderObject:(PlayerProvider*)value_;
- (void)removePlayerProviderObject:(PlayerProvider*)value_;

@end

@interface _Player (CoreDataGeneratedPrimitiveAccessors)


- (NSNumber*)primitiveAnonymousUser;
- (void)setPrimitiveAnonymousUser:(NSNumber*)value;

- (BOOL)primitiveAnonymousUserValue;
- (void)setPrimitiveAnonymousUserValue:(BOOL)value_;




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




- (NSString*)primitiveDisplayName;
- (void)setPrimitiveDisplayName:(NSString*)value;




- (NSString*)primitiveEMail;
- (void)setPrimitiveEMail:(NSString*)value;




- (NSNumber*)primitiveFavoriteTeam;
- (void)setPrimitiveFavoriteTeam:(NSNumber*)value;

- (int64_t)primitiveFavoriteTeamValue;
- (void)setPrimitiveFavoriteTeamValue:(int64_t)value_;




- (NSNumber*)primitiveIdPlayer;
- (void)setPrimitiveIdPlayer:(NSNumber*)value;

- (int64_t)primitiveIdPlayerValue;
- (void)setPrimitiveIdPlayerValue:(int64_t)value_;




- (NSNumber*)primitiveIndex;
- (void)setPrimitiveIndex:(NSNumber*)value;

- (int64_t)primitiveIndexValue;
- (void)setPrimitiveIndexValue:(int64_t)value_;




- (NSString*)primitivePhoneNumber;
- (void)setPrimitivePhoneNumber:(NSString*)value;




- (NSNumber*)primitivePhotoTimeStamp;
- (void)setPrimitivePhotoTimeStamp:(NSNumber*)value;

- (int64_t)primitivePhotoTimeStampValue;
- (void)setPrimitivePhotoTimeStampValue:(int64_t)value_;




- (NSString*)primitivePhotoUrl;
- (void)setPrimitivePhotoUrl:(NSString*)value;




- (NSNumber*)primitivePointsTotalAvailable;
- (void)setPrimitivePointsTotalAvailable:(NSNumber*)value;

- (int32_t)primitivePointsTotalAvailableValue;
- (void)setPrimitivePointsTotalAvailableValue:(int32_t)value_;




- (NSNumber*)primitivePointsWon;
- (void)setPrimitivePointsWon:(NSNumber*)value;

- (int32_t)primitivePointsWonValue;
- (void)setPrimitivePointsWonValue:(int32_t)value_;




- (NSNumber*)primitiveSessionFacebook;
- (void)setPrimitiveSessionFacebook:(NSNumber*)value;

- (BOOL)primitiveSessionFacebookValue;
- (void)setPrimitiveSessionFacebookValue:(BOOL)value_;




- (NSString*)primitiveTokenFacebook;
- (void)setPrimitiveTokenFacebook:(NSString*)value;




- (NSString*)primitiveUserName;
- (void)setPrimitiveUserName:(NSString*)value;





- (Device*)primitiveDevice;
- (void)setPrimitiveDevice:(Device*)value;



- (NSMutableSet*)primitivePlayerProvider;
- (void)setPrimitivePlayerProvider:(NSMutableSet*)value;


@end
