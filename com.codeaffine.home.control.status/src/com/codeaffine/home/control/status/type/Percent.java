package com.codeaffine.home.control.status.type;

import static com.codeaffine.home.control.status.type.Messages.ERROR_OUT_OF_RANGE;
import static com.codeaffine.util.ArgumentVerification.verifyCondition;
import static java.lang.Integer.parseInt;

public enum Percent {
  P_000, P_001, P_002, P_003, P_004, P_005, P_006, P_007, P_008, P_009,
  P_010, P_011, P_012, P_013, P_014, P_015, P_016, P_017, P_018, P_019,
  P_020, P_021, P_022, P_023, P_024, P_025, P_026, P_027, P_028, P_029,
  P_030, P_031, P_032, P_033, P_034, P_035, P_036, P_037, P_038, P_039,
  P_040, P_041, P_042, P_043, P_044, P_045, P_046, P_047, P_048, P_049,
  P_050, P_051, P_052, P_053, P_054, P_055, P_056, P_057, P_058, P_059,
  P_060, P_061, P_062, P_063, P_064, P_065, P_066, P_067, P_068, P_069,
  P_070, P_071, P_072, P_073, P_074, P_075, P_076, P_077, P_078, P_079,
  P_080, P_081, P_082, P_083, P_084, P_085, P_086, P_087, P_088, P_089,
  P_090, P_091, P_092, P_093, P_094, P_095, P_096, P_097, P_098, P_099,
  P_100;

  @Override
  public String toString() {
    return intValue() + "%";
  }

  public int intValue() {
    return parseInt( name().split( "_" )[ 1 ] );
  }

  public static Percent valueOf( int percent ) {
    verifyCondition( percent > -1 && percent < 101, ERROR_OUT_OF_RANGE, Integer.valueOf( percent ) );

    return Percent.values()[ percent ];
  }
}