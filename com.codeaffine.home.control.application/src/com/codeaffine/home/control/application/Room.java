package com.codeaffine.home.control.application;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.StatusChangeListener;
import com.codeaffine.home.control.item.ContactItem;
import com.codeaffine.home.control.type.OpenClosedType;

public enum Room implements StatusChangeListener<OpenClosedType> {

  HALL( "Flur", "hall" ),
  KITCHEN( "Küche", "kitchen" ),
  BATH_ROOM( "Badezimmer", "bathRoom" ),
  BED_ROOM( "Schlafzimmer", "bedRoom" ),
  LIVING_ROOM( "Wohnzimmer", "livingRoom" );

  private final String variablePrefix;
  private final String label;

  private ContactItem motion;

  private Room( String label, String variablePrefix ) {
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
    motion.addItemStateChangeListener( this );
    LoggerFactory.getLogger( Room.class ).error( "motion sensor registered: " + label + " [Thread " + Thread.currentThread().getName() + "]" );
  }

  @Override
  public void statusUpdated( Item<OpenClosedType> item, Optional<OpenClosedType> status ) {
    status.ifPresent( state -> getLogger( Room.class ).error( "motion state updated: " + label + " to state " + state + " [Thread " + Thread.currentThread().getName() + "]" ) );
  }

  @Override
  public void statusChanged( Item<OpenClosedType> item,
                            Optional<OpenClosedType> oldStatus,
                            Optional<OpenClosedType> newStatus )
  {
    if( oldStatus.isPresent() && newStatus.isPresent() ) {
      LoggerFactory.getLogger( Room.class ).error( "motion state changed: " + label + " from " + oldStatus.get() + " to " + newStatus.get() + " [Thread " + Thread.currentThread().getName() + "]" );
    }
  }
}