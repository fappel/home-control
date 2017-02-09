package com.codeaffine.home.control.type;

import com.codeaffine.home.control.Status;

public enum OnOffType implements Status {
  ON, OFF;

  public static OnOffType flip( OnOffType status ) {
    return status == ON ? OFF : ON;
  }
}
