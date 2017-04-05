package com.codeaffine.home.control.status.internal.computer;

import static com.codeaffine.home.control.status.internal.computer.ComputerStatusSupplierImpl.MIN_IDLE_TIME_IN_SECONDS;
import static com.codeaffine.home.control.status.internal.computer.Messages.INFO_COMPUTER_ACTIVITY_STATUS;
import static com.codeaffine.home.control.status.type.OnOff.*;
import static com.codeaffine.home.control.test.util.event.EventBusHelper.captureEvent;
import static com.codeaffine.home.control.test.util.logger.LoggerHelper.captureSingleInfoArgument;
import static com.codeaffine.home.control.type.DecimalType.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.UpdateEvent;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.internal.computer.ComputerStatusSupplierImpl;
import com.codeaffine.home.control.status.supplier.ComputerStatusSupplier;
import com.codeaffine.home.control.status.type.OnOff;
import com.codeaffine.home.control.type.DecimalType;

public class ComputerStatusSupplierImplTest {

  private static final DecimalType MAX_ACTIVATION_TIME = new DecimalType( MIN_IDLE_TIME_IN_SECONDS.longValue() - 1 );

  private ComputerStatusSupplierImpl statusSupplier;
  private EventBus eventBus;
  private Logger logger;

  @Before
  public void setUp() {
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    statusSupplier = new ComputerStatusSupplierImpl( eventBus, logger );
  }

  @Test
  public void initialStatus() {
    assertThat( statusSupplier.getStatus() ).isSameAs( OFF );
  }

  @Test
  public void onUpdateAboveActivationThreshold() {
    statusSupplier.onUpdate( stubEvent( ZERO ) );
    reset( eventBus, logger );

    statusSupplier.onUpdate( stubEvent( MIN_IDLE_TIME_IN_SECONDS ) );
    OnOff actual = statusSupplier.getStatus();

    assertThat( actual ).isSameAs( OFF );
    assertThat( captureInfoArgument() ).isSameAs( OFF );
    assertThat( captureEvent( eventBus, ComputerStatusSupplier.class ) ).hasValue( statusSupplier );
  }

  @Test
  @SuppressWarnings( "cast" )
  public void onUpdateAboveActivationThresholdWithoutStatusChange() {
    statusSupplier.onUpdate( stubEvent( new DecimalType( MIN_IDLE_TIME_IN_SECONDS.longValue() + 1 ) ) );
    reset( eventBus, logger );

    statusSupplier.onUpdate( stubEvent( MIN_IDLE_TIME_IN_SECONDS ) );
    OnOff actual = statusSupplier.getStatus();

    assertThat( actual ).isSameAs( OFF );
    verify( logger, never() ).info( eq( INFO_COMPUTER_ACTIVITY_STATUS ), ( Object )anyObject() );
    verify( eventBus, never() ).post( any() );
  }

  @Test
  public void onUpdateBelowActivationThreshold() {
    statusSupplier.onUpdate( stubEvent( MAX_ACTIVATION_TIME ) );
    OnOff actual = statusSupplier.getStatus();

    assertThat( actual ).isSameAs( ON );
    assertThat( captureInfoArgument() ).isSameAs( ON );
    assertThat( captureEvent( eventBus, ComputerStatusSupplier.class ) ).hasValue( statusSupplier );
  }

  @Test
  @SuppressWarnings( "cast" )
  public void onUpdateBelowActivationThresholdWithoutStatusChange() {
    statusSupplier.onUpdate( stubEvent( ZERO ) );
    reset( eventBus, logger );

    statusSupplier.onUpdate( stubEvent( MAX_ACTIVATION_TIME ) );
    OnOff actual = statusSupplier.getStatus();

    assertThat( actual ).isSameAs( ON );
    verify( logger, never() ).info( eq( INFO_COMPUTER_ACTIVITY_STATUS ), ( Object )anyObject() );
    verify( eventBus, never() ).post( any() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new ComputerStatusSupplierImpl( null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new ComputerStatusSupplierImpl( eventBus, null );
  }

  private Object captureInfoArgument() {
    return captureSingleInfoArgument( logger, INFO_COMPUTER_ACTIVITY_STATUS );
  }

  @SuppressWarnings("unchecked")
  private static UpdateEvent<NumberItem, DecimalType> stubEvent( DecimalType value ) {
    UpdateEvent<NumberItem, DecimalType> result = mock( UpdateEvent.class );
    when( result.getUpdatedStatus() ).thenReturn( Optional.ofNullable( value ) );
    return result;
  }
}