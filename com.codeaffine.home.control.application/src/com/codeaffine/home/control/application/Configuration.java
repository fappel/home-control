package com.codeaffine.home.control.application;

import java.util.stream.Stream;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.item.ContactItem;

public class Configuration implements SystemConfiguration {

  @Override
  public void configureSystem( Context context ) {
    Registry registry = context.get( Registry.class );
    Stream.of( Room.values() ).forEach( room -> {
      room.registerSensorItems( registry.getItem( room.getVariablePrefix() + "Motion", ContactItem.class ) );
    } );
    context.create( ActivityRate.class );
  }
}