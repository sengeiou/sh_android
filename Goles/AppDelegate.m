//
//  AppDelegate.m
//
//  Created by Christian Cabarrocas on 11/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "AppDelegate.h"
#import "Constants.h"
#import "CoreDataGenerator.h"
#import "CoreDataManager.h"
#import "ShotManager.h"
#import "CoreDataParsing.h"
#import "PeopleTableViewController.h"
#import "TimeLineViewController.h"
#import "MeContainerViewController.h"
#import "FavRestConsumer.h"
#import "LoginViewController.h"
#import "UserManager.h"
#import "SyncManager.h"
#import "Utils.h"
#import "FavRestConsumer.h"
#import "UserManager.h"
#import <AudioToolbox/AudioToolbox.h>
#import <AdSupport/ASIdentifierManager.h>
#import "FavRestConsumerHelper.h"
#import "Conection.h"
#import "User.h"
#import "Shot.h"
#import "CleanManager.h"

#define kWerePushNotificationsDisabled  @"disabledPushNotificationInSettings"
#define kAlertViewWelcome               1001
#define kAlertViewForeGround            1002
#define kAlertViewError                 1005
#define kAlertViewUpdate1Dot2           1006
#define kAlertViewUpdateApp             1007

@interface AppDelegate () <ParserProtocol>

@property (nonatomic,strong)      UITabBarItem            *tabBarPeople;
@property (nonatomic,strong)      UITabBarItem            *tabBarTimeline;
@property (nonatomic,strong)      UITabBarItem            *tabBarMe;
@property (nonatomic,strong)      UITabBarController      *tabBarController;
@property (nonatomic, strong)     TimeLineViewController  *timelineVC;

@end



@implementation AppDelegate
//------------------------------------------------------------------------------
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
   
    
    NSURLCache *sharedCache = [[NSURLCache alloc] initWithMemoryCapacity:2 * 1024 * 1024
                                                            diskCapacity:100 * 1024 * 1024
                                                                diskPath:nil];
    [NSURLCache setSharedURLCache:sharedCache];
    
    if ( IS_GENERATING_DEFAULT_DATABASE ) {
        golesBaseURL = K_ENDPOINT_PRODUCTION;
        [[CoreDataGenerator singleton] generateDefaultCoreDataBase];
    }
    else {

        if (IS_DEVELOPING)
            golesBaseURL = K_ENDPOINT_DEVELOPER;
        else
            golesBaseURL = K_ENDPOINT_PRODUCTION;


        // To handle push notification
//        if (launchOptions) {
//            NSDictionary *remoteNotif = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
//            [self parsePayload:remoteNotif];
//        }
        
        // Set Apirater settings
        [self setApiraterSettings];

        // Clear app icon notification badges
        [application setApplicationIconBadgeNumber:0];
        
        [Appirater appLaunched:YES];

    }
    
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
//    self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
    
    //Show the login view
    [self showInitView];
    
    
    return YES;
}

//------------------------------------------------------------------------------
- (void)applicationDidEnterBackground:(UIApplication *)application {
    
    UIApplication *app = [UIApplication sharedApplication];
    
    UIBackgroundTaskIdentifier bgTask = [app beginBackgroundTaskWithExpirationHandler:^{
        [app endBackgroundTask:bgTask];
       
    }];
    [[CleanManager sharedInstance] cleanProcess];
    [[[SyncManager singleton] synchroTimer] invalidate];
}

//------------------------------------------------------------------------------
- (void)applicationWillEnterForeground:(UIApplication *)application {
	
    User *userCurrent = [[UserManager sharedInstance] getActiveUser];
    
    if (userCurrent){
            [[NSNotificationCenter defaultCenter] postNotificationName:k_NOTIF_BACKGROUND object:nil];
    
        if (SYNCHRO_ACTIVATED)
            [[SyncManager singleton] startSyncProcess];
    }

//    User *userCurrent = [[UserManager sharedInstance] getActiveUser];
//   
//    if (userCurrent){
//        [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Shot class] withDelegate:nil];
//
//        [[Conection sharedInstance]getServerTimewithDelegate:self andRefresh:YES withShot:NO];
//
//        if (SYNCHRO_ACTIVATED)
//            [[SyncManager singleton] startSyncProcess];
//    }
}

//------------------------------------------------------------------------------
- (void)applicationDidBecomeActive:(UIApplication *)application {
   
}

//------------------------------------------------------------------------------
- (void)applicationWillTerminate:(UIApplication *)application {

	[[[SyncManager singleton] synchroTimer] invalidate];
}

#pragma mark - Private methods
//------------------------------------------------------------------------------
-(void) showInitView{
    
    if (![[UserManager singleton] getUserSessionToken]) {
        
        self.setupSB = [UIStoryboard storyboardWithName:@"Setup2" bundle:nil];
        LoginViewController *loginVC = [self.setupSB instantiateViewControllerWithIdentifier:@"loginVC"];
        UINavigationController *navLoginVC = [[UINavigationController alloc]initWithRootViewController:loginVC];
        self.window.rootViewController = navLoginVC;
        
    }else {
        self.request = [FavRestConsumerHelper createREQ];
        if (SYNCHRO_ACTIVATED)
            [[SyncManager singleton] startSyncProcess];
        [self setTabBarItems];
    }
    
}
//------------------------------------------------------------------------------
-(void)setTabBarItems {

    self.peopleSB = [UIStoryboard storyboardWithName:@"People" bundle:nil];
    PeopleTableViewController *peopleVC = [self.peopleSB instantiateViewControllerWithIdentifier:@"peopleTableVC"];
    UINavigationController *navPeopleVC = [[UINavigationController alloc]initWithRootViewController:peopleVC];

    self.timelineSB = [UIStoryboard storyboardWithName:@"Timeline" bundle:nil];
    self.timelineVC = [self.timelineSB instantiateViewControllerWithIdentifier:@"timelineVC"];
    UINavigationController *navTimelineVC = [[UINavigationController alloc]initWithRootViewController:self.timelineVC];

    
    self.meSB = [UIStoryboard storyboardWithName:@"Me" bundle:nil];
    MeContainerViewController *meVC = [self.meSB instantiateViewControllerWithIdentifier:@"meContainerVC"];
    UINavigationController *navMeVC = [[UINavigationController alloc]initWithRootViewController:meVC];
    
    
    navPeopleVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"PeopleTabTitle", nil) image:[UIImage imageNamed:@"Icon_People_OFF"] selectedImage:[UIImage imageNamed:@"Icon_People_ON"]];
    
    self.timelineVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"TimelineTabTitle", nil) image:[UIImage imageNamed:@"Icon_Timeline_OFF"] selectedImage:[UIImage imageNamed:@"Icon_Timeline_ON"]];
    
    navMeVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"MeTabTitle", nil) image:[UIImage imageNamed:@"Icon_Me_OFF"] selectedImage:[UIImage imageNamed:@"Icon_Me_ON"]];
    
    self.tabBarController.tabBar.barTintColor = [UIColor clearColor];
    self.tabBarController = [[UITabBarController alloc] init];
    self.tabBarController.viewControllers = @[navPeopleVC,navTimelineVC,navMeVC];
    self.tabBarController.selectedIndex = 1;
    self.window.rootViewController = self.tabBarController;

}

//------------------------------------------------------------------------------
-(void)setApiraterSettings {
    
    [Appirater setAppId:@"546611684"];
    [Appirater setUsesUntilPrompt:10];
    [Appirater setDaysUntilPrompt:10];
    [Appirater setTimeBeforeReminding:10];
    [Appirater setDebug:NO];
}

#pragma mark - UIAlert delegate methods
//------------------------------------------------------------------------------
-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
	
    if (alertView.tag == kAlertViewForeGround) {                    // Alert on push notification received while app is in foreground
		if (buttonIndex == 1) {
			self.tabBarController.selectedIndex = 1;
		}
	} else if (alertView.tag == kAlertViewError) {                  //Alert when APNS register did fail
		[self registerAPNS];
    } else if (alertView.tag == kAlertViewUpdateApp) {              //Alert when getAppLastVersion didResponse is called and app is outdated
        if (buttonIndex == 1)
            [[UIApplication sharedApplication] openURL:[NSURL URLWithString:K_ITUNES_URL]];
    }
}

#pragma mark - Notifications delegate Methods
//------------------------------------------------------------------------------
- (void)registerAPNS {

    if ([[UIApplication sharedApplication] respondsToSelector:@selector(isRegisteredForRemoteNotifications)]) {
        // iOS 8 Notifications
        [[UIApplication sharedApplication] registerUserNotificationSettings:[UIUserNotificationSettings settingsForTypes:(UIUserNotificationTypeSound | UIUserNotificationTypeAlert | UIUserNotificationTypeBadge) categories:nil]];
        
        [[UIApplication sharedApplication] registerForRemoteNotifications];
    }
    else {
        // iOS < 8 Notifications
        [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
         (UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeSound |UIRemoteNotificationTypeAlert)];
    }
}

//------------------------------------------------------------------------------
-(void)application:(UIApplication *)app didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)devToken {
    
    DLog(@"APNS Response with token %@",devToken);

	NSString *token = [[[[devToken description] stringByReplacingOccurrencesOfString:@"<" withString:@""]
                        stringByReplacingOccurrencesOfString:@">" withString:@""]
                       stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    [[UserManager singleton] setDeviceToken:token];
}

//------------------------------------------------------------------------------
-(void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)err {

//	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error de registro"
//                                                    message:@"No ha sido posible registrar este dispositivo. Verifica si tienes una conexión a internet y que el puerto 5223 TCP este abierto."
//                                                   delegate:self
//                                          cancelButtonTitle:nil
//                                          otherButtonTitles:@"Reintentar", nil];
//	
//	alert.tag = kAlertViewError;
//	[alert show];
}

//------------------------------------------------------------------------------
-(void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    
//    if ( application.applicationState == UIApplicationStateActive ){
//        
//		NSDictionary *apsInfo = [userInfo objectForKey:@"aps"];
//		NSString *alert = [apsInfo objectForKey:@"alert"];
//        NSString *appName = [[NSBundle mainBundle] objectForInfoDictionaryKey:(NSString*)kCFBundleNameKey];
//        
//		UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:appName
//															message:alert
//														   delegate:self
//												  cancelButtonTitle:NSLocalizedString(@"_ok",nil)
//												  otherButtonTitles:nil];
//		alertView.tag = kAlertViewForeGround;
//		
//		[alertView show];
//        
//        //CACTUS - S'hauria de controlar l'arribada de noms d'arxiu d'audio diferents.
//        NSString *sound = [apsInfo valueForKey:@"sound"];
//        
//        if(!sound || (sound && [sound isEqualToString:@"default"]))
//            AudioServicesPlayAlertSound(1007);
//        else {
//            
//            SystemSoundID completeSound;
//            NSURL *audioPath = [[NSBundle mainBundle] URLForResource:sound withExtension:@""];
//            if(audioPath)
//            {
//                AudioServicesCreateSystemSoundID((__bridge CFURLRef)audioPath, &completeSound);
//                AudioServicesPlaySystemSound (completeSound);
//            }
//        }
//		
//	} else {
//        [self parsePayload:userInfo];
//    }
}

//------------------------------------------------------------------------------
-(void)parsePayload:(NSDictionary *)payload {
    
    if ( payload ){
        self.tabBarController.selectedIndex = 1;    // Go to "jornadas" tab
        NSString *z = [payload objectForKey:@"z"];
        
        if ( [z isKindOfClass:[NSString class]] ) {
            
            z = [z stringByReplacingOccurrencesOfString:@"\"" withString:@""];
            NSArray *zItems = [z componentsSeparatedByString:@","];
            
            if ([zItems count] > 1){
                
                NSInteger idMatch = [[zItems objectAtIndex:1] integerValue];
                Match *pushMatch = [[CoreDataManager singleton] getEntity:[Match class] withId:idMatch];
                if ( !pushMatch ){
                    [pushMatch setIdMatchValue:idMatch];
                }
//                UINavigationController *nav = (UINavigationController *)self.tabBarController.selectedViewController;
//                DetailMatchFormViewController *detail = [[NavigationManager singleton] pushMatchDetailViewInNavigation:nav];
//
//                [detail setIsComingFromPush:YES];
//                [detail checkCurrentMatchData:pushMatch];
            }
        }
    }
}

#pragma mark - Public methods
//------------------------------------------------------------------------------
+(void)removeAllCache {

    // Remove Core Data
    [[CoreDataManager singleton] eraseCoreData];
}

#pragma mark - RestConsumers delegate methods
//------------------------------------------------------------------------------
-(void)deviceRegistrationDidResponse:(NSDictionary *)device {

}

#pragma mark - Webservice response methods
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error andRefresh:(BOOL)refresh {
    
}

#pragma mark - Orientation methods
//------------------------------------------------------------------------------
-(NSUInteger)application:(UIApplication *)application supportedInterfaceOrientationsForWindow:(UIWindow *)window {
    
    if(self.restrictRotation){
        
        //NSLog(@"SOLOOOOOOOO");
        return UIInterfaceOrientationMaskPortrait;
    }else{
        //NSLog(@"TODAS");
        self.timelineVC.orientation = YES;
        return UIInterfaceOrientationMaskAll;
    }
}


@end
