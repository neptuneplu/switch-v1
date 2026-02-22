package me.card.switchv1.api.visa;

public class TagValue {

  private String tag;
  private String value;

  public TagValue() {
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "TagValue{" +
        "tag=" + tag +
        ", value='" + value + '\'' +
        '}';
  }
}
