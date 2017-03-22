package com.codeaffine.home.control.application.internal.computer;

import static com.codeaffine.home.control.application.internal.computer.ComputerStatusProviderImpl.MIN_IDLE_TIME;
import static com.codeaffine.home.control.application.internal.computer.Messages.INFO_COMPUTER_ACTIVITY_STATUS;
import static com.codeaffine.home.control.application.test.EventBusHelper.captureEvent;
import static com.codeaffine.home.control.application.test.LoggerHelper.captureSingleInfoArgument;
import static com.codeaffine.home.control.application.type.OnOff.*;
import static com.codeaffine.home.control.type.DecimalType.ZERO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.status.ComputerStatusProvider;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.event.UpdateEvent;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.type.DecimalType;

public class ComputerStatusProviderImplTest {

  private static final DecimalType MAX_ACTIVATION_TIME = new DecimalType( MIN_IDLE_TIME.longValue() - 1 );

  private ComputerStatusProviderImpl statusProvider;
  private EventBus eventBus;
  private Logger logger;

  @Before
  public void setUp() {
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    statusProvider = new ComputerStatusProviderImpl( eventBus, logger );
  }

  @Test
  public void initialStatus() {
    assertThat( statusProvider.getStatus() ).isSameAs( OFF );
  }

  @Test
  public void onUpdateAboveActivationThreshold() {
    statusProvider.onUpdate( stubEvent( ZERO ) );
    reset( eventBus, logger );

    statusProvider.onUpdate( stubEvent( MIN_IDLE_TIME ) );
    OnOff actual = statusProvider.getStatus();

    assertThat( actual ).isSameAs( OFF );
    assertThat( captureInfoArgument() ).isSameAs( OFF );
    assertThat( captureEvent( eventBus, ComputerStatusProvider.class ) ).hasValue( statusProvider );
  }

  @Test
  @SuppressWarnings( "cast" )
  public void onUpdateAboveActivationThresholdWithoutStatusChange() {
    statusProvider.onUpdate( stubEvent( new DecimalType( MIN_IDLE_TIME.longValue() + 1 ) ) );
    reset( eventBus, logger );

    statusProvider.onUpdate( stubEvent( MIN_IDLE_TIME ) );
    OnOff actual = statusProvider.getStatus();

    assertThat( actual ).isSameAs( OFF );
    verify( logger, never() ).info( eq( INFO_COMPUTER_ACTIVITY_STATUS ), ( Object )anyObject() );
    verify( eventBus, never() ).post( any() );
  }

  @Test
  public void onUpdateBelowActivationThreshold() {
    statusProvider.onUpdate( stubEvent( MAX_ACTIVATION_TIME ) );
    OnOff actual = statusProvider.getStatus();

    assertThat( actual ).isSameAs( ON );
    assertThat( captureInfoArgument() ).isSameAs( ON );
    assertThat( captureEvent( eventBus, ComputerStatusProvider.class ) ).hasValue( statusProvider );
  }

  @Test
  @SuppressWarnings( "cast" )
  public void onUpdateBelowActivationThresholdWithoutStatusChange() {
    statusProvider.onUpdate( stubEvent( ZERO ) );
    reset( eventBus, logger );

    statusProvider.onUpdate( stubEvent( MAX_ACTIVATION_TIME ) );
    OnOff actual = statusProvider.getStatus();

    assertThat( actual ).isSameAs( ON );
    verify( logger, never() ).info( eq( INFO_COMPUTER_ACTIVITY_STATUS ), ( Object )anyObject() );
    verify( eventBus, never() ).post( any() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new ComputerStatusProviderImpl( null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new ComputerStatusProviderImpl( eventBus, null );
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