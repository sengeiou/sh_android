package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.ImageMedia;
import com.shootr.mobile.ui.model.ImageMediaModel;
import com.shootr.mobile.ui.model.ImageSizeModel;
import com.shootr.mobile.ui.model.SizesModel;
import javax.inject.Inject;

public class ImageMediaModelMapper {

  @Inject public ImageMediaModelMapper() {
  }

  public ImageMediaModel transform(ImageMedia imageMedia) {
    ImageMediaModel imageMediaModel = new ImageMediaModel();
    ImageSizeModel imageSize = new ImageSizeModel();

    SizesModel lowSize = new SizesModel();
    lowSize.setHeight(imageMedia.getSizes().getLow().getHeight());
    lowSize.setWidth(imageMedia.getSizes().getLow().getWidth());
    lowSize.setUrl(imageMedia.getSizes().getLow().getUrl());
    imageSize.setLow(lowSize);

    SizesModel mediumSize = new SizesModel();
    mediumSize.setHeight(imageMedia.getSizes().getMedium().getHeight());
    mediumSize.setWidth(imageMedia.getSizes().getMedium().getWidth());
    mediumSize.setUrl(imageMedia.getSizes().getMedium().getUrl());
    imageSize.setMedium(mediumSize);

    SizesModel highSize = new SizesModel();
    highSize.setHeight(imageMedia.getSizes().getHigh().getHeight());
    highSize.setWidth(imageMedia.getSizes().getHigh().getWidth());
    highSize.setUrl(imageMedia.getSizes().getHigh().getUrl());
    imageSize.setHigh(highSize);

    imageMediaModel.setType(imageMedia.getType());
    imageMediaModel.setSizes(imageSize);

    return imageMediaModel;
  }
}
