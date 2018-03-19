package com.shootr.mobile.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;

public class YoutubeVideoUtils implements ExternalVideoUtils {

  private final String PATTERN = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

  @Inject public YoutubeVideoUtils() {
  }

  @Override public String getVideoId(String url) {
    Pattern compiledPattern = Pattern.compile(PATTERN);
    Matcher matcher = compiledPattern.matcher(url);
    if(matcher.find()){
      return matcher.group();
    }
    return null;
  }
}
