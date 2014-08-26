//
//  BetWebViewController.m
//  Goles Messenger
//
//  Created by Luis Rollon on 03/02/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "BetWebViewController.h"
#import "ProviderBetViewController.h"
#import "LandingPageViewController.h"
#import "CoreDataParsing.h"
#import "ActivityProvider.h"
#import "Utils.h"

#define kAlertViewOne 1
#define kAlertViewTwo 2

@interface BetWebViewController () <UIWebViewDelegate>

@property (nonatomic, strong) UIBarButtonItem *backBarButtonItem;
@property (nonatomic, strong) UIBarButtonItem *forwardBarButtonItem;
@property (nonatomic, strong) UIBarButtonItem *refreshBarButtonItem;
@property (nonatomic, strong) UIBarButtonItem *stopBarButtonItem;
@property (nonatomic, strong) UIBarButtonItem *shareBarButtonItem;

@property (nonatomic, strong) UIActivityIndicatorView *loadingWebActivityIndicator;

@property (nonatomic, strong) UIWebView *webView;
@property (nonatomic, strong) NSURL *URL;

@property (nonatomic, strong) UILabel *loadingTextLabel;
@property (nonatomic, strong) UILabel *removeBetTextLabel;
@property (nonatomic, strong) UIImageView *removeBetImageView;

@property                     BOOL goToRegistry;

//- (id)initWithAddress:(NSString*)urlString;
//- (id)initWithURL:(NSURL*)URL;
- (void)loadURL:(NSURL*)URL;

- (void)updateToolbarItems;

- (void)goBackClicked:(UIBarButtonItem *)sender;
- (void)goForwardClicked:(UIBarButtonItem *)sender;
- (void)reloadClicked:(UIBarButtonItem *)sender;
- (void)stopClicked:(UIBarButtonItem *)sender;

@end


@implementation BetWebViewController

#pragma mark - Initialization

//------------------------------------------------------------------------------
- (void)dealloc {
    [self.webView stopLoading];
 	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
    self.webView.delegate = nil;
}

//------------------------------------------------------------------------------
//- (id)initWithAddress:(NSString *)urlString {
//    return [self initWithURL:[NSURL URLWithString:urlString]];
//}

//------------------------------------------------------------------------------
- (id)initWithProvider:(Provider*)provider andURL:(NSURL *)url andGoToRegistry:(BOOL)goToRegistry
{
    if(self = [super init]){
        self.mProvider = provider;
        self.URL = url;
        self.goToRegistry = goToRegistry;
    }
    return self;
}

//------------------------------------------------------------------------------
//- (id)initWithURL:(NSURL*)pageURL {
//    
//    if(self = [super init]) {
//        self.URL = pageURL;
//    }
//    return self;
//}

//------------------------------------------------------------------------------
- (void)loadURL:(NSURL *)pageURL {
    [self.webView loadRequest:[NSURLRequest requestWithURL:pageURL]];
}

#pragma mark - View lifecycle

//------------------------------------------------------------------------------
- (void)loadView {
    self.view = self.webView;
    [self loadURL:self.URL];
}

//------------------------------------------------------------------------------
- (void)viewDidLoad {
	[super viewDidLoad];
    [self updateToolbarItems];
    
    self.loadingWebActivityIndicator = [[UIActivityIndicatorView alloc]initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
//    self.loadingWebActivityIndicator.frame = CGRectMake((self.webView.frame.size.width/2)-80, (self.view.frame.size.height/2)-30, 30, 30);
    self.loadingWebActivityIndicator.frame = CGRectMake((self.webView.frame.size.width/2)-15, (self.view.frame.size.height/2)-55, 30, 30);
    
    self.loadingTextLabel = [[UILabel alloc]initWithFrame:CGRectMake((self.webView.frame.size.width/2)-110, (self.view.frame.size.height/2)-30, 220, 41)];
    self.loadingTextLabel.textAlignment = NSTextAlignmentCenter;
    self.loadingTextLabel.numberOfLines = 2;
    [self.loadingTextLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:14.0]];
    self.loadingTextLabel.textColor = [UIColor blackColor];
    
    if(self.goToRegistry)
        self.loadingTextLabel.text = [NSString stringWithFormat:@"Accediendo al formulario de \nregistro de %@...", self.mProvider.name];
    else{
        self.loadingTextLabel.text = [NSString stringWithFormat:@"Preparando tu apuesta en %@...", self.mProvider.name];

        self.removeBetTextLabel = [[UILabel alloc]initWithFrame:CGRectMake(46, self.webView.frame.size.height-130, 199, 21)];
        self.removeBetTextLabel.textAlignment = NSTextAlignmentCenter;
        [self.removeBetTextLabel setFont:[UIFont fontWithName:@"HelveticaNeue-Light" size:14.0]];
        self.removeBetTextLabel.textColor = [UIColor lightGrayColor];
        self.removeBetTextLabel.text = [NSString stringWithFormat:@"Para borrar la apuesta pulsa"];
        
        self.removeBetImageView = [[UIImageView alloc]initWithImage:[UIImage imageNamed:@"removeBetBwin.png"]];
        self.removeBetImageView.frame = CGRectMake(235, self.webView.frame.size.height-129, 20, 20);
    }
    
    
    [self.webView addSubview:self.loadingWebActivityIndicator];
    [self.webView addSubview:self.loadingTextLabel];
    [self.webView addSubview:self.removeBetTextLabel];
    [self.webView addSubview:self.removeBetImageView];
    [self.loadingWebActivityIndicator startAnimating];
    
    UIBarButtonItem *backButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:@selector(backClicked:)];

    [[self navigationItem] setLeftBarButtonItem:backButton];
    
    self.navigationItem.titleView = [Utils setNavigationBarTitle:@"Bwin" andSubtitle:[self.URL absoluteString] forMaximumLenght:[NSNumber numberWithInt:220]];
//    self.navigationItem.title = @"Bwin";
    
}

//------------------------------------------------------------------------------
-(void)backClicked:(id)sender
{
    NSString *messageString = [NSString stringWithFormat:@"¿Salir de %@ y volver a Goles Messenger?", self.mProvider.name];
    UIAlertView *backToGoles = [[UIAlertView alloc]initWithTitle:nil message:messageString delegate:self cancelButtonTitle:@"Cancelar" otherButtonTitles:@"OK", nil];
    backToGoles.tag = kAlertViewTwo;
    
    NSUserDefaults *localStorage = [NSUserDefaults standardUserDefaults];
    if (![localStorage objectForKey:kCUOTAS_USERCLICK]) {
    
        [backToGoles show];
    }else
        [self.navigationController popViewControllerAnimated:YES];
}

//------------------------------------------------------------------------------
- (void)viewDidUnload {
    [super viewDidUnload];
    self.webView = nil;
    _backBarButtonItem = nil;
    _forwardBarButtonItem = nil;
    _refreshBarButtonItem = nil;
    _stopBarButtonItem = nil;
    _shareBarButtonItem = nil;
}

//------------------------------------------------------------------------------
- (void)viewWillAppear:(BOOL)animated {
	[super viewWillAppear:animated];
    [self.navigationController setToolbarHidden:NO animated:animated];
    
    NSString *titleMessage = [NSString stringWithFormat:@"Bienvenido a %@", self.mProvider.name];
    
    UIAlertView *alertMore17 = [[UIAlertView alloc] initWithTitle:titleMessage message:@"Has salido de Goles Messenger. Juega con responsabilidad. Pulsa Continuar si tienes más de 17 años." delegate:self cancelButtonTitle:@"Cancelar" otherButtonTitles:@"Continuar", nil];
    alertMore17.tag = kAlertViewOne;
    [alertMore17 setDelegate:self];
    
    //If the user doesnt tap on Im registered yet or is the first time that the user go to the webView I will show him the alertView
    NSUserDefaults *localStorage = [NSUserDefaults standardUserDefaults];
    if (![localStorage objectForKey:kCUOTAS_USERCLICK]) {
        [alertMore17 show];
    }else if(![localStorage objectForKey:kFIRSTTIMEALERTMORE17]) {
        [localStorage setBool:NO forKey:kFIRSTTIMEALERTMORE17];
        [alertMore17 show];
    }
}

//------------------------------------------------------------------------------
-(void) alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if(alertView.tag == kAlertViewOne){
    
        if (buttonIndex == 0)
            [self.navigationController popViewControllerAnimated:YES];
//        else if (buttonIndex == 1){
//
//        //If the user tap on Continue I set the flag to YES
//        NSUserDefaults *localStorage = [NSUserDefaults standardUserDefaults];
//        [localStorage setBool:YES forKey:kCUOTAS_MORETHAN17];
//    
//        }
    }else if(alertView.tag == kAlertViewTwo){
        if (buttonIndex == 1)
            [self.navigationController popViewControllerAnimated:YES];
    }
}

//------------------------------------------------------------------------------
- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    [self.navigationController setToolbarHidden:YES animated:animated];
}

//------------------------------------------------------------------------------
- (void)viewDidDisappear:(BOOL)animated {
    [super viewDidDisappear:animated];
    [[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
}

#pragma mark - Getters

//------------------------------------------------------------------------------
- (UIWebView*)webView {
    if(!_webView) {
        _webView = [[UIWebView alloc] initWithFrame:[UIScreen mainScreen].bounds];
        _webView.delegate = self;
        _webView.scalesPageToFit = YES;
    }
    return _webView;
}

//------------------------------------------------------------------------------
- (UIBarButtonItem *)backBarButtonItem {
    if (!_backBarButtonItem) {
        _backBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"WebViewBackImage.png"]
                                                              style:UIBarButtonItemStylePlain
                                                             target:self
                                                             action:@selector(goBackClicked:)];
		_backBarButtonItem.width = 18.0f;
    }
    return _backBarButtonItem;
}

//------------------------------------------------------------------------------
- (UIBarButtonItem *)forwardBarButtonItem {
    if (!_forwardBarButtonItem) {
        _forwardBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"WebViewNextImage.png"]
                                                                 style:UIBarButtonItemStylePlain
                                                                target:self
                                                                action:@selector(goForwardClicked:)];
		_forwardBarButtonItem.width = 18.0f;
    }
    return _forwardBarButtonItem;
}

//------------------------------------------------------------------------------
- (UIBarButtonItem *)refreshBarButtonItem {
    if (!_refreshBarButtonItem) {
        _refreshBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(reloadClicked:)];
    }
    return _refreshBarButtonItem;
}

//------------------------------------------------------------------------------
- (UIBarButtonItem *)stopBarButtonItem {
    if (!_stopBarButtonItem) {
        _stopBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemStop target:self action:@selector(stopClicked:)];
    }
    return _stopBarButtonItem;
}

//------------------------------------------------------------------------------
- (UIBarButtonItem *)shareBarButtonItem {
    if (!_shareBarButtonItem) {
        _shareBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAction target:self action:@selector(shareButtonClicked:)];
    }
    return _shareBarButtonItem;
}

#pragma mark - Toolbar

//------------------------------------------------------------------------------
/**
 @brief Config Toolbar buttons
 
 @discussion Refresh button will be show when the web has been loaded. Stop button
 will be show when the web is loading
 */
//------------------------------------------------------------------------------
- (void)updateToolbarItems {
    self.backBarButtonItem.enabled = self.webView.canGoBack;
    self.forwardBarButtonItem.enabled = self.webView.canGoForward;
    self.shareBarButtonItem.enabled = !self.webView.isLoading;
    
    UIBarButtonItem *refreshBarButtonItem = self.refreshBarButtonItem;

    
    self.navigationItem.rightBarButtonItem = refreshBarButtonItem;

    UIBarButtonItem *fixedSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:nil];
    UIBarButtonItem *fixedSpace2 = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFixedSpace target:nil action:nil];
    UIBarButtonItem *flexibleSpace = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemFlexibleSpace target:nil action:nil];
    
    NSArray *items = [NSArray arrayWithObjects:
                          self.backBarButtonItem,
                          fixedSpace,flexibleSpace, fixedSpace,
                          self.forwardBarButtonItem,
                          fixedSpace, flexibleSpace, fixedSpace2, flexibleSpace, fixedSpace, flexibleSpace, fixedSpace, flexibleSpace, fixedSpace, flexibleSpace,
                          self.shareBarButtonItem,
                          nil];
        
        self.navigationController.toolbar.barStyle = self.navigationController.navigationBar.barStyle;
        self.navigationController.toolbar.tintColor = self.navigationController.navigationBar.tintColor;
        self.toolbarItems = items;
}

#pragma mark - UIWebViewDelegate

//------------------------------------------------------------------------------
- (void)webViewDidStartLoad:(UIWebView *)webView {
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
    [self updateToolbarItems];
}

//------------------------------------------------------------------------------
- (void)webViewDidFinishLoad:(UIWebView *)webView {
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
    [self.loadingWebActivityIndicator stopAnimating];
    self.loadingTextLabel.hidden = YES;
    self.removeBetTextLabel.hidden = YES;
    self.removeBetImageView.hidden = YES;
    
    [self updateToolbarItems];
}

//------------------------------------------------------------------------------
- (void)webView:(UIWebView *)webView didFailLoadWithError:(NSError *)error {
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
    [self.loadingWebActivityIndicator stopAnimating];
    self.loadingTextLabel.hidden = YES;
    self.removeBetImageView.hidden = YES;
    self.removeBetImageView.hidden = YES;

    self.navigationItem.title = @"";
    [self updateToolbarItems];
}

#pragma mark - Target actions

//------------------------------------------------------------------------------
- (void)goBackClicked:(UIBarButtonItem *)sender {
    [self.webView goBack];
}

//------------------------------------------------------------------------------
- (void)goForwardClicked:(UIBarButtonItem *)sender {
    [self.webView goForward];
}

//------------------------------------------------------------------------------
- (void)reloadClicked:(UIBarButtonItem *)sender {
    [self.webView reload];
}

//------------------------------------------------------------------------------
- (void)stopClicked:(UIBarButtonItem *)sender {
    [self.webView stopLoading];
	[self updateToolbarItems];
}

//------------------------------------------------------------------------------
- (void)shareButtonClicked:(id)sender {
    
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
- (void)doneButtonClicked:(id)sender {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

@end

