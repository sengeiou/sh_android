// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Mode.h instead.

#import <CoreData/CoreData.h>


extern const struct ModeAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idMode;
	__unsafe_unretained NSString *name;
	__unsafe_unretained NSString *order;
} ModeAttributes;

extern const struct ModeRelationships {
	__unsafe_unretained NSString *device;
	__unsafe_unretained NSString *teams;
	__unsafe_unretained NSString *tournaments;
} ModeRelationships;

extern const struct ModeFetchedProperties {
} ModeFetchedProperties;

@class Device;
@class Team;
@class Tournament;










@interface ModeID : NSManagedObjectID {}
@end

@interface _Mode : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (ModeID*)objectID;





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





@property (nonatomic, strong) NSNumber* idMode;



@property int64_t idModeValue;
- (int64_t)idModeValue;
- (void)setIdModeValue:(int64_t)value_;

//- (BOOL)validateIdMode:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* name;



//- (BOOL)validateName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* order;



@property int64_t orderValue;
- (int64_t)orderValue;
- (void)setOrderValue:(int64_t)value_;

//- (BOOL)validateOrder:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) Device *device;

//- (BOOL)validateDevice:(id*)value_ error:(NSError**)error_;




@property (nonatomic, strong) NSSet *teams;

- (NSMutableSet*)teamsSet;




@property (nonatomic, strong) NSSet *tournaments;

- (NSMutableSet*)tournamentsSet;





@end

@interface _Mode (CoreDataGeneratedAccessors)

- (void)addTeams:(NSSet*)value_;
- (void)removeTeams:(NSSet*)value_;
- (void)addTeamsObject:(Team*)value_;
- (void)removeTeamsObject:(Team*)value_;

- (void)addTournaments:(NSSet*)value_;
- (void)removeTournaments:(NSSet*)value_;
- (void)addTournamentsObject:(Tournament*)value_;
- (void)removeTournamentsObject:(Tournament*)value_;

@end

@interface _Mode (CoreDataGeneratedPrimitiveAccessors)


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




- (NSNumber*)primitiveIdMode;
- (void)setPrimitiveIdMode:(NSNumber*)value;

- (int64_t)primitiveIdModeValue;
- (void)setPrimitiveIdModeValue:(int64_t)value_;




- (NSString*)primitiveName;
- (void)setPrimitiveName:(NSString*)value;




- (NSNumber*)primitiveOrder;
- (void)setPrimitiveOrder:(NSNumber*)value;

- (int64_t)primitiveOrderValue;
- (void)setPrimitiveOrderValue:(int64_t)value_;





- (Device*)primitiveDevice;
- (void)setPrimitiveDevice:(Device*)value;



- (NSMutableSet*)primitiveTeams;
- (void)setPrimitiveTeams:(NSMutableSet*)value;



- (NSMutableSet*)primitiveTournaments;
- (void)setPrimitiveTournaments:(NSMutableSet*)value;


@end
