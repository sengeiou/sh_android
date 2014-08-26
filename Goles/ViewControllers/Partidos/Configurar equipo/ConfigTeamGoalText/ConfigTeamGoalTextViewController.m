//
//  ConfigTeamGoalTextViewController.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 23/09/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "ConfigTeamGoalTextViewController.h"
#import "Utils.h"
#import "Device.h"
#import "Subscription.h"
#import "SML.h"
#import "UserManager.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface ConfigTeamGoalTextViewController ()

@end

@implementation ConfigTeamGoalTextViewController

//------------------------------------------------------------------------------
- (id)initWithStyle:(UITableViewStyle)style {
    self = [super initWithStyle:style];
    if (self) {
        // Custom initialization
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad {
    
    [super viewDidLoad];
}

//------------------------------------------------------------------------------
-(void)viewWillAppear:(BOOL)animated{
    
    [super viewWillAppear:animated];
    self.navigationItem.title = NSLocalizedString(@"Mensaje de gol",nil);
}

//------------------------------------------------------------------------------
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

#pragma mark - Table view delegate

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return 3;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    
    cell.textLabel.textColor = [UIColor blackColor];
    
    if(indexPath.section == 0) {
        
        SML *sml = self.mOriginalTeamData.subscription.sml;
        
        if(indexPath.row == 0) {
            
            cell.textLabel.text = NSLocalizedString(@"Gooooool!!!",nil);
            if( [sml.message isEqualToNumber:@1] )
                cell.accessoryType = UITableViewCellAccessoryCheckmark;
            else
                cell.accessoryType = UITableViewCellAccessoryNone;
        }
        else if(indexPath.row == 1)
        {
            [cell.textLabel setText:NSLocalizedString(@"Goool!",nil)];
            if( [sml.message isEqualToNumber:@0] )
                cell.accessoryType = UITableViewCellAccessoryCheckmark;
            else
                cell.accessoryType = UITableViewCellAccessoryNone;
            
        }
        else if(indexPath.row == 2)
        {
            cell.textLabel.text = NSLocalizedString(@"Gol",nil);
            if( [[sml message] isEqualToNumber:@2] )
                cell.accessoryType = UITableViewCellAccessoryCheckmark;
            else
                cell.accessoryType = UITableViewCellAccessoryNone;
        }
    }

    return cell;
}

//------------------------------------------------------------------------------
- (UIView *) tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    
    NSUInteger selectedMessage = [self.mOriginalTeamData.subscription.sml messageValue];
    NSString *contraryMessageText;
    NSString *selectedMessageText;
    
    switch (selectedMessage) {
        case 1:
            selectedMessageText = NSLocalizedString(@"Gooooool!!!",nil);
            contraryMessageText = NSLocalizedString(@"Gol",nil);
            break;
        case 2:
            selectedMessageText = NSLocalizedString(@"Gol",nil);
            contraryMessageText = NSLocalizedString(@"Gooooool!!!",nil);
            break;
        default:
            selectedMessageText = [self.mOriginalTeamData.subscription.sml getMessageString];
            contraryMessageText = [self.mOriginalTeamData.subscription.sml getMessageString];
            break;
    }
    
    return [Utils createTableFooterWithText:[NSString stringWithFormat:NSLocalizedString(@"_TeamDetailGoalMessageFooter",nil),selectedMessageText,contraryMessageText]];
}

//------------------------------------------------------------------------------
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    SML *sml = self.mOriginalTeamData.subscription.sml;

    int messageValue;
    
    if(indexPath.row == 0)
        messageValue = 1;
    else if(indexPath.row ==1)
        messageValue = 0;
    else if(indexPath.row ==2)
        messageValue = 2;
    else
        messageValue = 1;
    
    [self.tableView reloadData];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"sound == %d && message == %d && language = 0",sml.soundValue,messageValue];
    NSArray *smlArray = [[CoreDataManager sharedInstance] getAllEntities:[SML class] withPredicate:predicate];
    
    if ([smlArray count] > 0) {
        self.mOriginalTeamData.subscription.sml = [smlArray firstObject];
        self.mOriginalTeamData.subscription.csys_syncronized = kJSON_SYNCRO_UPDATED;
        [[CoreDataManager sharedInstance] saveContext];
    }


}

#pragma mark - REST METHODS

//------------------------------------------------------------------------------
-(void) addSubscriptionDidResponse {
    [self.tableView reloadData];
}

@end
