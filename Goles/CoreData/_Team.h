// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to Team.h instead.

#import <CoreData/CoreData.h>


extern const struct TeamAttributes {
	__unsafe_unretained NSString *csys_birth;
	__unsafe_unretained NSString *csys_deleted;
	__unsafe_unretained NSString *csys_modified;
	__unsafe_unretained NSString *csys_revision;
	__unsafe_unretained NSString *csys_syncronized;
	__unsafe_unretained NSString *idTeam;
	__unsafe_unretained NSString *isNationalTeam;
	__unsafe_unretained NSString *name;
	__unsafe_unretained NSString *nameShort;
	__unsafe_unretained NSString *order;
	__unsafe_unretained NSString *urlImage;
} TeamAttributes;

extern const struct TeamRelationships {
	__unsafe_unretained NSString *matchesAsLocal;
	__unsafe_unretained NSString *matchesAsVisitor;
} TeamRelationships;

extern const struct TeamFetchedProperties {
} TeamFetchedProperties;

@class Match;
@class Match;













@interface TeamID : NSManagedObjectID {}
@end

@interface _Team : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (TeamID*)objectID;





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





@property (nonatomic, strong) NSNumber* idTeam;



@property int64_t idTeamValue;
- (int64_t)idTeamValue;
- (void)setIdTeamValue:(int64_t)value_;

//- (BOOL)validateIdTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* isNationalTeam;



@property BOOL isNationalTeamValue;
- (BOOL)isNationalTeamValue;
- (void)setIsNationalTeamValue:(BOOL)value_;

//- (BOOL)validateIsNationalTeam:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* name;



//- (BOOL)validateName:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* nameShort;



//- (BOOL)validateNameShort:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* order;



@property int64_t orderValue;
- (int64_t)orderValue;
- (void)setOrderValue:(int64_t)value_;

//- (BOOL)validateOrder:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* urlImage;



//- (BOOL)validateUrlImage:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSSet *matchesAsLocal;

- (NSMutableSet*)matchesAsLocalSet;




@property (nonatomic, strong) NSSet *matchesAsVisitor;

- (NSMutableSet*)matchesAsVisitorSet;





@end

@interface _Team (CoreDataGeneratedAccessors)

- (void)addMatchesAsLocal:(NSSet*)value_;
- (void)removeMatchesAsLocal:(NSSet*)value_;
- (void)addMatchesAsLocalObject:(Match*)value_;
- (void)removeMatchesAsLocalObject:(Match*)value_;

- (void)addMatchesAsVisitor:(NSSet*)value_;
- (void)removeMatchesAsVisitor:(NSSet*)value_;
- (void)addMatchesAsVisitorObject:(Match*)value_;
- (void)removeMatchesAsVisitorObject:(Match*)value_;

@end

@interface _Team (CoreDataGeneratedPrimitiveAccessors)


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




- (NSNumber*)primitiveIdTeam;
- (void)setPrimitiveIdTeam:(NSNumber*)value;

- (int64_t)primitiveIdTeamValue;
- (void)setPrimitiveIdTeamValue:(int64_t)value_;




- (NSNumber*)primitiveIsNationalTeam;
- (void)setPrimitiveIsNationalTeam:(NSNumber*)value;

- (BOOL)primitiveIsNationalTeamValue;
- (void)setPrimitiveIsNationalTeamValue:(BOOL)value_;




- (NSString*)primitiveName;
- (void)setPrimitiveName:(NSString*)value;




- (NSString*)primitiveNameShort;
- (void)setPrimitiveNameShort:(NSString*)value;




- (NSNumber*)primitiveOrder;
- (void)setPrimitiveOrder:(NSNumber*)value;

- (int64_t)primitiveOrderValue;
- (void)setPrimitiveOrderValue:(int64_t)value_;




- (NSString*)primitiveUrlImage;
- (void)setPrimitiveUrlImage:(NSString*)value;





- (NSMutableSet*)primitiveMatchesAsLocal;
- (void)setPrimitiveMatchesAsLocal:(NSMutableSet*)value;



- (NSMutableSet*)primitiveMatchesAsVisitor;
- (void)setPrimitiveMatchesAsVisitor:(NSMutableSet*)value;


@end
