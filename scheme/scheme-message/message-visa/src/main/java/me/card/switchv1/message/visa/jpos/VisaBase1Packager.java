package me.card.switchv1.message.visa.jpos;

import me.card.switchv1.message.visa.VisaMessageException;
import org.jpos.iso.IFB_AMOUNT;
import org.jpos.iso.IFB_BINARY;
import org.jpos.iso.IFB_BITMAP;
import org.jpos.iso.IFB_LLCHAR;
import org.jpos.iso.IFB_LLHBINARY;
import org.jpos.iso.IFB_LLHECHAR;
import org.jpos.iso.IFB_LLHNUM;
import org.jpos.iso.IFB_LLLBINARY;
import org.jpos.iso.IFB_LLLCHAR;
import org.jpos.iso.IFB_LLLHBINARY;
import org.jpos.iso.IFB_LLNUM;
import org.jpos.iso.IFB_NUMERIC;
import org.jpos.iso.IFE_AMOUNT;
import org.jpos.iso.IFE_CHAR;
import org.jpos.iso.IFE_LLCHAR;
import org.jpos.iso.IF_CHAR;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOFieldPackager;
import org.jpos.iso.ISOMsgFieldPackager;
import org.jpos.tlv.packager.bertlv.BERTLVBinaryPackager;

public class VisaBase1Packager extends ISOBasePackager {

  private static final boolean PAD = false;

  protected static ISOFieldPackager[] field = {
      /*000*/ new IFB_NUMERIC(4, "MTI", PAD),
      /*001*/ new IFB_BITMAP(16, "BITMAP"),
      /*002*/ new IFB_LLHNUM(19, "F2", PAD),
//    /*003*/ new IFB_NUMERIC(6, "F3", PAD),
      /*003*/ new ISOMsgFieldPackager(new IFB_BINARY(3, "F3"), new F3Packager()),
      /*004*/ new IFB_NUMERIC(12, "F4", true),
      /*005*/ null,
      /*006*/ new IFB_NUMERIC(12, "F6", true),
      /*007*/ new IFB_NUMERIC(10, "F7", true),
      /*008*/ null,
      /*009*/ null,
      /*010*/ new IFB_NUMERIC(8, "F10", true),
      /*011*/ new IFB_NUMERIC(6, "F11", true),
      /*012*/ new IFB_NUMERIC(6, "F12", true),
      /*013*/ new IFB_NUMERIC(4, "F13", true),
      /*014*/ new IFB_NUMERIC(4, "F14", true),
      /*015*/ null,
      /*016*/ null,
      /*017*/ null,
      /*018*/ new IFB_NUMERIC(4, "F18", true),
      /*019*/ new IFB_NUMERIC(3, "F19", true),
      /*020*/ new IFB_NUMERIC(3, "F20", true),
      /*021*/ null,
      /*022*/ new IFB_NUMERIC(4, "F22", true),
      /*023*/ new IFB_NUMERIC(3, "F23", true),
      /*024*/ null,
      /*025*/ new IFB_NUMERIC(2, "F25", true),
      /*026*/ new IFB_NUMERIC(2, "F26", true),
      /*027*/ null,
      /*028*/ new IFE_AMOUNT(9, "F28"),
      /*029*/ null,
      /*030*/ null,
      /*031*/ null,
      /*032*/ new IFB_LLHNUM(11, "F32", true),
      /*033*/ new IFB_LLHNUM(11, "F33", true),
      /*034*/ null,
      /*035*/ new IFB_LLHNUM(37, "F35", true),
      /*036*/ null,
      /*037*/ new IFE_CHAR(12, "F37"),
      /*038*/ new IFE_CHAR(6, "F38"),
      /*039*/ new IFE_CHAR(2, "F39"),
      /*040*/ null,
      /*041*/ new IFE_CHAR(8, "F41"),
      /*042*/ new IFE_CHAR(15, "F42"),
      /*043*/ new IFE_CHAR(40, "F43"),
      /*044*/ new IFB_LLHECHAR(25, "F44"),
      /*045*/ new IFB_LLHECHAR(76, "F45"),
      /*046*/ null,
      /*047*/ null,
//      check F48
      /*048*/ new IFB_LLHECHAR(255, "F48"),
      /*049*/ new IFB_NUMERIC(3, "F49", true),
      /*050*/ null,
      /*051*/ new IFB_NUMERIC(3, "F51", true),
      /*052*/ new IFB_BINARY(8, "F52"),
      /*053*/ new IFB_NUMERIC(16, "F53", true),
      /*054*/ new IFB_LLHECHAR(120, "F54"),
//    /*055*/ new IFB_LLHBINARY(255, "F55"),
      /*055*/ new ISOMsgFieldPackager(new IFB_LLHBINARY(255, "F55"), new F55Packager()),
      /*056*/ null,
      /*057*/ null,
      /*058*/ null,
      /*059*/ new IFE_LLCHAR(14, "F59"),
      /*060*/ new IFB_LLHBINARY(12, "F60"),
      /*061*/ new IFB_LLLCHAR(999, "F61"),
      /*062*/ new IFB_LLHBINARY(255, "F62"),
      /*063*/ new IFB_LLHBINARY(99, "F63"),
      /*064*/ new IFB_BINARY(8, "F64"),
      /*065*/ new IFB_BINARY(8, "F65"),
      /*066*/ new IFB_LLLCHAR(204, "F66"),
      /*067*/ new IFB_NUMERIC(2, "F67", PAD),
      /*068*/ new IFB_NUMERIC(3, "F68", PAD),
      /*069*/ new IFB_NUMERIC(3, "F69", PAD),
      /*070*/ new IFB_NUMERIC(4, "F70", PAD),
      /*071*/ new IFB_NUMERIC(8, "F71", PAD),
      /*072*/ new IFB_LLLCHAR(999, "F72"),
      /*073*/ new IFB_NUMERIC(6, "F73", PAD),
      /*074*/ new IFB_NUMERIC(10, "F74", PAD),
      /*075*/ new IFB_NUMERIC(10, "F75", PAD),
      /*076*/ new IFB_NUMERIC(10, "F76", PAD),
      /*077*/ new IFB_NUMERIC(10, "F77", PAD),
      /*078*/ new IFB_NUMERIC(10, "F78", PAD),
      /*079*/ new IFB_NUMERIC(10, "F79", PAD),
      /*080*/ new IFB_NUMERIC(10, "F80", PAD),
      /*081*/ new IFB_NUMERIC(10, "F81", PAD),
      /*082*/ new IFB_NUMERIC(10, "F82", PAD),
      /*083*/ new IFB_NUMERIC(10, "F83", PAD),
      /*084*/ new IFB_NUMERIC(10, "F84", PAD),
      /*085*/ new IFB_NUMERIC(10, "F85", PAD),
      /*086*/ new IFB_NUMERIC(10, "F86", PAD),
      /*087*/ new IFB_NUMERIC(10, "F87", PAD),
      /*088*/ new IFB_NUMERIC(10, "F88", PAD),
      /*089*/ new IFB_NUMERIC(10, "F89", PAD),
      /*090*/ new IFB_NUMERIC(10, "F90", PAD),
      /*091*/ new IFB_NUMERIC(3, "F91", PAD),
      /*092*/ new IFB_NUMERIC(3, "F92", PAD),
      /*093*/ new IFB_LLNUM(11, "F93", PAD),
      /*094*/ new IFB_LLNUM(11, "F94", PAD),
      /*095*/ new IFB_LLCHAR(99, "F95"),
      /*096*/ new IFB_LLLBINARY(999, "F96"),
      /*097*/ new IFB_AMOUNT(17, "F97", PAD),
      /*098*/ new IF_CHAR(25, "F98"),
      /*099*/ new IFB_LLCHAR(11, "F99"),
      /*100*/ new IFB_LLNUM(11, "F100", PAD),
      /*101*/ new IFB_LLCHAR(17, "F101"),
      /*102*/ new IFB_LLCHAR(28, "F102"),
      /*103*/ new IFB_LLCHAR(28, "F103"),
//      check F104
      /*104*/ new IFB_LLHECHAR(255, "F104"),
      /*105*/ new IFB_NUMERIC(16, "F105", PAD),
      /*106*/ new IFB_NUMERIC(16, "F106", PAD),
      /*107*/ new IFB_NUMERIC(10, "F107", PAD),
      /*108*/ new IFB_NUMERIC(10, "F108", PAD),
      /*109*/ new IFB_LLCHAR(84, "F109"),
      /*110*/ new IFB_LLCHAR(84, "F110"),
      /*112*/ new IFB_LLLCHAR(999, "F112"),
      /*113*/ new IFB_LLLCHAR(999, "F113"),
      /*114*/ new IFB_LLLCHAR(999, "F114"),
      /*115*/ new IFB_LLLCHAR(999, "F115"),
      /*116*/ new IFB_LLLCHAR(999, "F116"),
      /*117*/ new IFB_LLLCHAR(999, "F117"),
      /*118*/ new IFB_LLLCHAR(999, "F118"),
      /*119*/ new IFB_LLLCHAR(999, "F119"),
      /*120*/ new IFB_LLLCHAR(999, "F120"),
      /*121*/ new IFB_LLLCHAR(999, "F121"),
      /*122*/ new IFB_LLLCHAR(999, "F122"),
      /*123*/ new IFB_LLLCHAR(999, "F123"),
      /*124*/ new IFB_LLLCHAR(999, "F124"),
      /*125*/ new IFB_LLLCHAR(999, "F125"),
      /*126*/ new IFB_LLLCHAR(999, "F126"),
      /*127*/ new IFB_LLLCHAR(999, "F127"),
      /*128*/ new IFB_BINARY(8, "F128"),
  };


  public VisaBase1Packager() throws ISOException {
    super();
    setFieldPackager(field);
  }

  protected static class F3Packager extends ISOBasePackager {
    protected ISOFieldPackager[] f3 =
        {
            new IFB_NUMERIC(2, "process code", false),
            new IFB_NUMERIC(2, "from account number", false),
            new IFB_NUMERIC(2, "to account number", false),
        };

    protected F3Packager() {
      super();
      setFieldPackager(f3);
    }
  }

  protected static class F55Packager extends ISOBasePackager {

    protected ISOFieldPackager[] f55 = new ISOFieldPackager[2];

    protected F55Packager() {
      super();
      init();
      setFieldPackager(f55);
    }

    private void init() {
      f55[0] = new IFB_NUMERIC(2, "dataset id", false);

      try {
        BERTLVBinaryPackager bertlvBinaryPackager = new BERTLVBinaryPackager();
        bertlvBinaryPackager.setFieldPackager(new ISOFieldPackager[] {new IFB_LLHBINARY()});
        f55[1] =
            new ISOMsgFieldPackager(new IFB_LLLHBINARY(252, "dataset content"),
                bertlvBinaryPackager);
      } catch (ISOException e) {
        throw new VisaMessageException("F55 packager init error");
      }
    }
  }

}
