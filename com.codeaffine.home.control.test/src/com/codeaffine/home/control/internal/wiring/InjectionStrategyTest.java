package com.codeaffine.home.control.internal.wiring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.util.inject.Context;

public class InjectionStrategyTest {

  private static final String ITEM_NAME = "itemName";

  private Registry registry;
  private Context context;

  static class ItemHandler {

    private final Registry registry;
    private final NumberItem item;

    ItemHandler( @ByName( ITEM_NAME ) NumberItem item, Registry registry ) {
      this.item = item;
      this.registry = registry;
    }

    NumberItem getItem() {
      return item;
    }

    Registry getRegistry() {
      return registry;
    }
  }

  @Before
  public void setUp() {
    registry = mock( Registry.class );
    context = new Context( new InjectionStrategy() );
    context.set( Registry.class, registry );
  }

  @Test
  public void createWithItemInjection() {
    NumberItem item = stubRegistry( ITEM_NAME, mock( NumberItem.class ) );

    ItemHandler actual = context.create( ItemHandler.class );

    assertThat( actual.getItem() ).isSameAs( item );
    assertThat( actual.getRegistry() ).isSameAs( registry );
  }

  private NumberItem stubRegistry( String itemName, NumberItem item ) {
    when( registry.getItem( itemName, NumberItem.class ) ).thenReturn( item );
    return item;
  }
}