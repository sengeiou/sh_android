//
//  ActivityProvider.m
//  Goles Messenger
//
//  Created by Luis Rollon on 24/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ActivityProvider.h"

@implementation ActivityProvider

//------------------------------------------------------------------------------
- (id) activityViewController:(UIActivityViewController *)activityViewController
          itemForActivityType:(NSString *)activityType
{
    
    NSString *messageString;
    NSURL *url = [NSURL URLWithString:self.mProvider.registryURL];
    if (url)
        messageString = [NSString stringWithFormat:@"100€ gratis en %@ si te registras desde el enlace %@ \n\nPor tiempo limitado. Pásalo.", self.mProvider.name, url];
    else
        messageString = [NSString stringWithFormat:@"100€ gratis en %@ si te registras desde Goles Messenger.", self.mProvider.name];
        
    //Here we have the options to show a differente message for every way to share the link
    if ( [activityType isEqualToString:UIActivityTypePostToTwitter] )
        return messageString;
    if ( [activityType isEqualToString:UIActivityTypePostToFacebook] )
        return messageString;
    if ( [activityType isEqualToString:UIActivityTypeMessage] )
        return messageString;
    if ( [activityType isEqualToString:UIActivityTypeMail] )
        return messageString;
    return nil;
}

//------------------------------------------------------------------------------
- (id) activityViewControllerPlaceholderItem:(UIActivityViewController *)activityViewController { return @""; }

@end
