package com.codeaffine.home.control.application.internal.bulb;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.application.BulbProvider.Bulb;
import com.codeaffine.home.control.application.BulbProvider.BulbDefinition;
import com.codeaffine.home.control.entity.EntityProvider.EntityFactory;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.SwitchItem;

public class BulbFactory implements EntityFactory<Bulb, BulbDefinition> {

  static final String PREFIX_COLOR_TEMPERATURE = "colorTemperature";
  static final String PREFIX_BRIGHTNESS = "dimmer";
  static final String PREFIX_SWITCH = "switch";

  private final Registry registry;

  public BulbFactory( Registry registry ) {
    this.registry = registry;
  }

  @Override
  public Bulb create( BulbDefinition definition ) {
    return new BulbImpl( definition,
                         registry.getItem( PREFIX_SWITCH + definition, SwitchItem.class ),
                         registry.getItem( PREFIX_BRIGHTNESS + definition, DimmerItem.class ),
                         registry.getItem( PREFIX_COLOR_TEMPERATURE + definition, DimmerItem.class ) );
  }
}