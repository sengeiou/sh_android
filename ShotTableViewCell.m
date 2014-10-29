          //
//  ShotTableViewCell.m
//
//  Created by Maria Teresa Bañuls on 23/09/14.
//  Copyright (c) 2014 Fav24. All rights reserved.
//

#import "ShotTableViewCell.h"
#import "User.h"
#import "TimeLineUtilities.h"
#import "NSString+CleanLinks.h"
#import "UIImageView+AFNetworking.h"
#import "UIButton+AFNetworking.h"
#import "AFHTTPRequestOperation.h"
#import "DownloadImage.h"
#import "Fav24Colors.h"
#import "TimeLineUtilities.h"

#define kLabelHorizontalLeftInsets          80.0f
#define kLabelHorizontalRightInsets         16.0f
#define kLabelHorizontalPhotoInsets         16.0f
#define kLabelVerticalPhotoInsets           11.5f
#define kLabelVerticalInsets                7.5f
#define kLabelVerticalBottomComment         0.0f //24.0f
#define kLabelVerticalCommentToName         1.0f

@interface ShotTableViewCell ()

@property (nonatomic, assign) BOOL didSetupConstraints;

@end


@implementation ShotTableViewCell

//------------------------------------------------------------------------------
- (void)awakeFromNib {
    
    
}

//------------------------------------------------------------------------------
- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) {
        
        self.backgroundColor = [UIColor whiteColor];
        
        //USERNAME
        self.lblName = [UILabel newAutoLayoutView];
        [self.lblName setLineBreakMode:NSLineBreakByTruncatingTail];
        self.lblName.font = [UIFont boldSystemFontOfSize:17];
        self.lblName.backgroundColor = [UIColor whiteColor];
        
        //COMMENT
        self.txvText = [UILabel newAutoLayoutView];
        [self.txvText setNumberOfLines:0];
        [self.txvText setLineBreakMode:NSLineBreakByWordWrapping];
        self.txvText.font = [UIFont systemFontOfSize:15];
        self.txvText.backgroundColor = [UIColor whiteColor];
        
        //PHOTO
        self.imgPhoto = [UIImageView newAutoLayoutView];

        //BUTTONIMAGE
        self.btnPhoto = [UIButton newAutoLayoutView];
        
        //DATE
        self.lblDate = [UILabel newAutoLayoutView];
        self.lblDate.textColor = [Fav24Colors dateTimelime];
        self.lblDate.backgroundColor = [UIColor whiteColor];
        
        //ADD SUBVIEWS
        [self.contentView addSubview:self.lblName];
        [self.contentView addSubview:self.txvText];
        [self.contentView addSubview:self.imgPhoto];
        [self.contentView addSubview:self.btnPhoto];
        [self.contentView addSubview:self.lblDate];
        
        self.selectionStyle = UITableViewCellSelectionStyleNone;
    }
    
    return self;

}

//------------------------------------------------------------------------------
- (void)configureBasicCellWithShot:(Shot *)shot andRow:(NSInteger)row {

    NSMutableAttributedString *attributedString = [TimeLineUtilities filterLinkWithContent:shot.comment];
    NSMutableParagraphStyle *paragraphStyle = [[NSMutableParagraphStyle alloc] init];
    paragraphStyle.lineSpacing = 2.8;
    NSDictionary *dict = @{NSParagraphStyleAttributeName : paragraphStyle, NSFontAttributeName: [UIFont systemFontOfSize:15] };
    [attributedString addAttributes:dict range:NSMakeRange(0, [[shot.comment cleanStringfromLinks:shot.comment] length])];
    
    self.txvText.attributedText = [TimeLineUtilities filterLinkWithContent:shot.comment];
    
    self.lblName.text = shot.user.userName;
    
    self.imgPhoto = [DownloadImage downloadImageForTimeLineWithUrl:[NSURL URLWithString:shot.user.photo] andUIimageView:self.imgPhoto andText:[shot.user.name substringToIndex:1]];
    
    self.lblDate.text = [TimeLineUtilities getDateShot:shot.csys_birth];
    self.btnPhoto.tag = row;
}

//------------------------------------------------------------------------------
- (void)addTarget:(id)target action:(SEL)action {
    
    [self.btnPhoto addTarget:target action:action forControlEvents:UIControlEventTouchUpInside];
}

//------------------------------------------------------------------------------
- (void)updateConstraints {
    
    if (!self.didSetupConstraints) {

        //NAME
        [self.lblName autoPinEdgeToSuperviewEdge:ALEdgeTop withInset:kLabelVerticalInsets];
        [self.lblName autoPinEdgeToSuperviewEdge:ALEdgeLeading withInset:kLabelHorizontalLeftInsets];
        [self.lblName autoPinEdgeToSuperviewEdge:ALEdgeTrailing withInset:kLabelHorizontalRightInsets relation:NSLayoutRelationGreaterThanOrEqual];
        
        //COMMENT
        [self.txvText autoPinEdgeToSuperviewEdge:ALEdgeLeading withInset:kLabelHorizontalLeftInsets];
        [self.txvText autoPinEdgeToSuperviewEdge:ALEdgeTrailing withInset:kLabelHorizontalRightInsets];
        [self.txvText autoPinEdge:ALEdgeTop toEdge:ALEdgeBottom ofView:self.lblName withOffset:kLabelVerticalCommentToName];
        [self.txvText autoPinEdgeToSuperviewEdge:ALEdgeBottom withInset:kLabelVerticalBottomComment relation:NSLayoutRelationGreaterThanOrEqual];
        
        //PHOTO
        [self.imgPhoto autoPinEdgeToSuperviewEdge:ALEdgeTop withInset:kLabelVerticalPhotoInsets];
        [self.imgPhoto autoPinEdgeToSuperviewEdge:ALEdgeLeading withInset:kLabelHorizontalPhotoInsets];
        [self.imgPhoto autoSetDimensionsToSize:CGSizeMake(48, 48)];
        
        //BUTTON
        [self.btnPhoto autoPinEdgeToSuperviewEdge:ALEdgeTop];
        [self.btnPhoto autoPinEdgeToSuperviewEdge:ALEdgeLeading];
        [self.btnPhoto autoSetDimensionsToSize:CGSizeMake(70, 60)];

        //DATE
        [self.lblDate autoPinEdgeToSuperviewEdge:ALEdgeTop withInset:kLabelVerticalInsets];
        [self.lblDate autoPinEdgeToSuperviewEdge:ALEdgeTrailing withInset:kLabelHorizontalRightInsets];
        
        self.didSetupConstraints = YES;
    }
    
    [super updateConstraints];
}

//------------------------------------------------------------------------------
- (void)layoutSubviews {
    
    [super layoutSubviews];
    
    // Make sure the contentView does a layout pass here so that its subviews have their frames set, which we
    // need to use to set the preferredMaxLayoutWidth below.
    [self.contentView setNeedsLayout];
    [self.contentView layoutIfNeeded];
    
    // Set the preferredMaxLayoutWidth of the mutli-line bodyLabel based on the evaluated width of the label's frame,
    // as this will allow the text to wrap correctly, and as a result allow the label to take on the correct height.

   self.txvText.preferredMaxLayoutWidth = CGRectGetWidth(self.txvText.frame);

}

@end
