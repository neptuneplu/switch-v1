package me.card.switchv1.api.visa;

import java.util.Objects;
import me.card.switchv1.component.CorrelationId;

public class VisaCorrelationId implements CorrelationId {
  String F2;
  String F11;
  String F32;
  String F37;
  String F41;
  String F42;

  public String getF2() {
    return F2;
  }

  public void setF2(String f2) {
    F2 = f2;
  }

  public String getF11() {
    return F11;
  }

  public void setF11(String f11) {
    F11 = f11;
  }

  public String getF32() {
    return F32;
  }

  public void setF32(String f32) {
    F32 = f32;
  }

  public String getF37() {
    return F37;
  }

  public void setF37(String f37) {
    F37 = f37;
  }

  public String getF41() {
    return F41;
  }

  public void setF41(String f41) {
    F41 = f41;
  }

  public String getF42() {
    return F42;
  }

  public void setF42(String f42) {
    F42 = f42;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    VisaCorrelationId that = (VisaCorrelationId) o;
    return Objects.equals(F2, that.F2) && Objects.equals(F11, that.F11) &&
        Objects.equals(F32, that.F32) && Objects.equals(F37, that.F37) &&
        Objects.equals(F41, that.F41) && Objects.equals(F42, that.F42);
  }

  @Override
  public int hashCode() {
    return Objects.hash(F2, F11, F32, F37, F41, F42);
  }
}
