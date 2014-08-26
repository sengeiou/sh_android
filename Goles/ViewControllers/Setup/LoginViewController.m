//
//  LoginViewController.m
//  Goles Messenger
//
//  Created by Maria Teresa Bañuls on 01/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "LoginViewController.h"
#import <FacebookSDK/FacebookSDK.h>
#import <AddressBookUI/AddressBookUI.h>
#import "UserManager.h"
#import "CoreDataManager.h"

@interface LoginViewController () <FBLoginViewDelegate>{
    ABAddressBookRef _addressBookRef;

}

@property (nonatomic, strong) FBSession *fbSession;
@property (nonatomic, strong) NSArray *friensFacebook;
@property (nonatomic, strong) NSArray *friensAgenda ;

@end

@implementation LoginViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    
  //  FBLoginView *loginView = [[FBLoginView alloc] init];
    // Align the button in the center horizontally
    
    if (self.friensFacebook == nil)
        self.friensFacebook = [[NSArray alloc]init];
    
    
//   FBLoginView * loginView =
//    [[FBLoginView alloc] initWithReadPermissions:
//     @[@"public_profile", @"email", @"user_friends"]];
//    loginView.delegate = self;
//    NSLog(@"permissions::%@",FBSession.activeSession.permissions);
//
//    loginView.frame = CGRectMake(70, 200, 218, 46);
//    [self.view addSubview:loginView];

    [self getUsersAddressBook:^(NSArray *usersArray, NSError *error) {
        self.friensAgenda = usersArray;
    }];
}


-(IBAction)loginWithFacebook:(id)sender{
    FBSession *session = [[FBSession alloc] initWithPermissions:@[@"user_photos",@"email",@"user_about_me",@"user_friends", @"friends_about_me",
                                                                  @"user_birthday", @"friends_photos", @"friends_birthday"]];
    
    [FBSession setActiveSession:session];

    [session openWithCompletionHandler:^(FBSession *session, FBSessionState status, NSError *error) {

        [[FBRequest requestForMe] startWithCompletionHandler:^(FBRequestConnection *connection, NSDictionary<FBGraphUser> *FBuser, NSError *error) {
            if (error) {
                NSLog(@"********* FACEBOOK error al obtener mi perfil ************ %s error: %@  ", __PRETTY_FUNCTION__,error.localizedDescription);
            }else {
                
               // NSString *userName = [FBuser name];
                //NSString *userImageURL = [NSString stringWithFormat:@"https://graph.facebook.com/%@/picture?type=large", [FBuser objectID]];
            }
        }];

        if (error) {
            NSLog(@"********* FACEBOOK cancelar ************");
        
        }else{
            
            NSString *fbAccessToken = [[session accessTokenData] accessToken];
            NSLog(@"facebook token:%@", fbAccessToken);


            [UserManager sharedInstance].mUser.tokenFacebook = fbAccessToken;
            
            if (fbAccessToken != nil){
                self.fbSession = session;
                
                
                
                [UserManager sharedInstance].mUser.sessionFacebook = [NSNumber numberWithBool:YES];
                
                [[CoreDataManager singleton] saveContext];
                
                [self getUsersFacebook:^(NSArray *usersArray, NSError *error) {
                    self.friensFacebook = usersArray;
                    
                    //cuando terminemos de obtener los friends de facebook pasamos dentro de la app
                    [self performSelectorOnMainThread:@selector(passView) withObject:nil waitUntilDone:NO];
                    
                }];
            }
        }
        
    }];
}

-(void) passView{
    [[NSNotificationCenter defaultCenter] postNotificationName:@"enterOfApp" object:nil];
}

-(IBAction)logout{
    [FBSession.activeSession closeAndClearTokenInformation];
    
    [UserManager sharedInstance].mUser.sessionFacebook = [NSNumber numberWithBool:NO];

}

#pragma mark -  Facebook users
-(void)getUsersFacebook:(usersReturnBlock) block
{
    if (!self.fbSession) {
        if (block) block(nil,nil);
        return;
    }
    
    FBRequest *friendsRequest = [FBRequest requestForGraphPath:@"/me/user_friends"];
    [friendsRequest startWithCompletionHandler: ^(FBRequestConnection *connection,
                                                  NSDictionary* result,
                                                  NSError *error) {
        
        if (error) {
            NSLog(@"%s error: %@",__PRETTY_FUNCTION__,error.localizedDescription);
            if (block) block(nil,error);
        } else {
            NSArray* friends = [result objectForKey:@"data"];
            
           // NSLog(@"Found: %i friends", friends.count);
            /*for (NSDictionary<FBGraphUser>* friend in friends) {
                NSLog(@"I have a friend named %@ with id %@", friend.name, friend.id);
            }*/
            
           // NSMutableArray *users = [NSMutableArray arrayWithCapacity:friends.count];
            NSArray *users = [[NSArray alloc] initWithArray:friends];
            /*for (NSDictionary *friend in friends) {
                [users addObject:[NVBUser userWithFacebookDictionary:friend]];
            }*/
            if (block) block(users,nil);
        }
    }];
}

#pragma mark -  AddressBook users

-(void)getUsersAddressBook:(usersReturnBlock) block
{
    [self getAddressBookAccessPermission:^(BOOL success, NSError *error) {
        if (success) {
            // Obtenemos todos los contactos del usuario en base a la referencia obtenida
            CFArrayRef all = ABAddressBookCopyArrayOfAllPeople(_addressBookRef);
            
            // Obtenemos el total de contactos encontrados
            CFIndex n = ABAddressBookGetPersonCount(_addressBookRef);
            
            NSMutableArray *users = [NSMutableArray arrayWithCapacity:n];
            // Recorremos todos los contactos
            for( int i = 0 ; i < n ; i++ )
            {
                NSMutableDictionary *usersDict = [[NSMutableDictionary alloc]init];
                
                // Obtenemos el contacto
                ABRecordRef ref = CFArrayGetValueAtIndex(all, i);
                
                // Si no tiene teléfono no nos vale. Solo podemos identificarlos por su teléfono!
                ABMultiValueRef phoneNumberProperty = ABRecordCopyValue(ref, kABPersonPhoneProperty);
                NSArray *phones = (__bridge_transfer NSArray *)ABMultiValueCopyArrayOfAllValues(phoneNumberProperty);
                NSString *phone = [phones lastObject];
                CFRelease(phoneNumberProperty);
                
                if (!phone) continue;
                
                [usersDict addEntriesFromDictionary:@{ @"phone":phone}];
                
                // Obtenemos el nombre del contacto
                NSMutableArray *nameComponents = [NSMutableArray array];
                for (id property in @[@(kABPersonFirstNameProperty),@(kABPersonMiddleNameProperty),@(kABPersonLastNameProperty)]) {
                    NSString *component = (__bridge_transfer NSString *)ABRecordCopyValue(ref, [property integerValue]);
                    if (component) [nameComponents addObject:component];
                }
                if ([nameComponents count] == 0) continue;
                [usersDict addEntriesFromDictionary:@{ @"name":[nameComponents componentsJoinedByString:@" "]}];
                
                // Obtenemos los emails disponibles
                ABMultiValueRef emailProperty = ABRecordCopyValue(ref, kABPersonEmailProperty);
                NSArray *emails = (__bridge_transfer NSArray*)ABMultiValueCopyArrayOfAllValues(emailProperty);
                NSString *email = [emails lastObject];
                CFRelease(emailProperty);
                
                if (email) [usersDict addEntriesFromDictionary:@{ @"email":email}];
                
                
                // Obtenemos las fotos disponibles
                NSData *photo = (__bridge_transfer NSData*)ABPersonCopyImageDataWithFormat(ref, kABPersonImageFormatThumbnail);
                
                
                if (photo) [usersDict addEntriesFromDictionary:@{ @"avatarImage":photo}];
                
                
                //[users addObject:[NVBUser userWithAddressBookDictionary:usersDict]];
                
            }
            
            CFRelease(all);
            
            if (block) block(users,nil);
            
        } else {
            if (block) block(nil,nil);
        }
    }];
    
}


-(void)getAddressBookAccessPermission:(booleanReturnBlock)block
{
    // Solicitamos al usuario acceso a los contactos (no funciona en el Simulador)
    if (!_addressBookRef) _addressBookRef = ABAddressBookCreateWithOptions(NULL, NULL);
    
    // Comprobamos el estado de permisos de usuario
    if (ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusNotDetermined) {
        ABAddressBookRequestAccessWithCompletion(_addressBookRef, ^(bool granted, CFErrorRef error) {
            if(block) block(granted,nil);
        });
    }
    else if (ABAddressBookGetAuthorizationStatus() == kABAuthorizationStatusAuthorized) {
        // El usuario ya nos había autorizado previamente el acceso
        if(block) block(YES,nil);
    }
    else {
        // El usuario ya nos había denegado previamente el acceso
        NSLog(@"Permiso denegado");
        [[[UIAlertView alloc] initWithTitle:@"Error" message:@"Permiso denegado!" delegate:self cancelButtonTitle:@"Ok" otherButtonTitles:nil] show];
        if(block) block(NO,nil);
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
