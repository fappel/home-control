package com.codeaffine.home.control.application.internal.lamp;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.logger.LoggerFactory;

public class LampFactory implements EntityFactory<Lamp, LampDefinition> {

  static final String PREFIX_COLOR_TEMPERATURE = "colorTemperature";
  static final String PREFIX_BRIGHTNESS = "dimmer";
  static final String PREFIX_SWITCH = "switch";

  private final LoggerFactory loggerFactory;
  private final Registry registry;

  public LampFactory( Registry registry, LoggerFactory loggerFactory ) {
    verifyNotNull( loggerFactory, "loggerFactory" );
    verifyNotNull( registry, "registry" );

    this.loggerFactory = loggerFactory;
    this.registry = registry;
  }

  @Override
  public Lamp create( LampDefinition definition ) {
    verifyNotNull( definition, "definition" );

    return new LampImpl( definition,
                         registry.getItem( PREFIX_SWITCH + definition, SwitchItem.class ),
                         registry.getItem( PREFIX_BRIGHTNESS + definition, DimmerItem.class ),
                         registry.getItem( PREFIX_COLOR_TEMPERATURE + definition, DimmerItem.class ),
                         loggerFactory.getLogger( LampImpl.class ) );
  }
}