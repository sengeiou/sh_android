// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Match.h instead.

#import <CoreData/CoreData.h>

extern const struct MatchAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idLocalTeam;
	__unsafe_unretained NSString *idMatch;
	__unsafe_unretained NSString *idVisitorTeam;
	__unsafe_unretained NSString *localTeamName;
	__unsafe_unretained NSString *matchDate;
	__unsafe_unretained NSString *status;
	__unsafe_unretained NSString *visitorTeamName;
} MatchAttributes;

extern const struct MatchRelationships {
	__unsafe_unretained NSString *teamLocal;
	__unsafe_unretained NSString *teamVisitor;
	__unsafe_unretained NSString *watches;
} MatchRelationships;

@class Team;
@class Team;
@class Watch;

@interface MatchID : NSManagedObjectID {}
@end

@interface _Match : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
@property (nonatomic, readonly, strong) MatchID* objectID;

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

@property (nonatomic, strong) NSNumber* idLocalTeam;

@property (atomic) int32_t idLocalTeamValue;
- (int32_t)idLocalTeamValue;
- (void)setIdLocalTeamValue:(int32_t)value_;

//- (BOOL)validateIdLocalTeam:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* idMatch;

@property (atomic) int64_t idMatchValue;
- (int64_t)idMatchValue;
- (void)setIdMatchValue:(int64_t)value_;

//- (BOOL)validateIdMatch:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* idVisitorTeam;

@property (atomic) int32_t idVisitorTeamValue;
- (int32_t)idVisitorTeamValue;
- (void)setIdVisitorTeamValue:(int32_t)value_;

//- (BOOL)validateIdVisitorTeam:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSString* localTeamName;

//- (BOOL)validateLocalTeamName:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* matchDate;

@property (atomic) int64_t matchDateValue;
- (int64_t)matchDateValue;
- (void)setMatchDateValue:(int64_t)value_;

//- (BOOL)validateMatchDate:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSNumber* status;

@property (atomic) int16_t statusValue;
- (int16_t)statusValue;
- (void)setStatusValue:(int16_t)value_;

//- (BOOL)validateStatus:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSString* visitorTeamName;

//- (BOOL)validateVisitorTeamName:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) Team *teamLocal;

//- (BOOL)validateTeamLocal:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) Team *teamVisitor;

//- (BOOL)validateTeamVisitor:(id*)value_ error:(NSError**)error_;

@property (nonatomic, strong) NSSet *watches;

- (NSMutableSet*)watchesSet;

@end

@interface _Match (WatchesCoreDataGeneratedAccessors)
- (void)addWatches:(NSSet*)value_;
- (void)removeWatches:(NSSet*)value_;
- (void)addWatchesObject:(Watch*)value_;
- (void)removeWatchesObject:(Watch*)value_;

@end

@interface _Match (CoreDataGeneratedPrimitiveAccessors)

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

- (NSNumber*)primitiveIdLocalTeam;
- (void)setPrimitiveIdLocalTeam:(NSNumber*)value;

- (int32_t)primitiveIdLocalTeamValue;
- (void)setPrimitiveIdLocalTeamValue:(int32_t)value_;

- (NSNumber*)primitiveIdMatch;
- (void)setPrimitiveIdMatch:(NSNumber*)value;

- (int64_t)primitiveIdMatchValue;
- (void)setPrimitiveIdMatchValue:(int64_t)value_;

- (NSNumber*)primitiveIdVisitorTeam;
- (void)setPrimitiveIdVisitorTeam:(NSNumber*)value;

- (int32_t)primitiveIdVisitorTeamValue;
- (void)setPrimitiveIdVisitorTeamValue:(int32_t)value_;

- (NSString*)primitiveLocalTeamName;
- (void)setPrimitiveLocalTeamName:(NSString*)value;

- (NSNumber*)primitiveMatchDate;
- (void)setPrimitiveMatchDate:(NSNumber*)value;

- (int64_t)primitiveMatchDateValue;
- (void)setPrimitiveMatchDateValue:(int64_t)value_;

- (NSNumber*)primitiveStatus;
- (void)setPrimitiveStatus:(NSNumber*)value;

- (int16_t)primitiveStatusValue;
- (void)setPrimitiveStatusValue:(int16_t)value_;

- (NSString*)primitiveVisitorTeamName;
- (void)setPrimitiveVisitorTeamName:(NSString*)value;

- (Team*)primitiveTeamLocal;
- (void)setPrimitiveTeamLocal:(Team*)value;

- (Team*)primitiveTeamVisitor;
- (void)setPrimitiveTeamVisitor:(Team*)value;

- (NSMutableSet*)primitiveWatches;
- (void)setPrimitiveWatches:(NSMutableSet*)value;

@end
