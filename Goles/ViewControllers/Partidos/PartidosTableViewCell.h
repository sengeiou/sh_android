//
//  PartidosTableViewCell.h
//  Goles
//
//  Created by Maria Teresa Bañuls on 12/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface PartidosTableViewCell : UITableViewCell

@property (nonatomic, strong) IBOutlet UILabel *lblTeamLocal;
@property (nonatomic, strong) IBOutlet UILabel *lblTeamVisitor;
@property (nonatomic, strong) IBOutlet UILabel *lblTV;
@property (nonatomic, strong) IBOutlet UILabel *lblDateStart;

@end
