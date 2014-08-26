//
//  InformationToBetViewController.m
//  Goles Messenger
//
//  Created by Luis Rollon on 20/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "InformationToBetViewController.h"
#import "Utils.h"
#import "BetWebViewController.h"
#import "ProviderBetViewController.h"
#import "CuotasManager.h"
#import "ActivityProvider.h"

@interface InformationToBetViewController ()

@end

@implementation InformationToBetViewController

//------------------------------------------------------------------------------
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

//------------------------------------------------------------------------------
- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [self.scrollView setScrollEnabled:YES];
    [self createNavigationButtons];
    self.title = @"Cómo funciona";
    [self createInformationView];
    
}

//------------------------------------------------------------------------------
- (void)createInformationView {
    
    NSString *textLink = [NSString stringWithFormat:@"Registrarse en %@ con Goles Messenger", self.mProvider.name];
    
    UIView *firstView = [Utils createTableHeaderView:NSLocalizedString(@"_firstTextInView", nil)
                                            withFirstLink:textLink
                                       andSecondLink:@"Enviar a un amigo bono exclusivo de 100€"
                                    inBottomPosition:YES
                                           andFirstMethod:@selector(onFirstRegistryButtonClick:)
                         andSecondMethod:@selector(onShareButtonClick:)
                                        withDelegate:self];
    
    [self.scrollView addSubview:firstView];
    
    CGFloat heightScroll = firstView.frame.size.height+80;
    
    self.scrollView.contentSize= CGSizeMake(320, heightScroll);
    
}

//------------------------------------------------------------------------------
- (void)onFirstRegistryButtonClick:(id)sender
{
    if (!(self.mProvider.registryURL == nil)) {
    
        NSURL *url = [NSURL URLWithString:self.mProvider.registryURL];
        BetWebViewController *betWebVC = [[BetWebViewController alloc] initWithProvider:self.mProvider andURL:url andGoToRegistry:YES];
        [betWebVC setMProvider:self.mProvider];
        [self.navigationController pushViewController:betWebVC animated:YES];
    }
}

//------------------------------------------------------------------------------
- (void)onShareButtonClick:(id)sender
{
    ActivityProvider *activityProvider = [[ActivityProvider alloc]init];
    [activityProvider setMProvider:self.mProvider];
    NSArray *items = @[activityProvider];
    UIActivityViewController *activityVC = [[UIActivityViewController alloc]initWithActivityItems:items applicationActivities:nil];
    
    NSString *activityMessage = [NSString stringWithFormat:@"Oferta exclusiva 100€ %@ con Goles Messenger", self.mProvider.name];
    
    [activityVC setValue:activityMessage forKey:@"subject"];
    
    activityVC.excludedActivityTypes = @[UIActivityTypePrint, UIActivityTypePostToWeibo, UIActivityTypeCopyToPasteboard, UIActivityTypeSaveToCameraRoll];

    [self presentViewController:activityVC animated:YES completion:nil];
    
    [activityVC setCompletionHandler:^(NSString *act, BOOL done)
    {
        NSString *ServiceMsg = nil;
        if ( [act isEqualToString:UIActivityTypeMail] )           ServiceMsg = @"Email enviado.";
        if ( [act isEqualToString:UIActivityTypePostToTwitter] )  ServiceMsg = @"Tweet enviado.";
        if ( [act isEqualToString:UIActivityTypePostToFacebook] ) ServiceMsg = @"Post publicado.";
        if ( [act isEqualToString:UIActivityTypeMessage] )        ServiceMsg = @"Mensaje enviado.";
        if ( done )
        {
            UIAlertView *Alert = [[UIAlertView alloc] initWithTitle:ServiceMsg message:@"" delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [Alert show];
        }
    }];
}


//------------------------------------------------------------------------------
- (void)createNavigationButtons {

    UIBarButtonItem *closeButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"_close", @"") style:UIBarButtonItemStylePlain target:self action:@selector(onCloseButtonClick:)];
    UIBarButtonItem *mailButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(onMailButtonClick:)];
    [[self navigationItem] setLeftBarButtonItem:closeButton];
    [[self navigationItem] setRightBarButtonItem:mailButton];
}

//------------------------------------------------------------------------------
- (void)onCloseButtonClick:(id) sender {
    
    [[self navigationController] dismissViewControllerAnimated:YES completion:nil];
}

//------------------------------------------------------------------------------
-(void)onMailButtonClick:(id)sender{
    
    if ([MFMailComposeViewController canSendMail]) {
    
        MFMailComposeViewController *composePage=[[MFMailComposeViewController alloc]init];
        composePage.mailComposeDelegate = self;
        
        NSString *emailBodyHTML = [NSString stringWithFormat:NSLocalizedString(@"_informationEmailBodyHTML", nil), self.mProvider.registryURL, self.mProvider.registryURL];
        
        [composePage setMessageBody:emailBodyHTML isHTML:YES];
        
        NSLog(@"Valor de la URL %@", self.mProvider.registryURL);
    
        //Set the email subject
        
        NSString *emailSubject = [NSString stringWithFormat:@"Oferta exclusiva 100€ %@ con Goles Messenger", self.mProvider.name];
        [composePage setSubject:emailSubject];
    
        [composePage setTitle:@"Email"];
        [self presentViewController:composePage animated:YES completion:nil];
    }
    else{
        [self errorEmail];
    }
    
}

//------------------------------------------------------------------------------
- (void)errorEmail {
    UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:NSLocalizedString(@"Error",nil)
                                                        message:NSLocalizedString(@"Debes tener configurada una cuenta de correo para poder acceder a esta opción.",nil)
                                                       delegate:nil
                                              cancelButtonTitle:NSLocalizedString(@"Aceptar",nil)
                                              otherButtonTitles:nil];
    [alertView show];
}

//------------------------------------------------------------------------------
- (void)mailComposeController:(MFMailComposeViewController *)controller didFinishWithResult:(MFMailComposeResult)result error:(NSError *)error {
    
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    
}

@end
