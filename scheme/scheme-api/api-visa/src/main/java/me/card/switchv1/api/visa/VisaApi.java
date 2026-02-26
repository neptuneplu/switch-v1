package me.card.switchv1.api.visa;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;
import me.card.switchv1.component.Api;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE)
public class VisaApi implements Api, Serializable {

  private String seqNo;
  private String destinationId;
  private String sourceId;
  private String MTI;
  private String F2;
  private F3 F3;
  private String F4;
  private String F6;
  private String F7;
  private String F10;
  private String F11;
  private String F12;
  private String F13;
  private String F14;
  private String F15;
  private String F18;
  private String F19;
  private String F20;
  private String F22;
  private String F23;
  private String F25;
  private String F26;
  private String F28;
  private String F32;
  private String F33;
  private String F35;
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
  private String F51;
  private String F52;
  private String F53;
  private String F54;
  private F55 F55;
  private String F59;
  private String F60;
  private String F61;
  private String F62;
  private String F63;
  private String F68;
  private String F70;
  private String F73;
  private String F90;
  private String F91;
  private String F92;
  private String F94;
  private String F100;
  private String F101;
  private String F102;
  private String F103;
  private String F104;
  private String F115;
  private String F116;
  private String F117;
  private String F108;
  private String F121;
  private String F123;
  private String F125;
  private String F126;
  private String F130;
  private String F131;
  private String F132;
  private String F133;
  private String F134;
  private String F135;
  private String F136;
  private String F137;
  private String F138;
  private String F139;
  private String F140;
  private String F142;
  private String F143;
  private String F144;
  private String F145;
  private String F146;
  private String F147;
  private String F148;
  private String F149;
  private String F150;

  @Override
  public void toResponse(String code) {
    this.MTI = me.card.switchv1.api.visa.MTI.mapResponseMTI(this.MTI);
    String id = this.destinationId;
    this.destinationId = this.sourceId;
    this.sourceId = id;
    this.F39 = code;
  }

  @Override
  public String mti() {
    return this.MTI;
  }

  public VisaCorrelationId correlationId() {
    VisaCorrelationId correlationId = new VisaCorrelationId();
    correlationId.setF2(this.F2);
    correlationId.setF11(this.F11);
    correlationId.setF32(this.F32);
    correlationId.setF37(this.F37);
    correlationId.setF41(this.F41);
    correlationId.setF42(this.F42);

    return correlationId;
  }


  public String getSeqNo() {
    return seqNo;
  }

  public void setSeqNo(String seqNo) {
    this.seqNo = seqNo;
  }

  public String getDestinationId() {
    return destinationId;
  }

  public void setDestinationId(String destinationId) {
    this.destinationId = destinationId;
  }

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getMTI() {
    return MTI;
  }

  public void setMTI(String MTI) {
    this.MTI = MTI;
  }

  public String getF2() {
    return F2;
  }

  public void setF2(String f2) {
    F2 = f2;
  }

  public F3 getF3() {
    return F3;
  }

  public void setF3(F3 f3) {
    F3 = f3;
  }

  public String getF4() {
    return F4;
  }

  public void setF4(String f4) {
    F4 = f4;
  }

  public String getF6() {
    return F6;
  }

  public void setF6(String f6) {
    F6 = f6;
  }

  public String getF7() {
    return F7;
  }

  public void setF7(String f7) {
    F7 = f7;
  }

  public String getF10() {
    return F10;
  }

  public void setF10(String f10) {
    F10 = f10;
  }

  public String getF11() {
    return F11;
  }

  public void setF11(String f11) {
    F11 = f11;
  }

  public String getF12() {
    return F12;
  }

  public void setF12(String f12) {
    F12 = f12;
  }

  public String getF13() {
    return F13;
  }

  public void setF13(String f13) {
    F13 = f13;
  }

  public String getF14() {
    return F14;
  }

  public void setF14(String f14) {
    F14 = f14;
  }

  public String getF15() {
    return F15;
  }

  public void setF15(String f15) {
    F15 = f15;
  }

  public String getF18() {
    return F18;
  }

  public void setF18(String f18) {
    F18 = f18;
  }

  public String getF19() {
    return F19;
  }

  public void setF19(String f19) {
    F19 = f19;
  }

  public String getF20() {
    return F20;
  }

  public void setF20(String f20) {
    F20 = f20;
  }

  public String getF22() {
    return F22;
  }

  public void setF22(String f22) {
    F22 = f22;
  }

  public String getF23() {
    return F23;
  }

  public void setF23(String f23) {
    F23 = f23;
  }

  public String getF25() {
    return F25;
  }

  public void setF25(String f25) {
    F25 = f25;
  }

  public String getF26() {
    return F26;
  }

  public void setF26(String f26) {
    F26 = f26;
  }

  public String getF28() {
    return F28;
  }

  public void setF28(String f28) {
    F28 = f28;
  }

  public String getF32() {
    return F32;
  }

  public void setF32(String f32) {
    F32 = f32;
  }

  public String getF33() {
    return F33;
  }

  public void setF33(String f33) {
    F33 = f33;
  }

  public String getF35() {
    return F35;
  }

  public void setF35(String f35) {
    F35 = f35;
  }

  public String getF37() {
    return F37;
  }

  public void setF37(String f37) {
    F37 = f37;
  }

  public String getF38() {
    return F38;
  }

  public void setF38(String f38) {
    F38 = f38;
  }

  public String getF39() {
    return F39;
  }

  public void setF39(String f39) {
    F39 = f39;
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

  public String getF43() {
    return F43;
  }

  public void setF43(String f43) {
    F43 = f43;
  }

  public String getF44() {
    return F44;
  }

  public void setF44(String f44) {
    F44 = f44;
  }

  public String getF45() {
    return F45;
  }

  public void setF45(String f45) {
    F45 = f45;
  }

  public String getF48() {
    return F48;
  }

  public void setF48(String f48) {
    F48 = f48;
  }

  public String getF49() {
    return F49;
  }

  public void setF49(String f49) {
    F49 = f49;
  }

  public String getF51() {
    return F51;
  }

  public void setF51(String f51) {
    F51 = f51;
  }

  public String getF52() {
    return F52;
  }

  public void setF52(String f52) {
    F52 = f52;
  }

  public String getF53() {
    return F53;
  }

  public void setF53(String f53) {
    F53 = f53;
  }

  public String getF54() {
    return F54;
  }

  public void setF54(String f54) {
    F54 = f54;
  }

  public F55 getF55() {
    return F55;
  }

  public void setF55(F55 f55) {
    F55 = f55;
  }

  public String getF59() {
    return F59;
  }

  public void setF59(String f59) {
    F59 = f59;
  }

  public String getF60() {
    return F60;
  }

  public void setF60(String f60) {
    F60 = f60;
  }

  public String getF61() {
    return F61;
  }

  public void setF61(String f61) {
    F61 = f61;
  }

  public String getF62() {
    return F62;
  }

  public void setF62(String f62) {
    F62 = f62;
  }

  public String getF63() {
    return F63;
  }

  public void setF63(String f63) {
    F63 = f63;
  }

  public String getF68() {
    return F68;
  }

  public void setF68(String f68) {
    F68 = f68;
  }

  public String getF70() {
    return F70;
  }

  public void setF70(String f70) {
    F70 = f70;
  }

  public String getF73() {
    return F73;
  }

  public void setF73(String f73) {
    F73 = f73;
  }

  public String getF90() {
    return F90;
  }

  public void setF90(String f90) {
    F90 = f90;
  }

  public String getF91() {
    return F91;
  }

  public void setF91(String f91) {
    F91 = f91;
  }

  public String getF92() {
    return F92;
  }

  public void setF92(String f92) {
    F92 = f92;
  }

  public String getF94() {
    return F94;
  }

  public void setF94(String f94) {
    F94 = f94;
  }

  public String getF100() {
    return F100;
  }

  public void setF100(String f100) {
    F100 = f100;
  }

  public String getF101() {
    return F101;
  }

  public void setF101(String f101) {
    F101 = f101;
  }

  public String getF102() {
    return F102;
  }

  public void setF102(String f102) {
    F102 = f102;
  }

  public String getF103() {
    return F103;
  }

  public void setF103(String f103) {
    F103 = f103;
  }

  public String getF104() {
    return F104;
  }

  public void setF104(String f104) {
    F104 = f104;
  }

  public String getF115() {
    return F115;
  }

  public void setF115(String f115) {
    F115 = f115;
  }

  public String getF116() {
    return F116;
  }

  public void setF116(String f116) {
    F116 = f116;
  }

  public String getF117() {
    return F117;
  }

  public void setF117(String f117) {
    F117 = f117;
  }

  public String getF108() {
    return F108;
  }

  public void setF108(String f108) {
    F108 = f108;
  }

  public String getF121() {
    return F121;
  }

  public void setF121(String f121) {
    F121 = f121;
  }

  public String getF123() {
    return F123;
  }

  public void setF123(String f123) {
    F123 = f123;
  }

  public String getF125() {
    return F125;
  }

  public void setF125(String f125) {
    F125 = f125;
  }

  public String getF126() {
    return F126;
  }

  public void setF126(String f126) {
    F126 = f126;
  }

  public String getF130() {
    return F130;
  }

  public void setF130(String f130) {
    F130 = f130;
  }

  public String getF131() {
    return F131;
  }

  public void setF131(String f131) {
    F131 = f131;
  }

  public String getF132() {
    return F132;
  }

  public void setF132(String f132) {
    F132 = f132;
  }

  public String getF133() {
    return F133;
  }

  public void setF133(String f133) {
    F133 = f133;
  }

  public String getF134() {
    return F134;
  }

  public void setF134(String f134) {
    F134 = f134;
  }

  public String getF135() {
    return F135;
  }

  public void setF135(String f135) {
    F135 = f135;
  }

  public String getF136() {
    return F136;
  }

  public void setF136(String f136) {
    F136 = f136;
  }

  public String getF137() {
    return F137;
  }

  public void setF137(String f137) {
    F137 = f137;
  }

  public String getF138() {
    return F138;
  }

  public void setF138(String f138) {
    F138 = f138;
  }

  public String getF139() {
    return F139;
  }

  public void setF139(String f139) {
    F139 = f139;
  }

  public String getF140() {
    return F140;
  }

  public void setF140(String f140) {
    F140 = f140;
  }

  public String getF142() {
    return F142;
  }

  public void setF142(String f142) {
    F142 = f142;
  }

  public String getF143() {
    return F143;
  }

  public void setF143(String f143) {
    F143 = f143;
  }

  public String getF144() {
    return F144;
  }

  public void setF144(String f144) {
    F144 = f144;
  }

  public String getF145() {
    return F145;
  }

  public void setF145(String f145) {
    F145 = f145;
  }

  public String getF146() {
    return F146;
  }

  public void setF146(String f146) {
    F146 = f146;
  }

  public String getF147() {
    return F147;
  }

  public void setF147(String f147) {
    F147 = f147;
  }

  public String getF148() {
    return F148;
  }

  public void setF148(String f148) {
    F148 = f148;
  }

  public String getF149() {
    return F149;
  }

  public void setF149(String f149) {
    F149 = f149;
  }

  public String getF150() {
    return F150;
  }

  public void setF150(String f150) {
    F150 = f150;
  }


  @Override
  public String toString() {
    return "VisaApi{" +
        "seqNo='" + seqNo + '\'' +
        ", destinationId='" + destinationId + '\'' +
        ", sourceId='" + sourceId + '\'' +
        ", MTI='" + MTI + '\'' +
        ", F2='" + F2 + '\'' +
        ", F3=" + F3 +
        ", F4='" + F4 + '\'' +
        ", F6='" + F6 + '\'' +
        ", F7='" + F7 + '\'' +
        ", F10='" + F10 + '\'' +
        ", F11='" + F11 + '\'' +
        ", F12='" + F12 + '\'' +
        ", F13='" + F13 + '\'' +
        ", F14='" + F14 + '\'' +
        ", F15='" + F15 + '\'' +
        ", F18='" + F18 + '\'' +
        ", F19='" + F19 + '\'' +
        ", F20='" + F20 + '\'' +
        ", F22='" + F22 + '\'' +
        ", F23='" + F23 + '\'' +
        ", F25='" + F25 + '\'' +
        ", F26='" + F26 + '\'' +
        ", F28='" + F28 + '\'' +
        ", F32='" + F32 + '\'' +
        ", F33='" + F33 + '\'' +
        ", F35='" + F35 + '\'' +
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
        ", F51='" + F51 + '\'' +
        ", F52='" + F52 + '\'' +
        ", F53='" + F53 + '\'' +
        ", F54='" + F54 + '\'' +
        ", F55=" + F55 +
        ", F59='" + F59 + '\'' +
        ", F60='" + F60 + '\'' +
        ", F61='" + F61 + '\'' +
        ", F62='" + F62 + '\'' +
        ", F63='" + F63 + '\'' +
        ", F68='" + F68 + '\'' +
        ", F70='" + F70 + '\'' +
        ", F73='" + F73 + '\'' +
        ", F90='" + F90 + '\'' +
        ", F91='" + F91 + '\'' +
        ", F92='" + F92 + '\'' +
        ", F94='" + F94 + '\'' +
        ", F100='" + F100 + '\'' +
        ", F101='" + F101 + '\'' +
        ", F102='" + F102 + '\'' +
        ", F103='" + F103 + '\'' +
        ", F104='" + F104 + '\'' +
        ", F115='" + F115 + '\'' +
        ", F116='" + F116 + '\'' +
        ", F117='" + F117 + '\'' +
        ", F108='" + F108 + '\'' +
        ", F121='" + F121 + '\'' +
        ", F123='" + F123 + '\'' +
        ", F125='" + F125 + '\'' +
        ", F126='" + F126 + '\'' +
        ", F130='" + F130 + '\'' +
        ", F131='" + F131 + '\'' +
        ", F132='" + F132 + '\'' +
        ", F133='" + F133 + '\'' +
        ", F134='" + F134 + '\'' +
        ", F135='" + F135 + '\'' +
        ", F136='" + F136 + '\'' +
        ", F137='" + F137 + '\'' +
        ", F138='" + F138 + '\'' +
        ", F139='" + F139 + '\'' +
        ", F140='" + F140 + '\'' +
        ", F142='" + F142 + '\'' +
        ", F143='" + F143 + '\'' +
        ", F144='" + F144 + '\'' +
        ", F145='" + F145 + '\'' +
        ", F146='" + F146 + '\'' +
        ", F147='" + F147 + '\'' +
        ", F148='" + F148 + '\'' +
        ", F149='" + F149 + '\'' +
        ", F150='" + F150 + '\'' +
        '}';
  }
}
