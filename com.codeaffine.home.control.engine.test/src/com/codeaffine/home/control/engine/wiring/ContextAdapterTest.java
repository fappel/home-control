package com.codeaffine.home.control.engine.wiring;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ScheduledFuture;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Context.Disposable;
import com.codeaffine.home.control.engine.util.SystemExecutorImpl;
import com.codeaffine.home.control.engine.wiring.ContextAdapter;
import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.type.DecimalType;
import com.codeaffine.util.inject.Context;

public class ContextAdapterTest {

  private static final String ITEM_NAME = "itemName";
  private static final long PERIOD = 5L;

  private SystemExecutorImpl executor;
  private ContextAdapter adapter;
  private Registry registry;
  private EventBus eventBus;
  private Context context;
  private NumberItem item;

  static class Bean implements Disposable {

    private boolean disposed;

    @Schedule( period = PERIOD )
    private void method(){}

    @Observe( ITEM_NAME )
    private void onItemEvent( @SuppressWarnings("unused") ChangeEvent<NumberItem, DecimalType> event ){}

    @Override
    public void dispose() {
      disposed = true;
    }

    boolean isDisposed() {
      return disposed;
    }
  }

  @Before
  public void setUp() {
    context = new Context();
    item = mock( NumberItem.class );
    registry = stubRegistry( ITEM_NAME, item );
    context.set( Registry.class, registry );
    executor = mock( SystemExecutorImpl.class );
    eventBus = mock( EventBus.class );
    adapter = new ContextAdapter( context, registry, executor, eventBus );
  }

  @Test
  public void initialState() {
    assertThat( context.get( com.codeaffine.home.control.Context.class ) ).isSameAs( adapter );
    assertThat( context.get( EventBus.class ) ).isSameAs( eventBus );
    assertThat( context.get( SystemExecutor.class ) ).isSameAs( executor );
  }

  @Test
  @SuppressWarnings( "unchecked" )
  public void create() {
    Bean actual = adapter.create( Bean.class );

    assertThat( actual ).isNotNull();
    verify( executor ).scheduleAtFixedRate( any( Runnable.class ), eq( 0L ), eq( PERIOD ), eq( SECONDS ) );
    verify( item ).addChangeListener( any( ChangeListener.class ) );
    verify( eventBus ).register( actual );
  }

  @Test
  public void get() {
    Bean expected = new Bean();
    context.set( Bean.class, expected );

    Bean actual = adapter.get( Bean.class );

    assertThat( actual ).isSameAs( expected );
  }

  @Test
  public void set() {
    Bean expected = new Bean();

    adapter.set( Bean.class, expected );

    assertThat( context.get( Bean.class ) ).isSameAs( expected );
  }

  @Test
  public void contextDispose() {
    Bean bean = new Bean();
    adapter.set( Bean.class, bean );

    context.dispose();
    boolean actual = bean.isDisposed();

    assertThat( actual ).isTrue();
  }

  @Test
  public void contextDisposeIfSameInstanceIsAddedTwice() {
    Bean bean = spy( new Bean() );
    adapter.set( Bean.class, bean );
    adapter.set( Disposable.class, bean );

    context.dispose();
    boolean actual = bean.isDisposed();

    assertThat( actual ).isTrue();
    verify( bean ).dispose();
  }

  @Test
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void clearSchedules() {
    ScheduledFuture scheduledFuture = mock( ScheduledFuture.class );
    when( executor.scheduleAtFixedRate( any( Runnable.class ), eq( 0L ), eq( PERIOD ), eq( SECONDS ) ) )
      .thenReturn( scheduledFuture );
    adapter.create( Bean.class );

    adapter.clearSchedules();

    verify( scheduledFuture ).cancel( true );
  }

  private static Registry stubRegistry( String itemName, NumberItem numberItem ) {
    Registry result = mock( Registry.class );
    when( result.getItem( itemName, NumberItem.class ) ).thenReturn( numberItem );
    return result;
  }
}