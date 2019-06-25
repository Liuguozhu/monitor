package com.coder.monitor.common;

public class Result {

  private Integer code;
  private String description;
  private Object detail;

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Object getDetail() {
    return detail;
  }

  public void setDetail(Object detail) {
    this.detail = detail;
  }

  @Override
  public String toString() {
    return "Result{" +
            "code=" + code +
            ", description='" + description + '\'' +
            ", detail=" + detail +
            '}';
  }
}
