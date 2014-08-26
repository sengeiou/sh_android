//
//  CustomCellClassification.h
//  iGoles
//
//  Created by mac on 10/02/11.
//  Copyright 2011 Fav24. All rights reserved.
//

#import "Classification.h"

@interface ClassificationCell : UITableViewCell

@property (nonatomic,weak) IBOutlet UILabel *labelPos;
@property (nonatomic,weak) IBOutlet UILabel *labelTeam;
@property (nonatomic,weak) IBOutlet UILabel *labelPoints;
@property (nonatomic,weak) IBOutlet UILabel *labelPJ;
@property (nonatomic,weak) IBOutlet UILabel *labelPG;
@property (nonatomic,weak) IBOutlet UILabel *labelPE;
@property (nonatomic,weak) IBOutlet UILabel *labelPP;
@property (nonatomic,weak) IBOutlet UILabel *labelGF;
@property (nonatomic,weak) IBOutlet UILabel *labelGC;

-(void)configCellWithClassification:(Classification *)classification andMaxMatches:(int)maxMatches;

@end
