package me.card.switchv1.visaapi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class F55 implements Serializable {

  private List<TagValue> tagValues = new ArrayList<>();

  public void add(TagValue tagValue) {
    tagValues.add(tagValue);
  }

  public List<TagValue> getTagValues() {
    return tagValues;
  }

  public void setTagValues(List<TagValue> tagValues) {
    this.tagValues = tagValues;
  }

  @Override
  public String toString() {
    return "F55{" +
        "tagValues=" + tagValues +
        '}';
  }


}
