package com.codeaffine.home.control.internal.activation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.ItemByName;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.util.inject.Context;

public class SystemContextFactoryTest {

  public static final String ITEM_NAME = "itemName";

  private SystemContextFactory factory;
  private Registry registry;

  static class ItemHandler {

    private final NumberItem item;

    ItemHandler( @ItemByName( ITEM_NAME ) NumberItem item ) {
      this.item = item;
    }

    NumberItem getItem() {
      return item;
    }
  }

  @Before
  public void setUp() {
    registry = mock( Registry.class );
    factory = new SystemContextFactory( registry );
  }

  @Test
  public void create() {
    Context actual = factory.create();

    assertThat( actual.get( Registry.class ) ).isEqualTo( registry );
  }

  @Test
  public void createInstanceWithItemInjection() {
    NumberItem expected = stubRegistryWithItem( ITEM_NAME, mock( NumberItem.class ) );
    Context context = factory.create();

    ItemHandler itemHandler = context.create( ItemHandler.class );
    NumberItem actual = itemHandler.getItem();

    assertThat( actual ).isSameAs( expected );
  }

  private NumberItem stubRegistryWithItem( String itemName, NumberItem item ) {
    when( registry.getItem( itemName, NumberItem.class ) ).thenReturn( item );
    return item;
  }
}