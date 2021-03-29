package com.imooc.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix ="fiel")
@PropertySource("classpath:file-upload-dev.properties")
public class FileUpload {
  private  String imageUserFaceLocation;

  public String getImageServerUrl() {
    return imageServerUrl;
  }

  public void setImageServerUrl(String imageServerUrl) {
    this.imageServerUrl = imageServerUrl;
  }

  private  String imageServerUrl;

  public String getImageUserFaceLocation() {
    return imageUserFaceLocation;
  }

  public void setImageUserFaceLocation(String imageUserFaceLocation) {
    this.imageUserFaceLocation = imageUserFaceLocation;
  }



}
