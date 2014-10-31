// DO NOT EDIT. This file is machine-generated and constantly overwritten.
// Make changes to SyncControl.h instead.

#import <CoreData/CoreData.h>


extern const struct SyncControlAttributes {
	__unsafe_unretained NSString *aliasView;
	__unsafe_unretained NSString *lastCall;
	__unsafe_unretained NSString *lastServerDate;
	__unsafe_unretained NSString *nameEntity;
	__unsafe_unretained NSString *updatePriority;
} SyncControlAttributes;

extern const struct SyncControlRelationships {
} SyncControlRelationships;

extern const struct SyncControlFetchedProperties {
} SyncControlFetchedProperties;








@interface SyncControlID : NSManagedObjectID {}
@end

@interface _SyncControl : NSManagedObject {}
+ (id)insertInManagedObjectContext:(NSManagedObjectContext*)moc_;
+ (NSString*)entityName;
+ (NSEntityDescription*)entityInManagedObjectContext:(NSManagedObjectContext*)moc_;
- (SyncControlID*)objectID;





@property (nonatomic, strong) NSString* aliasView;



//- (BOOL)validateAliasView:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* lastCall;



@property int64_t lastCallValue;
- (int64_t)lastCallValue;
- (void)setLastCallValue:(int64_t)value_;

//- (BOOL)validateLastCall:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* lastServerDate;



@property int64_t lastServerDateValue;
- (int64_t)lastServerDateValue;
- (void)setLastServerDateValue:(int64_t)value_;

//- (BOOL)validateLastServerDate:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSString* nameEntity;



//- (BOOL)validateNameEntity:(id*)value_ error:(NSError**)error_;





@property (nonatomic, strong) NSNumber* updatePriority;



@property int64_t updatePriorityValue;
- (int64_t)updatePriorityValue;
- (void)setUpdatePriorityValue:(int64_t)value_;

//- (BOOL)validateUpdatePriority:(id*)value_ error:(NSError**)error_;






@end

@interface _SyncControl (CoreDataGeneratedAccessors)

@end

@interface _SyncControl (CoreDataGeneratedPrimitiveAccessors)


- (NSString*)primitiveAliasView;
- (void)setPrimitiveAliasView:(NSString*)value;




- (NSNumber*)primitiveLastCall;
- (void)setPrimitiveLastCall:(NSNumber*)value;

- (int64_t)primitiveLastCallValue;
- (void)setPrimitiveLastCallValue:(int64_t)value_;




- (NSNumber*)primitiveLastServerDate;
- (void)setPrimitiveLastServerDate:(NSNumber*)value;

- (int64_t)primitiveLastServerDateValue;
- (void)setPrimitiveLastServerDateValue:(int64_t)value_;




- (NSString*)primitiveNameEntity;
- (void)setPrimitiveNameEntity:(NSString*)value;




- (NSNumber*)primitiveUpdatePriority;
- (void)setPrimitiveUpdatePriority:(NSNumber*)value;

- (int64_t)primitiveUpdatePriorityValue;
- (void)setPrimitiveUpdatePriorityValue:(int64_t)value_;




@end
