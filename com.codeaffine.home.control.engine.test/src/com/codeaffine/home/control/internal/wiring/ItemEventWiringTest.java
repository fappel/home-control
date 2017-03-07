package com.codeaffine.home.control.internal.wiring;

import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.Registry;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.event.Observe;
import com.codeaffine.home.control.event.UpdateEvent;
import com.codeaffine.home.control.event.UpdateListener;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.type.DecimalType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ItemEventWiringTest {

  private static final String ITEM_NAME = "itemName";

  private ItemEventWiring eventWiring;
  private Registry registry;

  static class Bean {

    UpdateEvent<NumberItem, DecimalType> statusUpdatedEvent;
    ChangeEvent<NumberItem, DecimalType> statusChangedEvent;

    @Observe( ITEM_NAME )
    void onChange( ChangeEvent<NumberItem, DecimalType> event ) {
      this.statusChangedEvent = event;
    }

    @Observe( ITEM_NAME )
    void onUpdate( UpdateEvent<NumberItem, DecimalType> event ) {
      this.statusUpdatedEvent = event;
    }
  }

  static class BeanWithAdditionalEventMethodParameter {

    @SuppressWarnings("unused")
    @Observe( ITEM_NAME )
    void onChange( ChangeEvent<NumberItem, DecimalType> event, Runnable invalidParameter ) {}
  }

  static class BeanWithWrongEventMethodParameterType {

    @SuppressWarnings("unused")
    @Observe( ITEM_NAME )
    void onChange( Runnable invalidParameter ) {}
  }

  @Before
  public void setUp() {
    registry = mock( Registry.class );
    eventWiring = new ItemEventWiring( registry );
  }

  @Test
  public void wireWithItemChange() {
    NumberItem item = mock( NumberItem.class );
    when( registry.getItem( ITEM_NAME, NumberItem.class ) ).thenReturn( item );
    Bean bean = new Bean();

    eventWiring.wire( bean );
    triggerItemChange( item );

    assertThat( bean.statusChangedEvent ).isNotNull();
    assertThat( bean.statusUpdatedEvent ).isNull();
  }

  @Test
  public void wireWithItemUpdate() {
    NumberItem item = mock( NumberItem.class );
    when( registry.getItem( ITEM_NAME, NumberItem.class ) ).thenReturn( item );
    Bean bean = new Bean();

    eventWiring.wire( bean );
    triggerItemUpdate( item );

    assertThat( bean.statusChangedEvent ).isNull();
    assertThat( bean.statusUpdatedEvent ).isNotNull();
  }

  @Test
  public void wireWithAdditionalEventMethodParameter() {
    Throwable actual = thrownBy( () -> eventWiring.wire( new BeanWithAdditionalEventMethodParameter() ) );

    assertThat( actual ).isInstanceOf( WiringException.class );
  }

  @Test
  public void wireWithWrongEventMethodParameter() {
    Throwable actual = thrownBy( () -> eventWiring.wire( new BeanWithWrongEventMethodParameterType() ) );

    assertThat( actual ).isInstanceOf( WiringException.class );
  }

  private static void triggerItemChange( NumberItem item ) {
    ArgumentCaptor<ChangeListener> captor = forClass( ChangeListener.class );
    verify( item ).addChangeListener( captor.capture() );
    ChangeListener listener = captor.getValue();
    listener.itemChanged( mock( ChangeEvent.class ) );
  }

  private static void triggerItemUpdate( NumberItem item ) {
    ArgumentCaptor<UpdateListener> captor = forClass( UpdateListener.class );
    verify( item ).addUpdateListener( captor.capture() );
    UpdateListener listener = captor.getValue();
    listener.itemUpdated( mock( UpdateEvent.class ) );
  }
}
