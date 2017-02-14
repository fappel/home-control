package com.codeaffine.home.control.application;

import org.slf4j.LoggerFactory;

import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.UpdateEvent;
import com.codeaffine.home.control.item.ContactItem;
import com.codeaffine.home.control.type.OpenClosedType;

public enum RoomOld {

  HALL( "Flur", "hall" ),
  KITCHEN( "Küche", "kitchen" ),
  BATH_ROOM( "Badezimmer", "bathRoom" ),
  BED_ROOM( "Schlafzimmer", "bedRoom" ),
  LIVING_ROOM( "Wohnzimmer", "livingRoom" );

  private final String variablePrefix;
  private final String label;

  private ContactItem motion;

  private RoomOld( String label, String variablePrefix ) {
    this.label = label;
    this.variablePrefix = variablePrefix;
  }

  public String getLabel() {
    return label;
  }

  public String getVariablePrefix() {
    return variablePrefix;
  }

  public void registerSensorItems( ContactItem motion ) {
    this.motion = motion;
    motion.addUpdateListener( evt -> statusUpdated( evt ) );
    motion.addChangeListener( evt -> statusChanged( evt ) );
//    LoggerFactory.getLogger( Room.class ).error( "motion sensor registered: " + label + " [Thread " + Thread.currentThread().getName() + "]" );
  }

  private void statusUpdated( UpdateEvent<ContactItem, OpenClosedType> event ) {
//    event.getUpdatedStatus().ifPresent( state -> LoggerFactory.getLogger( Room.class ).error( "motion state updated: " + label + " to state " + state + " [Thread " + Thread.currentThread().getName() + "]" ) );
  }

  public void statusChanged( ChangeEvent<ContactItem, OpenClosedType> event ) {
    if( event.getOldStatus().isPresent() && event.getNewStatus().isPresent() ) {
//      LoggerFactory.getLogger( Room.class ).error( "motion state changed: " + label + " from " + event.getOldStatus().get() + " to " + event.getNewStatus().get() + " [Thread " + Thread.currentThread().getName() + "]" );
    }
  }
}