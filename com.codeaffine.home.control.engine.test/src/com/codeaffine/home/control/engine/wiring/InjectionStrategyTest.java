package com.codeaffine.home.control.engine.wiring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.engine.preference.PreferenceModelImpl;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.codeaffine.util.inject.Context;

public class InjectionStrategyTest {

  private static final String ITEM_NAME = "itemName";
  private static final String INT_ATTRIBUTE_VALUE = "12";

  private Registry registry;
  private Context context;

  @Preference
  static interface ItemHandlerPreference {
    @DefaultValue( INT_ATTRIBUTE_VALUE )
    int getIntAttribute();
    void setIntAttribute( int value );
  }

  static class ItemHandler {

    private final ItemHandlerPreference preference;
    private final Registry registry;
    private final NumberItem item;
    private final Logger logger;

    ItemHandler(
      @ByName( ITEM_NAME ) NumberItem item, Registry registry, Logger logger, ItemHandlerPreference preference )
    {
      this.registry = registry;
      this.logger = logger;
      this.item = item;
      this.preference = preference;
    }

    NumberItem getItem() {
      return item;
    }

    Registry getRegistry() {
      return registry;
    }

    Logger getLogger() {
      return logger;
    }

    int getIntAttributeOfPreference() {
      return preference.getIntAttribute();
    }
  }

  @Before
  public void setUp() {
    registry = mock( Registry.class );
    context = new Context( new InjectionStrategy() );
    context.set( Registry.class, registry );
    context.set( PreferenceModel.class, new PreferenceModelImpl( mock( EventBus.class ) ) );
  }

  @Test
  public void createWithItemInjection() {
    NumberItem item = stubRegistry( ITEM_NAME, mock( NumberItem.class ) );

    ItemHandler actual = context.create( ItemHandler.class );

    assertThat( actual.getItem() ).isSameAs( item );
    assertThat( actual.getRegistry() ).isSameAs( registry );
    assertThat( actual.getLogger() ).isNotNull();
    assertThat( actual.getIntAttributeOfPreference() )
      .isEqualTo( Integer.valueOf( INT_ATTRIBUTE_VALUE ) );
  }

  private NumberItem stubRegistry( String itemName, NumberItem item ) {
    when( registry.getItem( itemName, NumberItem.class ) ).thenReturn( item );
    return item;
  }
}