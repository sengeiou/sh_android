// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Team.h instead.

#import <CoreData/CoreData.h>


extern const struct TeamAttributes {
	__unsafe_unretained NSString *clubName;
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idTeam;
	__unsafe_unretained NSString *officialName;
	__unsafe_unretained NSString *shortName;
	__unsafe_unretained NSString *tlaName;
} TeamAttributes;

extern const struct TeamRelationships {
	__unsafe_unretained NSString *user;
} TeamRelationships;

extern const struct TeamFetchedProperties {
} TeamFetchedProperties;

@class User;












@interface TeamID : NSManagedObjectID {}
@end

@interface _Team : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (TeamID*)objectID;





@property (nonatomic, strong) NSString* clubName;



//- (BOOL)validateClubName:(id*)value_ error:(NSError**)error_;





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





@property (nonatomic, strong) NSNumber* idTeam;



@property int64_t idTeamValue;
- (int64_t)idTeamValue;
- (void)setIdTeamValue:(int64_t)value_;

//- (BOOL)validateIdTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* officialName;



//- (BOOL)validateOfficialName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* shortName;



//- (BOOL)validateShortName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* tlaName;



//- (BOOL)validateTlaName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSSet *user;

- (NSMutableSet*)userSet;





@end

@interface _Team (CoreDataGeneratedAccessors)

- (void)addUser:(NSSet*)value_;
- (void)removeUser:(NSSet*)value_;
- (void)addUserObject:(User*)value_;
- (void)removeUserObject:(User*)value_;

@end

@interface _Team (CoreDataGeneratedPrimitiveAccessors)


- (NSString*)primitiveClubName;
- (void)setPrimitiveClubName:(NSString*)value;




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




- (NSNumber*)primitiveIdTeam;
- (void)setPrimitiveIdTeam:(NSNumber*)value;

- (int64_t)primitiveIdTeamValue;
- (void)setPrimitiveIdTeamValue:(int64_t)value_;




- (NSString*)primitiveOfficialName;
- (void)setPrimitiveOfficialName:(NSString*)value;




- (NSString*)primitiveShortName;
- (void)setPrimitiveShortName:(NSString*)value;




- (NSString*)primitiveTlaName;
- (void)setPrimitiveTlaName:(NSString*)value;





- (NSMutableSet*)primitiveUser;
- (void)setPrimitiveUser:(NSMutableSet*)value;


@end
