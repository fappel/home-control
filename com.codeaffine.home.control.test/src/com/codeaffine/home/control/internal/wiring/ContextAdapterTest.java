package com.codeaffine.home.control.internal.wiring;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ScheduledFuture;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.internal.util.SystemExecutor;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.type.DecimalType;
import com.codeaffine.util.inject.Context;

public class ContextAdapterTest {

  private static final String ITEM_NAME = "itemName";
  private static final long PERIOD = 5L;

  private SystemExecutor executor;
  private ContextAdapter adapter;
  private Registry registry;
  private Context context;
  private NumberItem item;

  static class Bean {

    @Schedule( period = PERIOD )
    private void method(){}

    @Observe( ITEM_NAME )
    private void onEvent( @SuppressWarnings("unused") ChangeEvent<NumberItem, DecimalType> event ){}
  }


  @Before
  public void setUp() {
    context = new Context();
    item = mock( NumberItem.class );
    registry = stubRegistry( ITEM_NAME, item );
    context.set( Registry.class, registry );
    executor = mock( SystemExecutor.class );
    adapter = new ContextAdapter( context, registry, executor );
  }

  @Test
  public void initialState() {
    com.codeaffine.home.control.Context actual = context.get( com.codeaffine.home.control.Context.class );

    assertThat( actual ).isSameAs( adapter );
  }

  @Test
  @SuppressWarnings( "unchecked" )
  public void create() {
    Bean actual = adapter.create( Bean.class );

    assertThat( actual ).isNotNull();
    verify( executor ).scheduleAtFixedRate( any( Runnable.class ), eq( 0L ), eq( PERIOD ), eq( SECONDS ) );
    verify( item ).addChangeListener( any( ChangeListener.class ) );
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