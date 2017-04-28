package com.codeaffine.home.control.engine.adapter;

import static com.codeaffine.home.control.test.util.thread.ExecutorHelper.*;
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

import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.event.UpdateEvent;
import com.codeaffine.home.control.event.UpdateListener;
import com.codeaffine.home.control.item.ContactItem;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.OpenClosedType;
import com.codeaffine.test.util.lang.EqualsTester;

public class ItemAdapterTest {

  private static final String KEY = "key";

  private ItemAdapter<ContactItem, OpenClosedType> adapter;
  private ShutdownDispatcher shutdownDispatcher;
  private EventPublisher eventPublisher;
  private ItemRegistryAdapter registry;
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
  public void addChangeListener() {
    ChangeListener<ContactItem, OpenClosedType> expected = mock( ChangeListener.class );

    adapter.addChangeListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    assertThat( captor.getValue().getChangeListener() ).isSameAs( expected );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void addChangeListenerTwice() {
    ChangeListener<ContactItem, OpenClosedType> expected = mock( ChangeListener.class );

    adapter.addChangeListener( expected );
    adapter.addChangeListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    assertThat( captor.getValue().getChangeListener() ).isSameAs( expected );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void removeChangeListener() {
    ChangeListener<ContactItem, OpenClosedType> expected = mock( ChangeListener.class );
    adapter.addChangeListener( expected );

    adapter.removeChangeListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void removeChangeListenerTwice() {
    ChangeListener<ContactItem, OpenClosedType> expected = mock( ChangeListener.class );
    adapter.addChangeListener( expected );
    adapter.removeChangeListener( expected );

    adapter.removeChangeListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void shutdownDispatchOnChangeListener() {
    ChangeListener<ContactItem, OpenClosedType> listener = mock( ChangeListener.class );

    adapter.addChangeListener( listener );
    shutdownDispatcher.dispatch();

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
    verify( registry ).removeResetHook( resetHook );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void shutdownDispatchAfterChangeListenerRemoval() {
    ChangeListener<ContactItem, OpenClosedType> listener = mock( ChangeListener.class );

    adapter.addChangeListener( listener );
    adapter.removeChangeListener( listener );
    shutdownDispatcher.dispatch();

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
    verify( registry ).removeResetHook( resetHook );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void triggerItemChangeIfExecutorIsBlocked() {
    blockExecutor( executor );
    ChangeListener<ContactItem, OpenClosedType> listener = mock( ChangeListener.class );
    adapter.addChangeListener( listener );
    ArgumentCaptor<StateChangeListener> captor = forClass( StateChangeListener.class );
    verify( item ).addStateChangeListener( captor.capture() );

    captor.getValue().stateUpdated( item, null );

    verify( listener, never() ).itemChanged( any( ChangeEvent.class ) );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void triggerResetHookWithChangeListener() {
    ChangeListener<ContactItem, OpenClosedType> listener = mock( ChangeListener.class );
    adapter.addChangeListener( listener );
    GenericItem replacement = stubItemRegistryAdapter( registry, KEY, stubGenericItem() );

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
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void addUpdateListener() {
    UpdateListener<ContactItem, OpenClosedType> expected = mock( UpdateListener.class );

    adapter.addUpdateListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    assertThat( captor.getValue().getUpdateListener() ).isSameAs( expected );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void addUpdateListenerTwice() {
    UpdateListener<ContactItem, OpenClosedType> expected = mock( UpdateListener.class );

    adapter.addUpdateListener( expected );
    adapter.addUpdateListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    assertThat( captor.getValue().getUpdateListener() ).isSameAs( expected );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void removeUpdateListener() {
    UpdateListener<ContactItem, OpenClosedType> expected = mock( UpdateListener.class );
    adapter.addUpdateListener( expected );

    adapter.removeUpdateListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void removeUpdateListenerTwice() {
    UpdateListener<ContactItem, OpenClosedType> expected = mock( UpdateListener.class );
    adapter.addUpdateListener( expected );
    adapter.removeUpdateListener( expected );

    adapter.removeUpdateListener( expected );

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void shutdownDispatchOnUpdateListener() {
    UpdateListener<ContactItem, OpenClosedType> listener = mock( UpdateListener.class );

    adapter.addUpdateListener( listener );
    shutdownDispatcher.dispatch();

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
    verify( registry ).removeResetHook( resetHook );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void shutdownDispatchAfterUpdateListenerRemoval() {
    UpdateListener<ContactItem, OpenClosedType> listener = mock( UpdateListener.class );

    adapter.addUpdateListener( listener );
    adapter.removeUpdateListener( listener );
    shutdownDispatcher.dispatch();

    ArgumentCaptor<StateChangeAdapter> captor = forClass( StateChangeAdapter.class );
    verify( item ).addStateChangeListener( captor.capture() );
    verify( item ).removeStateChangeListener( captor.getValue() );
    verify( registry ).removeResetHook( resetHook );
  }

  @Test
  @SuppressWarnings("unchecked")
  public void triggerItemUpdateIfExecutorIsBlocked() {
    blockExecutor( executor );
    UpdateListener<ContactItem, OpenClosedType> listener = mock( UpdateListener.class );
    adapter.addUpdateListener( listener );
    ArgumentCaptor<StateChangeListener> captor = forClass( StateChangeListener.class );
    verify( item ).addStateChangeListener( captor.capture() );

    captor.getValue().stateUpdated( item, null );

    verify( listener, never() ).itemUpdated( any( UpdateEvent.class ) );
  }

  @Test
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void triggerResetHookWithUpdateListener() {
    UpdateListener<ContactItem, OpenClosedType> listener = mock( UpdateListener.class );
    adapter.addUpdateListener( listener );
    GenericItem replacement = stubItemRegistryAdapter( registry, KEY, stubGenericItem() );

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
  public void updateStatusInternal() {
    adapter.updateStatusInternal( OpenClosedType.OPEN );

    ArgumentCaptor<Event> captor = forClass( Event.class );
    verify( eventPublisher ).post( captor.capture() );
    assertThat( captor.getValue().getPayload() ).isEqualTo( "{\"type\":\"OpenClosedType\",\"value\":\"OPEN\"}" );
  }

  @Test(expected = IllegalArgumentException.class)
  public void updateStatusInternalWithNullArgument() {
    adapter.updateStatusInternal( null );
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void equalsAndHashcode() {
    String key = "other";
    GenericItem otherItem = mock( GenericItem.class );
    stubItemRegistryAdapter( registry, key, otherItem );
    ItemAdapter differentAdapter1
      = new ItemAdapter( KEY, registry, eventPublisher, shutdownDispatcher, executor, OnOffType.class );
    differentAdapter1.initialize();
    ItemAdapter differentAdapter2
      = new ItemAdapter( key, registry, eventPublisher, shutdownDispatcher, executor, OpenClosedType.class );
    differentAdapter2.initialize();
    ItemAdapter equalAdapter
      = new ItemAdapter( KEY, registry, eventPublisher, shutdownDispatcher, executor, OpenClosedType.class );
    equalAdapter.initialize();

    EqualsTester<ItemAdapter<ContactItem, OpenClosedType>> tester = EqualsTester.newInstance( adapter );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertEqual( adapter, equalAdapter );
    tester.assertNotEqual( adapter, differentAdapter1 );
    tester.assertNotEqual( adapter, differentAdapter2 );
  }

  private static GenericItem stubGenericItem() {
    GenericItem result = mock( GenericItem.class );
    when( result.getName() ).thenReturn( "itemName" );
    return result;
  }

  private static ItemRegistryAdapter stubItemRegistryAdapter( GenericItem item ) {
    ItemRegistryAdapter result = mock( ItemRegistryAdapter.class );
    stubItemRegistryAdapter( result, KEY, item );
    return result;
  }

  private static GenericItem stubItemRegistryAdapter( ItemRegistryAdapter registry, String key, GenericItem item ) {
    when( registry.getGenericItem( key ) ).thenReturn( item );
    return item;
  }

  private static Runnable captureResetHook( ItemRegistryAdapter registry ) {
    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( registry ).addResetHook( captor.capture() );
    return captor.getValue();
  }
}