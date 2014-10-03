//
//  ProfileViewController.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 01/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ProfileViewController.h"
#import "FavRestConsumer.h"
#import "UIImageView+AFNetworking.h"

@interface ProfileViewController ()

@property (weak, nonatomic) IBOutlet UIImageView *imgPhoto;
@property (weak, nonatomic) IBOutlet UIButton *btnPoints;
@property (weak, nonatomic) IBOutlet UILabel *lblPoints;
@property (weak, nonatomic) IBOutlet UIButton *btnFollowing;
@property (weak, nonatomic) IBOutlet UILabel *lblFollowing;
@property (weak, nonatomic) IBOutlet UIButton *btnFollowers;
@property (weak, nonatomic) IBOutlet UILabel *lblFollowers;
@property (weak, nonatomic) IBOutlet UIButton *btnFollow;
@property (weak, nonatomic) IBOutlet UILabel *lblName;
@property (weak, nonatomic) IBOutlet UILabel *lblRank;
@property (weak, nonatomic) IBOutlet UILabel *lblTeamBio;
@property (weak, nonatomic) IBOutlet UITextView *txtViewWebSite;
@property (weak, nonatomic) IBOutlet UISegmentedControl *segmentedControl;
@property (weak, nonatomic) IBOutlet UIView *mainView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;
@end

@implementation ProfileViewController

- (void)viewDidLoad {
    [super viewDidLoad];
   
    [self customView];

    [self dataFillView];
    
    
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[User class] withDelegate:self];
}

-(void)customView{
    self.navigationController.navigationBar.topItem.title = @"";

    self.btnFollow.layer.cornerRadius = 5; // this value vary as per your desire
    self.btnFollow.clipsToBounds = YES;
}

-(void) dataFillView{
    self.title = self.selectedUser.userName;
    
    [self.btnPoints setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.points] forState:UIControlStateNormal];
    [self.btnFollowing setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.numFollowing] forState:UIControlStateNormal];
    [self.btnFollowers setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.numFollowers] forState:UIControlStateNormal];
    
    self.lblName.text = self.selectedUser.name;
    [self.lblName sizeToFit];
    
    self.lblRank.text = [NSString stringWithFormat:@"rank %@", self.selectedUser.rank];
    [self.lblRank sizeToFit];
    
    self.lblTeamBio.text = self.selectedUser.bio;
    [self.lblTeamBio sizeToFit];
    
    self.txtViewWebSite.text = self.selectedUser.website;
    [self.txtViewWebSite sizeToFit];
    
    //self.imgPhoto.image = self.imgSelectedUser;
    [self receivedImage];
    
}

-(void)receivedImage{
    NSURLRequest *urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:self.selectedUser.photo] cachePolicy:NSURLRequestReturnCacheDataElseLoad timeoutInterval:30.0f];
    UIImage *image = [[UIImageView sharedImageCache] cachedImageForRequest:urlRequest];
    
    if (image == nil) {
        
        [self.imgPhoto setImageWithURLRequest:urlRequest placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            self.imgPhoto.image = image;
            self.imgPhoto.layer.cornerRadius = self.imgPhoto.frame.size.width / 2;
            self.imgPhoto.clipsToBounds = YES;
            [[UIImageView sharedImageCache] cacheImage:image forRequest:urlRequest];
            
            NSLog(@"success: %@", NSStringFromCGSize([image size]));
        } failure:^(NSURLRequest *request, NSHTTPURLResponse *response, NSError *error) {
            NSLog(@"%@", response);
        }];
    }else{
        self.imgPhoto.image = image;
        self.imgPhoto.layer.cornerRadius = self.imgPhoto.frame.size.width / 2;
        self.imgPhoto.clipsToBounds = YES;
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
  
    if (status){
        
        if (status && [entityClass isSubclassOfClass:[User class]]){
            [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[User class] withDelegate:self];
        }
    }else if (refresh){
        
         [self dataFillView];
    }
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
