package com.codeaffine.home.control.internal.adapter;

import static com.codeaffine.home.control.internal.adapter.ExecutorHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.eclipse.smarthome.core.events.Event;
import org.eclipse.smarthome.core.events.EventPublisher;
import org.eclipse.smarthome.core.items.GenericItem;
import org.eclipse.smarthome.core.items.StateChangeListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import com.codeaffine.home.control.ItemStateChangeListener;
import com.codeaffine.home.control.internal.util.SystemExecutor;
import com.codeaffine.home.control.type.OpenClosedType;

public class ItemAdapterTest {

  private static final String KEY = "key";

  private ShutdownDispatcher shutdownDispatcher;
  private ItemAdapter<OpenClosedType> adapter;
  private ItemRegistryAdapter registry;
  private EventPublisher eventPublisher;
  private SystemExecutor executor;
  private GenericItem item;
  private Runnable resetHook;

  @Before
  public void setUp() {
    item = stubGenericItem();
    registry = stubItemRegistryAdapter( item );
    shutdownDispatcher = new ShutdownDispatcher();
    executor = stubInThreadExecutor();
    eventPublisher = mock( EventPublisher.class );
    adapter = new ItemAdapter<>( KEY, registry, eventPublisher, shutdownDispatcher, executor, OpenClosedType.class );
    adapter.initialize();
    resetHook = captureResetHook( registry );
  }

  @Test
  public void getItem() {
    GenericItem actual = adapter.getItem();

    assertThat( actual ).isSameAs( item );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void addItemStateChangeListener() {
    ItemStateChangeListener<OpenClosedType> expected = mock( ItemStateChangeListener.class );

    adapter.addItemStateChangeListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    assertThat( captor.getValue().getListener() ).isSameAs( expected );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void addItemStateChangeListenerTwice() {
    ItemStateChangeListener<OpenClosedType> expected = mock( ItemStateChangeListener.class );

    adapter.addItemStateChangeListener( expected );
    adapter.addItemStateChangeListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    assertThat( captor.getValue().getListener() ).isSameAs( expected );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void removeItemStateChangeListener() {
    ItemStateChangeListener<OpenClosedType> expected = mock( ItemStateChangeListener.class );
    adapter.addItemStateChangeListener( expected );

    adapter.removeItemStateChangeListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void removeItemStateChangeListenerTwice() {
    ItemStateChangeListener<OpenClosedType> expected = mock( ItemStateChangeListener.class );
    adapter.addItemStateChangeListener( expected );
    adapter.removeItemStateChangeListener( expected );

    adapter.removeItemStateChangeListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void shutdownDispatch() {
    ItemStateChangeListener<OpenClosedType> listener = mock( ItemStateChangeListener.class );

    adapter.addItemStateChangeListener( listener );
    shutdownDispatcher.dispatch();

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
    verify( registry ).removeResetHook( resetHook );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void shutdownDispatchAfterItemStateChangeListenerRemoval() {
    ItemStateChangeListener<OpenClosedType> listener = mock( ItemStateChangeListener.class );

    adapter.addItemStateChangeListener( listener );
    adapter.removeItemStateChangeListener( listener );
    shutdownDispatcher.dispatch();

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
    verify( registry ).removeResetHook( resetHook );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void triggerStateChangeIfExecutorIsBlocked() {
    blockExecutor( executor );
    ItemStateChangeListener<OpenClosedType> listener = mock( ItemStateChangeListener.class );
    adapter.addItemStateChangeListener( listener );
    ArgumentCaptor<StateChangeListener> captor = forClass( StateChangeListener.class );
    verify( item ).addStateChangeListener( captor.capture() );

    captor.getValue().stateUpdated( item, null );

    verify( listener, never() ).stateUpdated( any( ItemAdapter.class ), any( Optional.class ) );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void triggerResetHook() {
    ItemStateChangeListener<OpenClosedType> listener = mock( ItemStateChangeListener.class );
    adapter.addItemStateChangeListener( listener );
    GenericItem replacement = stubItemRegistryAdapter( registry, stubGenericItem() );

    resetHook.run();

    InOrder order = inOrder( item, replacement );
    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    order.verify( item ).addStateChangeListener( captor.capture() );
    StateChangeAdapter stateChangeAdapter = captor.getValue();
    order.verify( item ).removeStateChangeListener( stateChangeAdapter );
    order.verify( replacement ).addStateChangeListener( stateChangeAdapter );
    assertThat( adapter.getItem() ).isSameAs( replacement );
  }

  @Test
  public void getStatus() {
    when( item.getState() ).thenReturn( org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN );

    Optional<OpenClosedType> actual = adapter.getStatus();

    assertThat( actual ).hasValue( OpenClosedType.OPEN );
  }

  @Test
  public void setStatusInternal() {
    adapter.setStatusInternal( OpenClosedType.OPEN );

    verify( item ).setState( org.eclipse.smarthome.core.library.types.OpenClosedType.OPEN );
  }

  @Test( expected = IllegalArgumentException.class )
  public void setStatusInternalWithNullArgument() {
    adapter.setStatusInternal( null );
  }

  @Test
  public void sendStatusInternal() {
    adapter.sendStatusInternal( OpenClosedType.OPEN );

    ArgumentCaptor<Event> captor = forClass( Event.class );
    verify( eventPublisher ).post( captor.capture() );
    assertThat( captor.getValue().getPayload() ).isEqualTo( "{\"type\":\"OpenClosedType\",\"value\":\"OPEN\"}" );
  }

  @Test(expected = IllegalArgumentException.class)
  public void sendStatusInternalWithNullArgument() {
    adapter.sendStatusInternal( null );
  }

  private static GenericItem stubGenericItem() {
    GenericItem result = mock( GenericItem.class );
    when( result.getName() ).thenReturn( "itemName" );
    return result;
  }

  private static ItemRegistryAdapter stubItemRegistryAdapter( GenericItem item ) {
    ItemRegistryAdapter result = mock( ItemRegistryAdapter.class );
    stubItemRegistryAdapter( result, item );
    return result;
  }

  private static GenericItem stubItemRegistryAdapter( ItemRegistryAdapter registry, GenericItem item ) {
    when( registry.getGenericItem( KEY ) ).thenReturn( item );
    return item;
  }

  private static Runnable captureResetHook( ItemRegistryAdapter registry ) {
    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( registry ).addResetHook( captor.capture() );
    return captor.getValue();
  }
}