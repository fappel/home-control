package com.codeaffine.home.control.application;

import static java.util.Arrays.asList;

import java.math.BigDecimal;
import java.util.Collection;

public class Model {

  // private NumberItemAdapter activityRate;
  public BigDecimal getActivityRate() {
    // activityRate.get
    return null;
  }

  public void setActivityRate( BigDecimal activityRate ) {
  }

  Collection<Room> getRooms() {
    return asList( Room.values() );
  }
}