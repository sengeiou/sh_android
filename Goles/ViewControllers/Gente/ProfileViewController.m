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
#import "CoreDataParsing.h"
#import "Utils.h"
#import "DownloadImage.h"
#import "Followingbutton.h"
#import "Followbutton.h"
#import "TTTAttributedLabel.h"
#import "ChangeEndPointViewController.h"
#import "FavRestConsumer.h"
#import "SyncManager.h"

@interface ProfileViewController () <TTTAttributedLabelDelegate, UIActionSheetDelegate>

@property (weak, nonatomic) IBOutlet UIImageView *imgPhoto;
@property (weak, nonatomic) IBOutlet UIButton *btnPhoto;

@property (weak, nonatomic) IBOutlet UIButton *btnPoints;
@property (weak, nonatomic) IBOutlet UILabel *lblPoints;
@property (weak, nonatomic) IBOutlet UIButton *btnFollowing;
@property (weak, nonatomic) IBOutlet UILabel *lblFollowing;
@property (weak, nonatomic) IBOutlet UIButton *btnFollowers;
@property (weak, nonatomic) IBOutlet UILabel *lblFollowers;
@property (weak, nonatomic) IBOutlet Followbutton *btnFollow;
@property (weak, nonatomic) IBOutlet Followingbutton *btnUnfollow;
@property (weak, nonatomic) IBOutlet UIButton *btnEditProfile;

@property (weak, nonatomic) IBOutlet UILabel *lblName;
@property (weak, nonatomic) IBOutlet UILabel *lblRank;
@property (weak, nonatomic) IBOutlet UILabel *lblTeamBio;
@property (weak, nonatomic) IBOutlet TTTAttributedLabel *txtViewWebSite;
@property (weak, nonatomic) IBOutlet UISegmentedControl *segmentedControl;
@property (weak, nonatomic) IBOutlet UIView *mainView;
@property (weak, nonatomic) IBOutlet UIScrollView *scrollView;

@property (nonatomic,assign)       BOOL   followActionSuccess;

@end

@implementation ProfileViewController

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    [super viewDidLoad];

    self.selectedUser = [[UserManager singleton] getUserForId:self.selectedUser.idUserValue];
    [self dataFillView];
    
    [self customView];
    [self textLocalizable];
}

//------------------------------------------------------------------------------
-(void)viewWillAppear:(BOOL)animated{
    
	[super viewWillAppear:animated];
    
    //Listen for keyboard process close
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshBotonAndScores) name:k_NOTIF_USER_SYNCHRO_END object:nil];

    [[FavRestConsumer sharedInstance] getEntityFromClass:[User class] withKey:@{kJSON_ID_USER:self.selectedUser.idUser} withDelegate:self];
}

-(void)viewWillDisappear:(BOOL)animated{
    
    [super viewWillDisappear:animated];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

//------------------------------------------------------------------------------
- (void)customView{
    
    self.btnFollow.layer.cornerRadius = 5; // this value vary as per your desire
    self.btnFollow.clipsToBounds = YES;
    self.btnUnfollow.layer.cornerRadius = 5; // this value vary as per your desire
    self.btnUnfollow.clipsToBounds = YES;
    self.btnEditProfile.layer.cornerRadius = 5; // this value vary as per your desire
    self.btnEditProfile.clipsToBounds = YES;
}

//------------------------------------------------------------------------------
- (void)dataFillView{
    
    [self.navigationItem setTitle:self.selectedUser.userName];

    
    [self.btnFollowing setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.numFollowing] forState:UIControlStateNormal];
    [self.btnFollowers setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.numFollowers] forState:UIControlStateNormal];
    
    self.lblName.text = self.selectedUser.name;
    
    //Para la siguiente version de la app
    /*[self.btnPoints setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.points] forState:UIControlStateNormal];
      NSString *rank =  NSLocalizedString(@"rank", nil);
      self.lblRank.text = [NSString stringWithFormat:@"%@ %@",rank, self.selectedUser.rank];*/
    
    self.lblTeamBio.text = [NSString stringWithFormat:@"%@, %@",self.selectedUser.favoriteTeamName, self.selectedUser.bio];
    [self.lblTeamBio sizeToFit];
    
    self.txtViewWebSite.text = self.selectedUser.website;
    self.txtViewWebSite.enabledTextCheckingTypes = NSTextCheckingTypeLink;
    self.txtViewWebSite.linkAttributes = [NSDictionary dictionaryWithObject:[NSNumber numberWithBool:NO] forKey:(__bridge NSString *)kCTUnderlineStyleAttributeName];
    self.txtViewWebSite.delegate = self;
    [self.txtViewWebSite sizeToFit];
	
	[self configureFollowButton];
	
    self.imgPhoto = [DownloadImage downloadImageWithUrl:[NSURL URLWithString:self.selectedUser.photo] andUIimageView:self.imgPhoto andText:[self.selectedUser.name substringToIndex:1]];
    
    
    if ([self.selectedUser.idUser isEqual:[[UserManager sharedInstance] getUserId]]) {
        if (K_DEBUG_MODE) {
            [self.btnPhoto addTarget:self action:@selector(passToChageEndPoint) forControlEvents:UIControlEventTouchUpInside];
        }
    }
}

//------------------------------------------------------------------------------
- (void)refreshBotonAndScores{
    
    [self.btnFollowing setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.numFollowing] forState:UIControlStateNormal];
    [self.btnFollowers setTitle:[NSString stringWithFormat:@"%@", self.selectedUser.numFollowers] forState:UIControlStateNormal];
    [self configureFollowButton];
}

//------------------------------------------------------------------------------
-(void)passToChageEndPoint{
    
    ChangeEndPointViewController *changeEndPointVC = [self.storyboard instantiateViewControllerWithIdentifier:@"changeEndPointVC"];
    [self.navigationController pushViewController:changeEndPointVC animated:YES];
}

//------------------------------------------------------------------------------
#pragma mark - TTTAttributedLabelDelegate

- (void)attributedLabel:(__unused TTTAttributedLabel *)label
   didSelectLinkWithURL:(NSURL *)url {
    
    [[UIApplication sharedApplication] openURL:url];
}

#pragma mark - FOLLOW BUTTONS
//------------------------------------------------------------------------------
- (void)configureFollowButton {
    
    if ([self.selectedUser isEqual:[[UserManager sharedInstance] getActiveUser]]) {
        [self setEditProfile];
    }else if ([[UserManager singleton] checkIfImFollowingUser:self.selectedUser])
		[self setFollowToYes];
	else
		[self setFollowToNo];
}

//------------------------------------------------------------------------------
- (void)setEditProfile {

    self.btnFollow.hidden = YES;
    self.btnUnfollow.hidden = YES;
    self.btnEditProfile.hidden = NO;
    [self.btnEditProfile addTarget:self action:@selector(editProfile) forControlEvents:UIControlEventTouchUpInside];
}

//------------------------------------------------------------------------------
- (void)setFollowToNo {
   [self.btnUnfollow setAttributedTitle:[Utils formatTitle:NSLocalizedString(@"+ FOLLOW", nil)] forState:UIControlStateNormal];
    self.btnFollow.hidden = YES;
    self.btnEditProfile.hidden = YES;
    self.btnUnfollow.hidden = NO;
    [self.btnUnfollow addTarget:self action:@selector(followUser) forControlEvents:UIControlEventTouchUpInside];
}

//------------------------------------------------------------------------------
- (void)setFollowToYes {
    
    self.btnFollow.hidden = NO;
    self.btnEditProfile.hidden = YES;
    self.btnUnfollow.hidden = YES;
    [self.btnFollow addTarget:self action:@selector(unFollowUser) forControlEvents:UIControlEventTouchUpInside];
}

#pragma mark - FOLLOW AND UNFOLLOW ACTIONS
//------------------------------------------------------------------------------
- (void)followUser {
    
    self.followActionSuccess = [[UserManager singleton] startFollowingUser:self.selectedUser];
    if (self.followActionSuccess){
        [self refreshBotonAndScores];
        [[SyncManager sharedInstance] sendUpdatesToServerWithDelegate:self necessaryDownload:NO];
    }
}

//------------------------------------------------------------------------------
- (void)unFollowUser {
    
    [self unfollow:self.selectedUser];
}

//------------------------------------------------------------------------------
-(void)unfollow:(User *)userUnfollow{
    UIAlertController * alert=   [UIAlertController
                                  alertControllerWithTitle:userUnfollow.userName
                                  message:nil
                                  preferredStyle:UIAlertControllerStyleActionSheet];
    
    UIAlertAction* cancel = [UIAlertAction
                             actionWithTitle:NSLocalizedString(@"Cancel", nil)
                             style:UIAlertActionStyleCancel
                             handler:^(UIAlertAction * action)
                             {
                                 [alert dismissViewControllerAnimated:YES completion:nil];
                                 
                                 self.followActionSuccess = NO;
                             }];
    
    UIAlertAction* unfollow = [UIAlertAction
                               actionWithTitle:NSLocalizedString(@"Unfollow", nil)
                               style:UIAlertActionStyleDestructive
                               handler:^(UIAlertAction * action)
                               {
                                   
                                   self.followActionSuccess = [[UserManager singleton] stopFollowingUser:userUnfollow];
                                   
                                   if (self.followActionSuccess) {
                                       [[SyncManager sharedInstance] sendUpdatesToServerWithDelegate:self necessaryDownload:NO];
                                       [self performSelectorOnMainThread:@selector(refreshBotonAndScores) withObject:nil waitUntilDone:NO];
                                   }
                                   
                                   [alert dismissViewControllerAnimated:YES completion:nil];
                                   
                               }];
    
    
    [alert addAction:unfollow];
    
    [alert addAction:cancel];

    [self presentViewController:alert animated:YES completion:nil];
}

//------------------------------------------------------------------------------
- (void)editProfile {
    
    NSLog(@"Edit Profile");
}

#pragma mark - Localizable Strings
//------------------------------------------------------------------------------
-(void)textLocalizable{
    
    self.lblFollowing.text = NSLocalizedString(@"Following", nil);
    self.lblFollowers.text = NSLocalizedString(@"Followers", nil);
    
    //Para la siguiente version de la app
    /* self.lblPoints.text = NSLocalizedString(@"Points", nil);
       [self setSegments];*/
}

//------------------------------------------------------------------------------
- (void)setSegments {
    
    while(self.segmentedControl.numberOfSegments > 0) {
        [self.segmentedControl removeSegmentAtIndex:0 animated:NO];
    }
    
    [self.segmentedControl insertSegmentWithTitle:NSLocalizedString(@"Shots", nil) atIndex:0 animated:NO];
    [self.segmentedControl insertSegmentWithTitle:NSLocalizedString(@"Predictions", nil) atIndex:1 animated:NO];
    
    self.segmentedControl.selectedSegmentIndex = 0;

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

#pragma mark - Conection response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh{
    
    if (status && !error){
        
        if ([entityClass isSubclassOfClass:[Follow class]]) { //Para cuando este ya situada en la misma pantalla
            [[FavRestConsumer sharedInstance] getEntityFromClass:[User class] withKey:@{kJSON_ID_USER:self.selectedUser.idUser} withDelegate:self];

        }else if ([entityClass isSubclassOfClass:[User class]]){
            self.selectedUser = [[UserManager singleton] getUserForId:[self.selectedUser.idUser integerValue]];
            [self performSelectorOnMainThread:@selector(refreshBotonAndScores) withObject:nil waitUntilDone:NO];
        }
    }
}

@end
