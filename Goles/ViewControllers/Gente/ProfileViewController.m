//
//  ProfileViewController.m
//  Shootr
//
//  Created by Maria Teresa Bañuls on 01/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ProfileViewController.h"
#import "UIImageView+AFNetworking.h"
#import "FollowingTableViewController.h"
#import "Follow.h"
#import "AppDelegate.h"
#import "Constants.h"
#import "UserManager.h"
#import "Fav24Colors.h"

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

//- (id)initWithCoder:(NSCoder*)aDecoder
//{
//    if(self = [super initWithCoder:aDecoder]) {
//        UIBarButtonItem *btnBack = [[UIBarButtonItem alloc]
//                                    initWithTitle:@" "
//                                    style:UIBarButtonItemStylePlain
//                                    target:self
//                                    action:nil];
//        self.navigationController.navigationBar.topItem.backBarButtonItem=btnBack;
//    }
//    return self;
//}

- (void)viewDidLoad {
    [super viewDidLoad];
   
    [self restrictRotation:YES];
    [self customView];
    [self dataFillView];

}

- (void)customView{
    
    UIBarButtonItem *btnBack = [[UIBarButtonItem alloc]
                                initWithTitle:@" "
                                style:UIBarButtonItemStylePlain
                                target:self
                                action:nil];
    self.navigationController.navigationBar.topItem.backBarButtonItem=btnBack;

    
    self.btnFollow.layer.cornerRadius = 5; // this value vary as per your desire
    self.btnFollow.clipsToBounds = YES;
}

- (void)dataFillView{
    self.title = self.selectedUser.userName;
    
    [self.btnPoints setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.points] forState:UIControlStateNormal];
    [self.btnFollowing setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.numFollowing] forState:UIControlStateNormal];
    [self.btnFollowers setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.numFollowers] forState:UIControlStateNormal];
    
    self.lblName.text = self.selectedUser.name;
    
    self.lblRank.text = [NSString stringWithFormat:@"rank %@", self.selectedUser.rank];
    
    self.lblTeamBio.text = self.selectedUser.bio;
    [self.lblTeamBio sizeToFit];
    
    self.txtViewWebSite.text = self.selectedUser.website;
    [self.txtViewWebSite sizeToFit];
	
	[self configureFollowButton];
	
    [self receivedImage];
}

- (void)configureFollowButton {
    
    if ([self.selectedUser isEqual:[[UserManager sharedInstance] getActiveUser]]) {
        [self setEditProfile];
    }else if ([[UserManager singleton] isLoggedUserFollowing:self.selectedUser])
		[self setFollowToYes];
	else
		[self setFollowToNo];
}

- (void)setEditProfile {
    [self.btnFollow setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
    [self.btnFollow setTitle:@"EDIT PROFILE" forState:UIControlStateNormal];
    self.btnFollow.backgroundColor = [Fav24Colors iosSevenBlue];
    self.btnFollow.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
    self.btnFollow.backgroundColor = [UIColor whiteColor];
    self.btnFollow.layer.borderWidth = 1.0f;
    self.btnFollow.layer.masksToBounds = YES;
}

- (void)setFollowToNo {
	
    [self.btnFollow setTitleColor:[Fav24Colors iosSevenBlue] forState:UIControlStateNormal];
    [self.btnFollow setTitle:@"+ FOLLOW" forState:UIControlStateNormal];
    self.btnFollow.layer.borderColor = [[Fav24Colors iosSevenBlue] CGColor];
    self.btnFollow.backgroundColor = [UIColor whiteColor];
    self.btnFollow.layer.borderWidth = 1.0f;
    self.btnFollow.layer.masksToBounds = YES;

    
}

- (void)setFollowToYes {
	
    [self.btnFollow setTitle:@"FOLLOWING" forState:UIControlStateNormal];
    self.btnFollow.backgroundColor = [Fav24Colors iosSevenBlue];
    [self.btnFollow setImage:[UIImage imageNamed:@"check"] forState:UIControlStateNormal];
    UIEdgeInsets contentInsets = UIEdgeInsetsMake(0.0f, -15.0f, 0.0f, 0.0f);
    [self.btnFollow setContentEdgeInsets:contentInsets];

}

- (void)receivedImage{
    NSURLRequest *urlRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:self.selectedUser.photo] cachePolicy:NSURLRequestReturnCacheDataElseLoad timeoutInterval:30.0f];
    UIImage *image = [[UIImageView sharedImageCache] cachedImageForRequest:urlRequest];
    
    if (image == nil) {
        
        [self.imgPhoto setImageWithURLRequest:urlRequest placeholderImage:nil success:^(NSURLRequest *request, NSHTTPURLResponse *response, UIImage *image) {
            self.imgPhoto.image = image;
            self.imgPhoto.layer.cornerRadius = self.imgPhoto.frame.size.width / 2;
            self.imgPhoto.clipsToBounds = YES;
            [[UIImageView sharedImageCache] cacheImage:image forRequest:urlRequest];
            
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


#pragma mark - Navigation
//------------------------------------------------------------------------------
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    
    if ([segue.identifier isEqualToString:@"toFollowing"]) {
        FollowingTableViewController *followingTVC = (FollowingTableViewController *)[segue destinationViewController];
        [followingTVC setSelectedUser:self.selectedUser];
		[followingTVC setViewSelected:FOLLOWING_SELECTED];
    }
	if ([segue.identifier isEqualToString:@"toFollowers"]) {
		FollowingTableViewController *followingTVC = (FollowingTableViewController *)[segue destinationViewController];
		[followingTVC setSelectedUser:self.selectedUser];
		[followingTVC setViewSelected:FOLLOWERS_SELECTED];
	}

}

#pragma mark - Orientation methods
-(void) restrictRotation:(BOOL) restriction
{
    AppDelegate* appDelegate = (AppDelegate*)[UIApplication sharedApplication].delegate;
    appDelegate.restrictRotation = restriction;
}

@end
