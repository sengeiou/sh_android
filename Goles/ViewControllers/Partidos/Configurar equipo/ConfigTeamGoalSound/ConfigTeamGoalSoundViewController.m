//
//  ConfigTeamGoalSoundViewController.m
//  Goles Messenger
//
//  Created by Christian Cabarrocas on 23/09/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import "ConfigTeamGoalSoundViewController.h"
#import <AudioToolbox/AudioToolbox.h>
#import "Device.h"
#import "SML.h"
#import "Utils.h"
#import "UserManager.h"
#import "CoreDataManager.h"
#import "CoreDataParsing.h"

@interface ConfigTeamGoalSoundViewController ()

@end

@implementation ConfigTeamGoalSoundViewController

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
    self.navigationItem.title = NSLocalizedString(@"Sonido de gol",nil);
}

//------------------------------------------------------------------------------
- (void) viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

#pragma mark - Table view data source

//------------------------------------------------------------------------------
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    
    return 4;
}

//------------------------------------------------------------------------------
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    
    cell.textLabel.textColor = [UIColor blackColor];
    
    if(indexPath.section == 0)
    {
        if(indexPath.row == 0)
            cell.textLabel.text = NSLocalizedString(@"Sistema",nil);
        else if(indexPath.row == 1)
            cell.textLabel.text = NSLocalizedString(@"Gooooool!!!",nil);
        else if(indexPath.row == 2)
            cell.textLabel.text = NSLocalizedString(@"_TeamDetailNeutralGoalSoundNameLabel",nil);
        else if(indexPath.row == 3)
            cell.textLabel.text = NSLocalizedString(@"Silbidos",nil);
        
        if ( self.mOriginalTeamData.subscription.sml.soundValue-1 ==  indexPath.row )
            [cell setAccessoryType:UITableViewCellAccessoryCheckmark];
        else
            [cell setAccessoryType:UITableViewCellAccessoryNone];
    }
    
    return cell;
}

//------------------------------------------------------------------------------
- (UIView *) tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    
    NSString *contraryMessageText;
    NSString *selectedMessageText;

    NSUInteger selectedMessage = self.mOriginalTeamData.subscription.sml.soundValue;
    switch (selectedMessage) {
        case 2:
            selectedMessageText = NSLocalizedString(@"Gooooool!!!",nil);
            contraryMessageText = NSLocalizedString(@"Silbidos",nil);
            break;
        case 4:
            selectedMessageText = NSLocalizedString(@"Silbidos",nil);
            contraryMessageText = NSLocalizedString(@"Gooooool!!!",nil);
            break;
        default:
            selectedMessageText = [self.mOriginalTeamData.subscription.sml getSoundMessageString];
            contraryMessageText = [self.mOriginalTeamData.subscription.sml getSoundMessageString];
            break;
    }
    
    return [Utils createTableFooterWithText:[NSString stringWithFormat:NSLocalizedString(@"_TeamDetailGoalSoundFooter",nil),selectedMessageText,contraryMessageText]];
}

//------------------------------------------------------------------------------
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    
    SML *sml = self.mOriginalTeamData.subscription.sml;
    
    int soundValue;
    NSString *sound = nil;
    
    if(indexPath.row == 0){
        soundValue = 1;
    }
    else if(indexPath.row ==1) {
        soundValue = 2;
         sound = @"g.aiff";
    }
    else if(indexPath.row ==2){
        soundValue = 3;
         sound = @"a.aiff";
    }
    else if(indexPath.row ==3){
        soundValue = 4;
        sound = @"s.aiff";
    }else
        soundValue = 100;

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
    
    [self.tableView reloadData];
    
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"sound == %d && message == %d && language = 0",soundValue,sml.messageValue];
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
