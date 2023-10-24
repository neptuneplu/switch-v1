package me.card.switchv1.cupserver.message.jpos;


import org.jpos.iso.IFA_LLCHAR;
import org.jpos.iso.IFA_LLLBINARY;
import org.jpos.iso.IFA_LLLCHAR;
import org.jpos.iso.IFA_LLNUM;
import org.jpos.iso.IFA_NUMERIC;
import org.jpos.iso.IFB_BINARY;
import org.jpos.iso.IFB_BITMAP;
import org.jpos.iso.IF_CHAR;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.ISOFieldPackager;

public class CupPackager extends ISOBasePackager {

  protected static ISOFieldPackager[] field = {
      /*000*/ new IFA_NUMERIC(4, "MTI"),
      /*001*/ new IFB_BITMAP(16, "BITMAP"),
      /*002*/ new IFA_LLNUM(19, "F2"),
      /*003*/ new IFA_NUMERIC(6, "F3"),
      /*004*/ new IFA_NUMERIC(12, "F4"),
      /*005*/ new IFA_NUMERIC(12, "F5"),
      /*006*/ new IFA_NUMERIC(12, "F6"),
      /*007*/ new IFA_NUMERIC(10, "F7"),
      /*008*/ new IFA_NUMERIC(8, "F8"),
      /*009*/ new IFA_NUMERIC(8, "F9"),
      /*010*/ new IFA_NUMERIC(8, "F10"),
      /*011*/ new IFA_NUMERIC(6, "F11"),
      /*012*/ new IFA_NUMERIC(6, "F12"),
      /*013*/ new IFA_NUMERIC(4, "F13"),
      /*014*/ new IFA_NUMERIC(4, "F14"),
      /*015*/ new IFA_NUMERIC(4, "F15"),
      /*016*/ new IFA_NUMERIC(4, "F16"),
      /*017*/ new IFA_NUMERIC(4, "F17"),
      /*018*/ new IFA_NUMERIC(4, "F18"),
      /*019*/ new IFA_NUMERIC(3, "F19"),
      /*020*/ new IFA_NUMERIC(3, "F20"),
      /*021*/ new IFA_NUMERIC(3, "F21"),
      /*022*/ new IF_CHAR(3, "F22"),
      /*023*/ new IFA_NUMERIC(3, "F23"),
      /*024*/ new IFA_NUMERIC(3, "F24"),
      /*025*/ new IFA_NUMERIC(2, "F25"),
      /*026*/ new IFA_NUMERIC(2, "F26"),
      /*027*/ new IFA_NUMERIC(1, "F27"),
      /*028*/ new IF_CHAR(9, "F28"),
      /*029*/ new IFA_NUMERIC(3, "F29"),
      /*030*/ new IFA_NUMERIC(24, "F30"),
      /*031*/ new IFA_LLCHAR(99, "F31"),
      /*032*/ new IFA_LLNUM(11, "F32"),
      /*033*/ new IFA_LLNUM(11, "F33"),
      /*034*/ new IFA_LLCHAR(28, "F34"),
      /*035*/ new IFA_LLCHAR(37, "F35"),
      /*036*/ new IFA_LLLCHAR(104, "F36"),
      /*037*/ new IF_CHAR(12, "F37"),
      /*038*/ new IF_CHAR(6, "F38"),
      /*039*/ new IFA_NUMERIC(2, "F39"),
      /*040*/ new IFA_NUMERIC(3, "F40"),
      /*041*/ new IF_CHAR(8, "F41"),
      /*042*/ new IF_CHAR(15, "F42"),
      /*043*/ new IF_CHAR(40, "F43"),
      /*044*/ new IFA_LLCHAR(99, "F44"),
      /*045*/ new IFA_LLCHAR(76, "F45"),
      /*046*/ new IFA_LLLCHAR(204, "F46"),
      /*047*/ new IFA_LLLCHAR(999, "F47"),
      /*048*/ new IFA_LLLCHAR(999, "F48"),
      /*049*/ new IF_CHAR(3, "F49"),
      /*050*/ new IF_CHAR(3, "F50"),
      /*051*/ new IF_CHAR(3, "F51"),
      /*052*/ new IFB_BINARY(8, "F52"),
      /*053*/ new IFA_NUMERIC(16, "F53"),
      /*054*/ new IFA_LLLCHAR(120, "F54"),
      /*055*/ new IFA_LLLBINARY(255, "F55"),
      /*056*/ new IFA_LLNUM(35, "F56"),
      /*057*/ new IFA_NUMERIC(3, "F57"),
      /*058*/ null,
      /*059*/ new IFA_LLLCHAR(999, "F59"),
      /*060*/ new IFA_LLLCHAR(100, "F60"),
      /*061*/ new IFA_LLLCHAR(999, "F61"),
      /*062*/ new IFA_LLLCHAR(999, "F62"),
      /*063*/ new IFA_LLLCHAR(999, "F63"),
      /*064*/ null,
      /*065*/ null,
      /*066*/ null,
      /*067*/ null,
      /*068*/ null,
      /*069*/ null,
      /*070*/ new IFA_NUMERIC(3, "F70"),
      /*071*/ null,
      /*072*/ null,
      /*073*/ null,
      /*074*/ null,
      /*075*/ null,
      /*076*/ null,
      /*077*/ null,
      /*078*/ null,
      /*079*/ null,
      /*080*/ null,
      /*081*/ null,
      /*082*/ null,
      /*083*/ null,
      /*084*/ null,
      /*085*/ null,
      /*086*/ null,
      /*087*/ null,
      /*088*/ null,
      /*089*/ null,
      /*090*/ new IFA_NUMERIC(42, "F90"),
      /*091*/ null,
      /*092*/ null,
      /*093*/ null,
      /*094*/ null,
      /*095*/ null,
      /*096*/ new IFB_BINARY(8, "F96"),
      /*097*/ null,
      /*098*/ null,
      /*099*/ null,
      /*100*/ new IFA_LLNUM(11, "F100"),
      /*101*/ null,
      /*102*/ new IFA_LLCHAR(28, "F102"),
      /*103*/ new IFA_LLCHAR(28, "F103"),
      /*104*/ new IFA_LLLCHAR(100, "F104"),
      /*105*/ null,
      /*106*/ null,
      /*107*/ null,
      /*108*/ null,
      /*109*/ null,
      /*110*/ null,
      /*111*/ null,
      /*112*/ null,
      /*113*/ new IFA_LLLCHAR(512, "F113"),
      /*114*/ null,
      /*115*/ null,
      /*116*/ new IFA_LLLCHAR(512, "F116"),
      /*117*/ new IFA_LLLCHAR(256, "F117"),
      /*118*/ null,
      /*119*/ null,
      /*120*/ null,
      /*121*/ new IFA_LLLCHAR(100, "F121"),
      /*122*/ new IFA_LLLCHAR(100, "F122"),
      /*123*/ new IFA_LLLCHAR(100, "F123"),
      /*124*/ null,
      /*125*/ new IFA_LLLCHAR(256, "F125"),
      /*126*/ new IFA_LLLCHAR(256, "F126"),
      /*127*/ null,
      /*128*/ new IFB_BINARY(8, "F128"),
  };


  public CupPackager() throws ISOException {
    super();
    setFieldPackager(field);
  }


}
