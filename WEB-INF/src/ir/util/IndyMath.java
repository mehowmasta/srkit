package ir.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class IndyMath
{
  public static final BigDecimal HUNDRED = new BigDecimal(100).setScale(6);
  public static final int RoundFractionScale = 6;
  public static final RoundingMode RoundMode = RoundingMode.HALF_UP;
  /**
   * Instantiates BigDecimal from passed double, sets scale to 8
   */
  public static BigDecimal bd(double v)
  {
    return bd(v,7);
  }
  /**
   * Instantiates BigDecimal from passed double, sets scale to that passed.
   */
  public static BigDecimal bd(double v, int scale)
  {
    return new BigDecimal(v).setScale(scale, RoundMode);
  }
  /**
   * Returns number of nonzero digits to the right
   * of the decimal.
   * <br> ie. 81.875000   yields 3
   */
  public static int countDecimalDigits(BigDecimal d)
  {
    String t = d.stripTrailingZeros().toString();
    int dotAt = t.indexOf('.');
    return dotAt == -1 ? 0 : t.length() - dotAt - 1;
  }
  /**
   * Returns number of nonzero digits to the right
   * of the decimal.
   * <br> ie. 81.875000   yields 3
   */
  public static int countDecimalDigits(double d)
  {
    String t = d +"";
    int dotAt = t.indexOf('.');
    if (dotAt<0)
    {
      return 0;
    }
    int lastNonZeroAt = t.length()-1;
    while (lastNonZeroAt>dotAt)
    {
      char c = t.charAt(lastNonZeroAt);
      if (c!='0')
      {
        break;
      }
      lastNonZeroAt--;
    }
    return lastNonZeroAt - dotAt;
  }
  /**
   * Indicates whether a.compareTo(b)==0
   */
  public static boolean eq(BigDecimal a, BigDecimal b)
  {
    return a.compareTo(b) == 0;
  }
  /**
   * Indicates whether a.compareTo(b)==0
   */
  public static boolean eq(BigDecimal a, BigDecimal b, BigDecimal tolerance)
  {
    return a.subtract(b).abs().compareTo(tolerance) <= 0;
  }
  /**
   * Compares two BigDecimal values using BigDecimal.compareTo
   */
  public static boolean eq(BigDecimal a, BigDecimal b,int scale)
  {
    return eq(a, b,new BigDecimal(1.0 / Math.pow(10,scale)));
  }
  /**
   * Indicates whether a = b within a decimal tolerance
   */
  public static boolean eq(double a, double b, int scale)
  {
    return Math.abs(a - b) < (scale == 0 ? 1 : 1.0/Math.pow(10,scale));
  }
  public static boolean ez(double a)
  {
    return ez(a,2);
  }
  public static boolean ez(double a,int decimals)
  {
    return eq(0,a,decimals);
  }
  /**
   * Rounds BigDecimal to nearest 1/nFraction value, scaled to
   * RoundFractionScale
   */
  public static BigDecimal floorFraction(BigDecimal dValue, int nFraction)
  {
    if (nFraction==0)
    {
      return new BigDecimal(dValue.intValue());
    }
    BigDecimal df = new BigDecimal(nFraction);
    return dValue.multiply(df).setScale(0, RoundingMode.FLOOR).divide(df, RoundFractionScale, IndyMath.RoundMode);
  }
  /**
   * Takes floor of passed value to nearest 1/nFraction value, returns mixed
   * string like 1 3/4.
   */
  public static String floorFractionStr(BigDecimal dValue, int nFraction)
  {
    BigDecimal floor0 = floorFraction(dValue,0);
    String s = floor0.stripTrailingZeros().toPlainString();
    if (nFraction==0)
    {
      return s;
    }
    BigDecimal v = floorFraction(dValue,nFraction);
    int denominator = nFraction;
    int numerator = v.subtract(floor0).multiply(new BigDecimal(nFraction)).intValue();
    if (numerator==0)
    {
      return s;
    }
    if (denominator % 2 == 0 )
    {
      while (numerator % 2 == 0)
      {
        numerator /= 2;
        denominator /= 2;
      }
    }
    return s + " " + numerator + "/" + denominator;
  }
  public static int getDecimalsForFraction(int fraction)
  {//should yield someting like 0.125
    String v = "" + (1.0 / fraction);
    return v.length() <= 2 ? 0 : v.length() - 2;
  }
  /**
   * Indicates whether passed value is > 0
   */
  public static boolean gz(BigDecimal a)
  {
    return a.compareTo(BigDecimal.ZERO) > 0;
  }
  /**
   * Indicates whether passedValue.compareTo(BigDecimal.ZERO)==0
   */
  public static boolean isZero(BigDecimal a)
  {
    return a.compareTo(BigDecimal.ZERO) == 0;
  }
  /**
   * Returns nearest even integer from passed value
   */
  public static int makeEven(double v)
  {
    return (int) Math.round((Math.round(v / 2.0) + .000000001) * 2);
  }
  /**
   * Returns multiplier * part / sum rounded to greatest scale of arguments   */
  public static BigDecimal multPart(BigDecimal multiplier, BigDecimal part, BigDecimal sum,int scale)
  {
    if (sum.compareTo(BigDecimal.ZERO)==0)
    {
      return BigDecimal.ZERO;
    }
    BigDecimal res1 = multiplier.multiply(part);
    BigDecimal res = res1.divide(sum, scale, RoundMode);
    return res;
  }
  /**
   * Returns multiplier * pct / 100
   */
  public static BigDecimal multPct(BigDecimal multiplier, BigDecimal pct,int scale)
  {
    return multPart(multiplier, pct, HUNDRED,scale);
  }
  /**
   * Compares two BigDecimal values using BigDecimal.compareTo
   */
  public static boolean ne(BigDecimal a, BigDecimal b)
  {
    return !eq(a, b);
  }
  /**
   * Compares two BigDecimal values using BigDecimal.compareTo
   */
  public static boolean ne(BigDecimal a, BigDecimal b,BigDecimal tolerance)
  {
    return !eq(a, b,tolerance);
  }
  /**
   * Compares two BigDecimal values using BigDecimal.compareTo
   */
  public static boolean ne(BigDecimal a, BigDecimal b,int scale)
  {
    return !eq(a, b,new BigDecimal(1.0 / Math.pow(10,scale)));
  }
  /**
   * Indicates whether a <> b within a decimal tolerance
   */
  public static boolean ne(double a, double b)
  {
    return !eq(a,b,4);
  }
  /**
   * Indicates whether a <> b within a decimal tolerance
   */
  public static boolean ne(double a, double b, int scale)
  {
    return !eq(a,b,scale);
  }
  /**
   * Indicates if passed value is not zero
   */
  public static boolean nz(BigDecimal a)
  {
    return !isZero(a);
  }
  public static boolean nz(double a)
  {
    return ne(0,a,2);
  }
  /**
   * Indicates if passed value is not zero
   */
  public static boolean nz(double a,int scale)
  {
    return ne(0,a,scale);
  }
  /**
   * Scales passed BigDecimal to scale 0 with IndyMath.RoundMode, returns
   * result as long
   */
  public static BigDecimal round(BigDecimal d,int scale)
  {
    return d.setScale(scale, RoundMode);
  }
  /**
   * Rounds passed double, returns result as long
   */
  public static long round(double d)
  {
    return Math.round(d);
  }
  /**
   * Rounds passed double, returns result as long
   */
  public static double round(double inValue, int digits)
  {
    double multiplier;
    switch (digits)
    {
      case 0:
        return Math.round(inValue);
      case 1:
        multiplier = 10;
        break;
      case 2:
        multiplier = 100;
        break;
      case 3:
        multiplier = 1000;
        break;
      case 4:
        multiplier = 10000;
        break;
      case 5:
        multiplier = 100000;
        break;
      case 6:
        multiplier = 1000000;
        break;
      case 7:
        multiplier = 10000000;
        break;
      case 8:
        multiplier = 100000000;
        break;
      default:
        multiplier = Math.pow(10, digits);
        break;
    }
    return Math.round(inValue * multiplier) / multiplier;
  }
  /**
   * Scales passed BigDecimal to scale 0 with IndyMath.RoundMode, returns
   * result as long
   */
  public static long round0(BigDecimal d)
  {
    return d.setScale(0, RoundMode).longValue();
  }
  public static double round2(double d)
  {
    return Math.round(100.0 * d + (d < 0 ? -.001 : .001)) / 100.0;
  }
  /**
   * Rounds BigDecimal to nearest 1/nFraction value, scaled to
   * RoundFractionScale
   */
  public static BigDecimal roundFraction(BigDecimal dValue, int nFraction)
  {
    if (nFraction==0)
    {
      return new BigDecimal(dValue.intValue());
    }
    BigDecimal df = new BigDecimal(nFraction);
    return dValue.multiply(df).setScale(0, RoundMode).divide(df, RoundFractionScale, IndyMath.RoundMode);
  }
  /**
   * Rounds to nearest 1/nFraction value
   */
  public static double roundFraction(double dValue, int nFraction)
  {
    return Math.round(dValue * nFraction) / (double) nFraction;
  }
  public static int roundInt(BigDecimal d)
  {
    return (int) Math.round(d.doubleValue());
  }
  public static int roundInt(double d)
  {
    return (int) Math.round(d);
  }
  /**
   * Zero or Equal: indicates whether a.compareTo(ZERO)==0 OR
   * a.compareTo(b)==0
   */
  public static boolean zeq(BigDecimal a, BigDecimal b)
  {
    return isZero(a) || a.compareTo(b) == 0;
  }
}
