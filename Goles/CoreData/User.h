#import "_User.h"

@interface User : _User {}

@property (nonatomic, strong) UIImage *imgUser;




+(instancetype)insertWithDictionary:(NSDictionary *)dict;
+(instancetype)updateWithDictionary:(NSDictionary *)dict;
+(User *)createUserWithUser:(User *)user;

@end
