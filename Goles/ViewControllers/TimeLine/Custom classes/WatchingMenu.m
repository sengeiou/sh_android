//
//  WatchingMenu.m
//  Shootr
//
//  Created by Christian Cabarrocas on 19/10/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "WatchingMenu.h"

@interface WatchingMenu ()

@property (nonatomic,weak) 		IBOutlet 	UILabel 			*labelMatchName;
@property (nonatomic,weak) 		IBOutlet 	UILabel 			*labelNowPlaying;
@property (nonatomic,weak)      IBOutlet    UIButton            *buttonWatching;
@property (nonatomic,weak)      IBOutlet    UIButton            *buttonIgnore;

@end


@implementation WatchingMenu

#pragma mark - INIT
//------------------------------------------------------------------------------
- (instancetype)initWithCoder:(NSCoder *)aDecoder {
	if ((self = [super initWithCoder:aDecoder])) {
		[self basicSetup];
	}
	return self;
}

//------------------------------------------------------------------------------
- (void)basicSetup {
	
	[self.buttonWatching setTitle:NSLocalizedString(@"I'm Watching", nil) forState:UIControlStateNormal];
	[self.buttonIgnore setTitle:NSLocalizedString(@"Ignore", nil) forState:UIControlStateNormal];
	self.labelNowPlaying.text = NSLocalizedString(@"Now Playing", nil);

}
@end
