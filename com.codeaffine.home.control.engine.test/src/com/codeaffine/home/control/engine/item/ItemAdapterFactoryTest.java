package com.codeaffine.home.control.engine.item;

import static com.codeaffine.home.control.engine.item.Messages.ERROR_ITEM_TYPE_NOT_SUPPORTED;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.smarthome.core.events.EventPublisher;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.Status;
import com.codeaffine.home.control.engine.adapter.ItemAdapter;
import com.codeaffine.home.control.engine.adapter.ItemRegistryAdapter;
import com.codeaffine.home.control.engine.adapter.ShutdownDispatcher;
import com.codeaffine.home.control.engine.item.ContactItemAdapter;
import com.codeaffine.home.control.engine.item.DimmerItemAdapter;
import com.codeaffine.home.control.engine.item.ItemAdapterFactory;
import com.codeaffine.home.control.engine.item.NumberItemAdapter;
import com.codeaffine.home.control.engine.item.StringItemAdapter;
import com.codeaffine.home.control.engine.item.SwitchItemAdapter;
import com.codeaffine.home.control.engine.util.SystemExecutorImpl;
import com.codeaffine.home.control.item.ContactItem;
import com.codeaffine.home.control.item.DimmerItem;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.item.StringItem;
import com.codeaffine.home.control.item.SwitchItem;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith( JUnitParamsRunner.class )
public class ItemAdapterFactoryTest {

  private static final String ITEM_NAME = "itemName";

  public static class ItemTypeMapping {

    public final Class<?> interfaceType;
    public final Class<?> adapterType;

    public ItemTypeMapping( Class<?> interfaceType, Class<?> adapterType ) {
      this.interfaceType = interfaceType;
      this.adapterType = adapterType;
    }
  }

  public static class ItemTypeMappingProvider {

    public static Object[] provideData() {
      return new Object[] {
        new ItemTypeMapping( NumberItem.class, NumberItemAdapter.class ),
        new ItemTypeMapping( SwitchItem.class, SwitchItemAdapter.class ),
        new ItemTypeMapping( ContactItem.class, ContactItemAdapter.class ),
        new ItemTypeMapping( DimmerItem.class, DimmerItemAdapter.class ),
        new ItemTypeMapping( StringItem.class, StringItemAdapter.class )
      };
    }
  }

  @Test
  @Parameters( source = ItemTypeMappingProvider.class )
  public void createAdapter( ItemTypeMapping mapEntry ) {
    ItemRegistryAdapter registry = mock( ItemRegistryAdapter.class );
    EventPublisher publisher = mock( EventPublisher.class );
    ShutdownDispatcher shutdownDispatcher = mock( ShutdownDispatcher.class );
    SystemExecutorImpl executor = mock( SystemExecutorImpl.class );

    ItemAdapter<? extends Item<?, ?>, ? extends Status> actual
      = ItemAdapterFactory.createAdapter( ITEM_NAME,
                                          mapEntry.interfaceType,
                                          registry,
                                          publisher,
                                          shutdownDispatcher,
                                          executor );

    assertThat( actual ).isInstanceOf( mapEntry.adapterType );
  }

  @Test
  public void createAdapterForUnknownType() {
    Class<Runnable> unknownType = Runnable.class;

    Throwable actual = thrownBy( () -> ItemAdapterFactory.createAdapter( null, unknownType, null, null, null, null ) );

    assertThat( actual ).hasMessage( format( ERROR_ITEM_TYPE_NOT_SUPPORTED, unknownType.getName() ) );
  }
}