//
//  MoneyBetCell.h
//  Goles Messenger
//
//  Created by Delfin Pereiro on 18/12/13.
//  Copyright (c) 2013 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MoneyBetCell : UITableViewCell

@property (weak, nonatomic) IBOutlet UIPickerView *mQuantity;
@property (weak, nonatomic) IBOutlet UILabel *mCellInfoLabel;
@property (nonatomic, strong) NSArray *mItemsPickerView;

@end
