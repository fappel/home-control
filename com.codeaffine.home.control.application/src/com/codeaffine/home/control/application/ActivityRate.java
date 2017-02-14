package com.codeaffine.home.control.application;

import static com.codeaffine.home.control.type.OnOffType.*;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.LoggerFactory;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.event.UpdateEvent;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.type.DecimalType;
import com.codeaffine.home.control.type.OnOffType;

public class ActivityRate {

  private static final long CALCULATION_INTERVAL_DURATION = 10L; // Seconds
  private static final long OBSERVATION_TIME_FRAME = 5L; // Minutes
  private static final long SCAN_RATE = 2L; // Seconds

  private final Queue<LocalDateTime> motionActivations;
  private final List<RoomOld> rooms;
  private final NumberItem activityRate;
  private final SwitchItem switchItem;

  public ActivityRate( @ByName( "activityRate" ) NumberItem activityRate,
                       @ByName( "switchWindowUplight" ) SwitchItem switchItem )
  {
    this.activityRate = activityRate;
    this.switchItem = switchItem;
    this.motionActivations = new LinkedList<>();
    this.rooms = asList( RoomOld.values() );
  }

  @Observe( "activityRate" )
  void onChange( ChangeEvent<NumberItem, DecimalType> event ) {
//    LoggerFactory.getLogger( ActivityRate.class ).info( "activityRate status change: " + event.getSource().getStatus( -1 ) );
  }

  @Observe( "switchWindowUplight" )
  void onUpdate( UpdateEvent<SwitchItem, OnOffType> event ) {
//    LoggerFactory.getLogger( ActivityRate.class ).info( "switchWindowUplight status update: " + event.getSource().getStatus() );
  }

  @Schedule( period = SCAN_RATE )
  private void calculateRate() {
//    LoggerFactory.getLogger( ActivityRate.class ).info( "calculate Rate: " + activityRate.getStatus( -1 ) );

//    OnOffType status = switchItem.getStatus( OFF );
//    switchItem.updateStatus( flip( status ) );
//    activityRate.setStatus( 10 );
//
//    if( activityRate.getStatus().isPresent() ) {
//      activityRate.getStatus().get().intValue();
//    }
//    int rate = activityRate.getStatus( 10 );
//    activityRate.setStatus( 10 );
//    DecimalType decimalType = activityRate.getStatus().get();
//    activityRate.setStatus( new DecimalType( 2 ) );
  }

  @Schedule( initialDelay = 10, period = CALCULATION_INTERVAL_DURATION )
  private void updateMotionActivations() {
//    LoggerFactory.getLogger( ActivityRate.class ).info( "updateMotionActivations: " + activityRate.getStatus( -1 ) );
//    Boolean isOpen = rooms
//      .stream()
//      .map( room -> /* room.getMotionState() == OPEN */Boolean.TRUE )
//      .reduce( ( state1, state2 ) -> Boolean
//        .valueOf( state1.booleanValue() == state2.booleanValue() ) )
//      .get();
//    if( isOpen.booleanValue() ) {
//      motionActivations.add( now() );
//    }
//    while( hasExpiredTimestamps() ) {
//      motionActivations.poll();
//    }
  }

  private boolean hasExpiredTimestamps() {
    return !motionActivations.isEmpty()
           && motionActivations.peek().plusMinutes( OBSERVATION_TIME_FRAME ).isBefore( now() );
  }
}