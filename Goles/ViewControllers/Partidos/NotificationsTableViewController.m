//
//  NotificationsTableViewController.m
//  Goles
//
//  Created by Maria Teresa Bañuls on 13/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "NotificationsTableViewController.h"
#import "MatchDetailTableViewController.h"
#import "AppDelegate.h"
#import "SubscriptionManager.h"
#import "Subscription.h"
#import "UserManager.h"
#import "Device.h"
#import "Services.pch"
#import "CoreDataManager.h"

@interface NotificationsTableViewController (){
   
    NSArray *typeNotification;
    int selectedIndex;
}

@end

@implementation NotificationsTableViewController

- (id)initWithStyle:(UITableViewStyle)style
{
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
         }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [self updateUserData];
    
    typeNotification = [NSArray arrayWithObjects:@"Ninguna",@"Básicas",@"Todas",@"Activadas por equipos", nil];

}
//------------------------------------------------------------------------------
-(void)updateUserData {
    
    //Check for device entity in CoreData
    Device *device = [[UserManager singleton] getDevice];
    if (!device) {
        device = [Device updateWithDictionary:nil];
        [[CoreDataManager singleton] saveContext];
    }
    
    //Populate basic DB Tables
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[SML class] withDelegate:nil];
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[Message class] withDelegate:nil];
    [[FavRestConsumer sharedInstance] getAllEntitiesFromClass:[AppAdvice class] withDelegate:nil];


    NSNumber *idDevice = [[UserManager sharedInstance] getIdDevice];
    if ([idDevice integerValue] > 0) {
        [[FavRestConsumer sharedInstance] getDeviceSuscriptions];
        [self appleServicesRegister];
    }/*else{
      [[RestConsumer sharedInstance] deviceRegistration:device withDelegate:self];
      }*/
}


//------------------------------------------------------------------------------
- (void)appleServicesRegister {
#if TARGET_IPHONE_SIMULATOR
    [[UserManager singleton] setDeviceToken:K_DEVICE_TOKEN_SIMULATOR];
#else
    AppDelegate *delegate = (AppDelegate*)[[UIApplication sharedApplication] delegate];
    //[delegate initCloudServices];
    [delegate registerAPNS];
#endif
}
-(void) back{
    NSLog(@"HHH");
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

#pragma mark - Table view data source

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return 4;
}


- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"notificationCell" forIndexPath:indexPath];
    
    cell.textLabel.text = typeNotification[indexPath.row];
    // Configure the cell...
    
    if(indexPath.row == selectedIndex)
    {
        cell.accessoryType = UITableViewCellAccessoryCheckmark;
    }
    else
    {
        cell.accessoryType = UITableViewCellAccessoryNone;
    }
    
    
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
    selectedIndex = indexPath.row;
    [self.tableView reloadData];
    
    switch (indexPath.row) {
        case 0:
            [[SubscriptionManager sharedInstance]setSubscriptionMode:0 forMatch:self.selectedMatch to:YES withDelegate:self];

            break;
        case 1:
            [[SubscriptionManager sharedInstance]setSubscriptionMode:1 forMatch:self.selectedMatch to:YES withDelegate:self];
            
            break;
        case 2:
            [[SubscriptionManager sharedInstance]setSubscriptionMode:2 forMatch:self.selectedMatch to:YES withDelegate:self];
            
            break;
            
        default:
            break;
    }
    
}

#pragma mark - Delegate Methods from Webservice
//------------------------------------------------------------------------------
- (void)parserResponseForClass:(Class)entityClass status:(BOOL)status andError:(NSError *)error {
    
    if ([entityClass isSubclassOfClass:[Subscription class]] && status && !error)
        [self.tableView reloadData];
}

/*
// Override to support conditional editing of the table view.
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the specified item to be editable.
    return YES;
}
*/

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath
{
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath] withRowAnimation:UITableViewRowAnimationFade];
    } else if (editingStyle == UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }   
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath
{
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath
{
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
