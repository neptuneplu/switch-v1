package me.card.switchv1.api.cup;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import java.util.Arrays;
import me.card.switchv1.component.Api;
import me.card.switchv1.component.CorrelationId;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE)
public class CupApi implements Api, Serializable {

  private byte[] header;
  private byte[] destinationId;
  private byte[] sourceId;
  private String MTI;
  private String F2;
  private String F3;
  private String F4;
  private String F6;
  private String F7;
  private String F9;
  private String F10;
  private String F11;
  private String F12;
  private String F13;
  private String F14;
  private String F15;
  private String F16;
  private String F18;
  private String F19;
  private String F22;
  private String F23;
  private String F25;
  private String F26;
  private String F28;
  private String F32;
  private String F33;
  private String F35;
  private String F36;
  private String F37;
  private String F38;
  private String F39;
  private String F41;
  private String F42;
  private String F43;
  private String F44;
  private String F45;
  private String F48;
  private String F49;
  private String F50;
  private String F51;
  private String F52;
  private String F53;
  private String F54;
  private String F55;
  private String F56;
  private String F57;
  private String F59;
  private String F60;
  private String F61;
  private String F62;
  private String F63;
  private String F70;
  private String F90;
  private String F96;
  private String F100;
  private String F102;
  private String F103;
  private String F113;
  private String F116;
  private String F117;
  private String F121;
  private String F122;
  private String F123;
  private String F125;
  private String F126;
  private String F128;

  @Override
  public void toResponse(String code) {

  }

  @Override
  public String mti() {
    return this.MTI;
  }

  @Override
  public CorrelationId correlationId() {
    return null;
  }

  public byte[] getHeader() {
    return header;
  }

  public void setHeader(byte[] header) {
    this.header = header;
  }

  public byte[] getDestinationId() {
    return destinationId;
  }

  public void setDestinationId(byte[] destinationId) {
    this.destinationId = destinationId;
  }

  public byte[] getSourceId() {
    return sourceId;
  }

  public void setSourceId(byte[] sourceId) {
    this.sourceId = sourceId;
  }

  public String getMTI() {
    return MTI;
  }

  public CupApi setMTI(String MTI) {
    this.MTI = MTI;
    return this;
  }

  public String getF2() {
    return F2;
  }

  public CupApi setF2(String f2) {
    F2 = f2;
    return this;
  }

  public String getF3() {
    return F3;
  }

  public CupApi setF3(String f3) {
    F3 = f3;
    return this;
  }

  public String getF4() {
    return F4;
  }

  public CupApi setF4(String f4) {
    F4 = f4;
    return this;
  }

  public String getF6() {
    return F6;
  }

  public CupApi setF6(String f6) {
    F6 = f6;
    return this;
  }

  public String getF7() {
    return F7;
  }

  public CupApi setF7(String f7) {
    F7 = f7;
    return this;
  }

  public String getF9() {
    return F9;
  }

  public CupApi setF9(String f9) {
    F9 = f9;
    return this;
  }

  public String getF10() {
    return F10;
  }

  public CupApi setF10(String f10) {
    F10 = f10;
    return this;
  }

  public String getF11() {
    return F11;
  }

  public CupApi setF11(String f11) {
    F11 = f11;
    return this;
  }

  public String getF12() {
    return F12;
  }

  public CupApi setF12(String f12) {
    F12 = f12;
    return this;
  }

  public String getF13() {
    return F13;
  }

  public CupApi setF13(String f13) {
    F13 = f13;
    return this;
  }

  public String getF14() {
    return F14;
  }

  public CupApi setF14(String f14) {
    F14 = f14;
    return this;
  }

  public String getF15() {
    return F15;
  }

  public CupApi setF15(String f15) {
    F15 = f15;
    return this;
  }

  public String getF16() {
    return F16;
  }

  public CupApi setF16(String f16) {
    F16 = f16;
    return this;
  }

  public String getF18() {
    return F18;
  }

  public CupApi setF18(String f18) {
    F18 = f18;
    return this;
  }

  public String getF19() {
    return F19;
  }

  public CupApi setF19(String f19) {
    F19 = f19;
    return this;
  }

  public String getF22() {
    return F22;
  }

  public CupApi setF22(String f22) {
    F22 = f22;
    return this;
  }

  public String getF23() {
    return F23;
  }

  public CupApi setF23(String f23) {
    F23 = f23;
    return this;
  }

  public String getF25() {
    return F25;
  }

  public CupApi setF25(String f25) {
    F25 = f25;
    return this;
  }

  public String getF26() {
    return F26;
  }

  public CupApi setF26(String f26) {
    F26 = f26;
    return this;
  }

  public String getF28() {
    return F28;
  }

  public CupApi setF28(String f28) {
    F28 = f28;
    return this;
  }

  public String getF32() {
    return F32;
  }

  public CupApi setF32(String f32) {
    F32 = f32;
    return this;
  }

  public String getF33() {
    return F33;
  }

  public CupApi setF33(String f33) {
    F33 = f33;
    return this;
  }

  public String getF35() {
    return F35;
  }

  public CupApi setF35(String f35) {
    F35 = f35;
    return this;
  }

  public String getF36() {
    return F36;
  }

  public CupApi setF36(String f36) {
    F36 = f36;
    return this;
  }

  public String getF37() {
    return F37;
  }

  public CupApi setF37(String f37) {
    F37 = f37;
    return this;
  }

  public String getF38() {
    return F38;
  }

  public CupApi setF38(String f38) {
    F38 = f38;
    return this;
  }

  public String getF39() {
    return F39;
  }

  public CupApi setF39(String f39) {
    F39 = f39;
    return this;
  }

  public String getF41() {
    return F41;
  }

  public CupApi setF41(String f41) {
    F41 = f41;
    return this;
  }

  public String getF42() {
    return F42;
  }

  public CupApi setF42(String f42) {
    F42 = f42;
    return this;
  }

  public String getF43() {
    return F43;
  }

  public CupApi setF43(String f43) {
    F43 = f43;
    return this;
  }

  public String getF44() {
    return F44;
  }

  public CupApi setF44(String f44) {
    F44 = f44;
    return this;
  }

  public String getF45() {
    return F45;
  }

  public CupApi setF45(String f45) {
    F45 = f45;
    return this;
  }

  public String getF48() {
    return F48;
  }

  public CupApi setF48(String f48) {
    F48 = f48;
    return this;
  }

  public String getF49() {
    return F49;
  }

  public CupApi setF49(String f49) {
    F49 = f49;
    return this;
  }

  public String getF50() {
    return F50;
  }

  public CupApi setF50(String f50) {
    F50 = f50;
    return this;
  }

  public String getF51() {
    return F51;
  }

  public CupApi setF51(String f51) {
    F51 = f51;
    return this;
  }

  public String getF52() {
    return F52;
  }

  public CupApi setF52(String f52) {
    F52 = f52;
    return this;
  }

  public String getF53() {
    return F53;
  }

  public CupApi setF53(String f53) {
    F53 = f53;
    return this;
  }

  public String getF54() {
    return F54;
  }

  public CupApi setF54(String f54) {
    F54 = f54;
    return this;
  }

  public String getF55() {
    return F55;
  }

  public CupApi setF55(String f55) {
    F55 = f55;
    return this;
  }

  public String getF56() {
    return F56;
  }

  public CupApi setF56(String f56) {
    F56 = f56;
    return this;
  }

  public String getF57() {
    return F57;
  }

  public CupApi setF57(String f57) {
    F57 = f57;
    return this;
  }

  public String getF59() {
    return F59;
  }

  public CupApi setF59(String f59) {
    F59 = f59;
    return this;
  }

  public String getF60() {
    return F60;
  }

  public CupApi setF60(String f60) {
    F60 = f60;
    return this;
  }

  public String getF61() {
    return F61;
  }

  public CupApi setF61(String f61) {
    F61 = f61;
    return this;
  }

  public String getF62() {
    return F62;
  }

  public CupApi setF62(String f62) {
    F62 = f62;
    return this;
  }

  public String getF63() {
    return F63;
  }

  public CupApi setF63(String f63) {
    F63 = f63;
    return this;
  }

  public String getF70() {
    return F70;
  }

  public CupApi setF70(String f70) {
    F70 = f70;
    return this;
  }

  public String getF90() {
    return F90;
  }

  public CupApi setF90(String f90) {
    F90 = f90;
    return this;
  }

  public String getF96() {
    return F96;
  }

  public CupApi setF96(String f96) {
    F96 = f96;
    return this;
  }

  public String getF100() {
    return F100;
  }

  public CupApi setF100(String f100) {
    F100 = f100;
    return this;
  }

  public String getF102() {
    return F102;
  }

  public CupApi setF102(String f102) {
    F102 = f102;
    return this;
  }

  public String getF103() {
    return F103;
  }

  public CupApi setF103(String f103) {
    F103 = f103;
    return this;
  }

  public String getF113() {
    return F113;
  }

  public CupApi setF113(String f113) {
    F113 = f113;
    return this;
  }

  public String getF116() {
    return F116;
  }

  public CupApi setF116(String f116) {
    F116 = f116;
    return this;
  }

  public String getF117() {
    return F117;
  }

  public CupApi setF117(String f117) {
    F117 = f117;
    return this;
  }

  public String getF121() {
    return F121;
  }

  public CupApi setF121(String f121) {
    F121 = f121;
    return this;
  }

  public String getF122() {
    return F122;
  }

  public CupApi setF122(String f122) {
    F122 = f122;
    return this;
  }

  public String getF123() {
    return F123;
  }

  public CupApi setF123(String f123) {
    F123 = f123;
    return this;
  }

  public String getF125() {
    return F125;
  }

  public CupApi setF125(String f125) {
    F125 = f125;
    return this;
  }

  public String getF126() {
    return F126;
  }

  public CupApi setF126(String f126) {
    F126 = f126;
    return this;
  }

  public String getF128() {
    return F128;
  }

  public CupApi setF128(String f128) {
    F128 = f128;
    return this;
  }


  @Override
  public String toString() {
    return "CupApi{" +
        "header=" + Arrays.toString(header) +
        ", destinationId=" + Arrays.toString(destinationId) +
        ", sourceId=" + Arrays.toString(sourceId) +
        ", MTI='" + MTI + '\'' +
        ", F2='" + F2 + '\'' +
        ", F3='" + F3 + '\'' +
        ", F4='" + F4 + '\'' +
        ", F6='" + F6 + '\'' +
        ", F7='" + F7 + '\'' +
        ", F9='" + F9 + '\'' +
        ", F10='" + F10 + '\'' +
        ", F11='" + F11 + '\'' +
        ", F12='" + F12 + '\'' +
        ", F13='" + F13 + '\'' +
        ", F14='" + F14 + '\'' +
        ", F15='" + F15 + '\'' +
        ", F16='" + F16 + '\'' +
        ", F18='" + F18 + '\'' +
        ", F19='" + F19 + '\'' +
        ", F22='" + F22 + '\'' +
        ", F23='" + F23 + '\'' +
        ", F25='" + F25 + '\'' +
        ", F26='" + F26 + '\'' +
        ", F28='" + F28 + '\'' +
        ", F32='" + F32 + '\'' +
        ", F33='" + F33 + '\'' +
        ", F35='" + F35 + '\'' +
        ", F36='" + F36 + '\'' +
        ", F37='" + F37 + '\'' +
        ", F38='" + F38 + '\'' +
        ", F39='" + F39 + '\'' +
        ", F41='" + F41 + '\'' +
        ", F42='" + F42 + '\'' +
        ", F43='" + F43 + '\'' +
        ", F44='" + F44 + '\'' +
        ", F45='" + F45 + '\'' +
        ", F48='" + F48 + '\'' +
        ", F49='" + F49 + '\'' +
        ", F50='" + F50 + '\'' +
        ", F51='" + F51 + '\'' +
        ", F52='" + F52 + '\'' +
        ", F53='" + F53 + '\'' +
        ", F54='" + F54 + '\'' +
        ", F55='" + F55 + '\'' +
        ", F56='" + F56 + '\'' +
        ", F57='" + F57 + '\'' +
        ", F59='" + F59 + '\'' +
        ", F60='" + F60 + '\'' +
        ", F61='" + F61 + '\'' +
        ", F62='" + F62 + '\'' +
        ", F63='" + F63 + '\'' +
        ", F70='" + F70 + '\'' +
        ", F90='" + F90 + '\'' +
        ", F96='" + F96 + '\'' +
        ", F100='" + F100 + '\'' +
        ", F102='" + F102 + '\'' +
        ", F103='" + F103 + '\'' +
        ", F113='" + F113 + '\'' +
        ", F116='" + F116 + '\'' +
        ", F117='" + F117 + '\'' +
        ", F121='" + F121 + '\'' +
        ", F122='" + F122 + '\'' +
        ", F123='" + F123 + '\'' +
        ", F125='" + F125 + '\'' +
        ", F126='" + F126 + '\'' +
        ", F128='" + F128 + '\'' +
        '}';
  }
}
