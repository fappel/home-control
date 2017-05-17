package com.codeaffine.home.control.engine.activation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.engine.component.util.BundleDeactivationTracker;
import com.codeaffine.home.control.engine.component.util.TypeUnloadTracker;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.util.inject.Context;

public class SystemContextFactoryTest {

  public static final String ITEM_NAME = "itemName";

  private SystemContextFactory factory;
  private BundleContext bundleContext;
  private Registry registry;


  static class ItemHandler {

    private final NumberItem item;

    ItemHandler( @ByName( ITEM_NAME ) NumberItem item ) {
      this.item = item;
    }

    NumberItem getItem() {
      return item;
    }
  }

  @Before
  public void setUp() {
    registry = mock( Registry.class );
    bundleContext = mock( BundleContext.class );
    factory = new SystemContextFactory( registry, bundleContext );
  }

  @Test
  public void create() {
    Context actual = factory.create();

    assertThat( actual.get( Registry.class ) ).isEqualTo( registry );
    assertThat( actual.get( TypeUnloadTracker.class ) ).isInstanceOf( BundleDeactivationTracker.class );
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