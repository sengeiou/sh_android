//
//  BetDescriptionCell.h
//  Goles Messenger
//
//  Created by Delfin Pereiro on 19/12/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "BetTypeOdd.h"

@interface BetDescriptionCell : UITableViewCell

@property (nonatomic, strong) UILabel   *mValueLabel;
@property (nonatomic, strong) UILabel   *mCuotaLabel;

- (void)configBetCellWithBetTypeOdd:(BetTypeOdd *)betTypeOdd andEuros:(NSNumber *)euros;
- (void)configBetCellWithoutOdds;

@end
