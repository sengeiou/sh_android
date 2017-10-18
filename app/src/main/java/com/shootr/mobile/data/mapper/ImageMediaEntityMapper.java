package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.ImageMediaEntity;
import com.shootr.mobile.data.entity.ImageSizeEntity;
import com.shootr.mobile.data.entity.SizeEntity;
import com.shootr.mobile.domain.model.ImageMedia;
import com.shootr.mobile.domain.model.ImageSize;
import com.shootr.mobile.domain.model.Sizes;
import javax.inject.Inject;

public class ImageMediaEntityMapper {

  @Inject public ImageMediaEntityMapper() {
  }

  public ImageMedia transform(ImageMediaEntity imageMediaEntity) {

    ImageMedia imageMedia = new ImageMedia();
    ImageSize imageSize = new ImageSize();

    Sizes lowSize = new Sizes();
    lowSize.setHeight(imageMediaEntity.getSizes().getLow().getHeight());
    lowSize.setWidth(imageMediaEntity.getSizes().getLow().getWidth());
    lowSize.setUrl(imageMediaEntity.getSizes().getLow().getUrl());
    imageSize.setLow(lowSize);

    Sizes mediumSize = new Sizes();
    mediumSize.setHeight(imageMediaEntity.getSizes().getMedium().getHeight());
    mediumSize.setWidth(imageMediaEntity.getSizes().getMedium().getWidth());
    mediumSize.setUrl(imageMediaEntity.getSizes().getMedium().getUrl());
    imageSize.setMedium(mediumSize);

    Sizes highSize = new Sizes();
    highSize.setHeight(imageMediaEntity.getSizes().getHigh().getHeight());
    highSize.setWidth(imageMediaEntity.getSizes().getHigh().getWidth());
    highSize.setUrl(imageMediaEntity.getSizes().getHigh().getUrl());
    imageSize.setHigh(highSize);

    imageMedia.setType(imageMediaEntity.getType());
    imageMedia.setSizes(imageSize);

    return imageMedia;
  }

  public ImageMediaEntity transform(ImageMedia imageMedia) {
    ImageMediaEntity imageMediaEntity = new ImageMediaEntity();
    ImageSizeEntity imageSizeEntity = new ImageSizeEntity();

    SizeEntity lowSize = new SizeEntity();
    lowSize.setHeight(imageMedia.getSizes().getLow().getHeight());
    lowSize.setWidth(imageMedia.getSizes().getLow().getWidth());
    lowSize.setUrl(imageMedia.getSizes().getLow().getUrl());
    imageSizeEntity.setLow(lowSize);

    SizeEntity mediumSize = new SizeEntity();
    mediumSize.setHeight(imageMedia.getSizes().getMedium().getHeight());
    mediumSize.setWidth(imageMedia.getSizes().getMedium().getWidth());
    mediumSize.setUrl(imageMedia.getSizes().getMedium().getUrl());
    imageSizeEntity.setMedium(mediumSize);

    SizeEntity highSize = new SizeEntity();
    highSize.setHeight(imageMedia.getSizes().getHigh().getHeight());
    highSize.setWidth(imageMedia.getSizes().getHigh().getWidth());
    highSize.setUrl(imageMedia.getSizes().getHigh().getUrl());
    imageSizeEntity.setHigh(highSize);

    imageMediaEntity.setType(imageMedia.getType());
    imageMediaEntity.setSizes(imageSizeEntity);

    return imageMediaEntity;
  }
}
