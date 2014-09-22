//
//  AppDelegate.m
//  Goles
//
//  Created by Christian Cabarrocas on 11/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "AppDelegate.h"
#import "Constants.h"
#import "CoreDataGenerator.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"
#import "PeopleTableViewController.h"
#import "TimeLineViewController.h"
#import "MeContainerViewController.h"
#import "FavRestConsumer.h"
#import "LoginViewController.h"
#import "UserManager.h"
#import "SyncManager.h"
#import "Utils.h"
#import "Device.h"
#import "FavRestConsumer.h"
#import "UserManager.h"
#import <AudioToolbox/AudioToolbox.h>
#import <AdSupport/ASIdentifierManager.h>
#import "FavRestConsumerHelper.h"
#import "Conection.h"

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
@property (nonatomic)             BOOL                    waitingForAPNS;

@end



@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
   
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
        if (launchOptions) {
            NSDictionary *remoteNotif = [launchOptions objectForKey:UIApplicationLaunchOptionsRemoteNotificationKey];
            [self parsePayload:remoteNotif];
        }
        
        // Set Apirater settings
        [self setApiraterSettings];

        // Clear app icon notification badges
        [application setApplicationIconBadgeNumber:0];

        self.waitingForAPNS = YES;
        
        [Appirater appLaunched:YES];
        
        self.request = [FavRestConsumerHelper createREQ];
        
        if (SYNCHRO_ACTIVATED)
            [[SyncManager singleton] startSyncProcess];
    }
    
    
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
//    self.window.backgroundColor = [UIColor whiteColor];
    [self.window makeKeyAndVisible];
    
    //Show the login view
    [self showInitView];
    
    
    return YES;
}

- (void)applicationDidEnterBackground:(UIApplication *)application {
    
   [[[SyncManager singleton] synchroTimer] invalidate];

}

- (void)applicationWillEnterForeground:(UIApplication *)application {
    

}

- (void)applicationDidBecomeActive:(UIApplication *)application {
   
}

- (void)applicationWillTerminate:(UIApplication *)application {

}

#pragma mark - Private methods
//------------------------------------------------------------------------------
-(void) showInitView{
    
    if (![[UserManager singleton] getUserSessionToken]) {
        
        self.setupSB = [UIStoryboard storyboardWithName:@"Setup" bundle:nil];
        
        LoginViewController *loginVC = [self.setupSB instantiateViewControllerWithIdentifier:@"loginVC"];
        
        UINavigationController *navLoginVC = [[UINavigationController alloc]initWithRootViewController:loginVC];
        
        self.window.rootViewController = navLoginVC;
    }else
        [self setTabBarItems];
    
}
//------------------------------------------------------------------------------
-(void)setTabBarItems {

    self.peopleSB = [UIStoryboard storyboardWithName:@"People" bundle:nil];
    PeopleTableViewController *peopleVC = [self.peopleSB instantiateViewControllerWithIdentifier:@"peopleTableVC"];
    UINavigationController *navPeopleVC = [[UINavigationController alloc]initWithRootViewController:peopleVC];

    self.timelineSB = [UIStoryboard storyboardWithName:@"Timeline" bundle:nil];
    TimeLineViewController *timelineVC = [self.timelineSB instantiateViewControllerWithIdentifier:@"timelineVC"];
    UINavigationController *navTimelineVC = [[UINavigationController alloc]initWithRootViewController:timelineVC];

    
    self.meSB = [UIStoryboard storyboardWithName:@"Me" bundle:nil];
    MeContainerViewController *meVC = [self.meSB instantiateViewControllerWithIdentifier:@"meContainerVC"];
    UINavigationController *navMeVC = [[UINavigationController alloc]initWithRootViewController:meVC];
    
    
    navPeopleVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"PeopleTabTitle", nil) image:[UIImage imageNamed:@"Icon_People_OFF"] selectedImage:[UIImage imageNamed:@"Icon_People_ON"]];
    
    timelineVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"TimelineTabTitle", nil) image:[UIImage imageNamed:@"Icon_Timeline_OFF"] selectedImage:[UIImage imageNamed:@"Icon_Timeline_ON"]];
    
    navMeVC.tabBarItem = [[UITabBarItem alloc] initWithTitle:NSLocalizedString(@"MeTabTitle", nil) image:[UIImage imageNamed:@"Icon_Me_OFF"] selectedImage:[UIImage imageNamed:@"Icon_Me_ON"]];
    
    
    self.tabBarController = [[UITabBarController alloc] init];
    self.tabBarController.viewControllers = @[navPeopleVC,navTimelineVC,navMeVC];
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

    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:(UIRemoteNotificationTypeBadge |
                                                                           UIRemoteNotificationTypeSound |
                                                                           UIRemoteNotificationTypeAlert)];
}

//------------------------------------------------------------------------------
-(void)application:(UIApplication *)app didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)devToken {
    
    DLog(@"APNS Response with token %@",devToken);
    BOOL disabledNotifications = [[NSUserDefaults standardUserDefaults] boolForKey:kWerePushNotificationsDisabled];
    if ( disabledNotifications )
        [Utils setValueToUserDefaults:@NO ToKey:kWerePushNotificationsDisabled];
    
	NSString *token = [[[[devToken description] stringByReplacingOccurrencesOfString:@"<" withString:@""]
                        stringByReplacingOccurrencesOfString:@">" withString:@""]
                       stringByReplacingOccurrencesOfString:@" " withString:@""];
    
    [[UserManager singleton] setDeviceToken:token];
    self.waitingForAPNS = NO;
}

//------------------------------------------------------------------------------
-(void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)err {
	//DLog(@"Notifications are disabled for this application.");
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error de registro"
                                                    message:@"No ha sido posible registrar este dispositivo. Verifica si tienes una conexión a internet y que el puerto 5223 TCP este abierto."
                                                   delegate:self
                                          cancelButtonTitle:nil
                                          otherButtonTitles:@"Reintentar", nil];
	
	alert.tag = kAlertViewError;
	[alert show];
}

//------------------------------------------------------------------------------
-(void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    
    if ( application.applicationState == UIApplicationStateActive ){
        
		NSDictionary *apsInfo = [userInfo objectForKey:@"aps"];
		NSString *alert = [apsInfo objectForKey:@"alert"];
        NSString *appName = [[NSBundle mainBundle] objectForInfoDictionaryKey:(NSString*)kCFBundleNameKey];
        
		UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:appName
															message:alert
														   delegate:self
												  cancelButtonTitle:NSLocalizedString(@"_ok",nil)
												  otherButtonTitles:nil];
		alertView.tag = kAlertViewForeGround;
		
		[alertView show];
        
        //CACTUS - S'hauria de controlar l'arribada de noms d'arxiu d'audio diferents.
        NSString *sound = [apsInfo valueForKey:@"sound"];
        
        if(!sound || (sound && [sound isEqualToString:@"default"]))
            AudioServicesPlayAlertSound(1007);
        else {
            
            SystemSoundID completeSound;
            NSURL *audioPath = [[NSBundle mainBundle] URLForResource:sound withExtension:@""];
            if(audioPath)
            {
                AudioServicesCreateSystemSoundID((__bridge CFURLRef)audioPath, &completeSound);
                AudioServicesPlaySystemSound (completeSound);
            }
        }
		
	} else {
        [self parsePayload:userInfo];
    }
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
                    pushMatch = [Match createTemporaryMatch];
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


//------------------------------------------------------------------------------
//-(void)updateUserData {
//    
//    //Check for device entity in CoreData
//    Device *device = [[UserManager singleton] getDevice];
//    if (!device) {
//        device = [Device updateWithDictionary:nil];
//        [[CoreDataManager singleton] saveContext];
//    }
//    [[FavRestConsumer sharedInstance] deviceRegistration:device withDelegate:self];
//}


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
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error {
    
}


@end
