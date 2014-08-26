//
//  CustomCellClassification.m
//  iGoles
//
//  Marçal Albert Castellví on 10/02/11.
//  Copyright 2012 Opentrends. All rights reserved.
//

#import "ClassificationCell.h"
#import "Team.h"

#import "Fav24Colors.h"
#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>

@implementation ClassificationCell

#pragma mark - View lifecycle
//------------------------------------------------------------------------------
- (void)setSelected:(BOOL)selected animated:(BOOL)animated {
	
    [super setSelected:selected animated:animated];
	
}

//------------------------------------------------------------------------------
-(void)configCellWithClassification:(Classification *)classification andMaxMatches:(int)maxMatches{

    [[[self labelPoints] layer] setCornerRadius:12.0f];

    [[self labelPos] setText:[[classification weight] stringValue]];
    
   /* switch ([classification orderColorValue]) {
        case 0:
            [[self labelPoints] setBackgroundColor:[Fav24Colors iosSevenBlue]];
            break;
        case 1:
            [[self labelPoints] setBackgroundColor:[Fav24Colors iosSevenOrange]];
            break;
        case 2:
            [[self labelPoints] setBackgroundColor:[Fav24Colors iosSevenRed]];
            break;
        case 3:
            [[self labelPoints] setBackgroundColor:[UIColor lightGrayColor]];
        default:
            [[self labelPoints] setBackgroundColor:[UIColor lightGrayColor]];
            break;
    }*/
    
    int pl  = [classification plValue];
    int pv  = [classification pvValue];
    int wl  = [classification wlValue];
    int wv  = [classification wvValue];
    int dl  = [classification dlValue];
    int dv  = [classification dvValue];
    int ll  = [classification llValue];
    int lv  = [classification lvValue];
    int gfl = [classification gflValue];
    int gfv = [classification gfvValue];
    int gal = [classification galValue];
    int gav = [classification gavValue];
    
    int pj = pl  + pv;
    int pg = wl  + wv;
    int pe = dl  + dv;
    int pp = ll  + lv;
    int gf = gfl + gfv;
    int gc = gal + gav;
    
    self.labelTeam.text = [[classification team] nameShort];
    self.labelPoints.text = [NSString stringWithFormat:@"%@",[classification points]];
    
    self.labelPJ.text = [NSString stringWithFormat:@"%d", pj];
    self.labelPG.text = [NSString stringWithFormat:@"%d", pg];
    self.labelPE.text = [NSString stringWithFormat:@"%d", pe];
    self.labelPP.text = [NSString stringWithFormat:@"%d", pp];
    self.labelGF.text = [NSString stringWithFormat:@"%d", gf];
    self.labelGC.text = [NSString stringWithFormat:@"%d", gc];
    
    if (pj < maxMatches)        self.labelPJ.textColor = [UIColor redColor];
//    else                        self.labelPJ.textColor = self.labelPG.textColor;
    
    // Performance improvement
    [[self layer] setOpaque:NO];
    self.backgroundColor = [UIColor clearColor];
    [[self layer] setShouldRasterize:YES];
    [[self layer] setRasterizationScale:[[UIScreen mainScreen] scale]];
}

@end


