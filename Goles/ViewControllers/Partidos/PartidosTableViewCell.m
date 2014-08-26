//
//  PartidosTableViewCell.m
//  Goles
//
//  Created by Maria Teresa Bañuls on 12/08/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "PartidosTableViewCell.h"

@implementation PartidosTableViewCell

- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        // Initialization code
    }
    return self;
}

- (void)awakeFromNib
{
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
