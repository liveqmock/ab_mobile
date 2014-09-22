package com.feihuwang.domain;

public class Demo {

  private String id;
  private String displayName;

  public Demo() {
  }

  public Demo(String id, String displayName) {
    this.id = id;
    this.displayName = displayName;
  }

  public String getId() {
    return id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

}
