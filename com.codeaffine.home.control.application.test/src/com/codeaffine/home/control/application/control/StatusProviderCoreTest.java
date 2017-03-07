package com.codeaffine.home.control.application.control;

import static com.codeaffine.home.control.application.test.MyStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.application.test.MyStatus;
import com.codeaffine.home.control.application.test.MyStatusProvider;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;

public class StatusProviderCoreTest {

  private static final String STATUS_INFO_PATTERN = "%s";

  private StatusProviderCore<MyStatus> core;
  private MyStatusProvider statusProvider;
  private EventBus eventBus;
  private Logger logger;

  @Before
  public void setUp() {
    eventBus = mock( EventBus.class );
    statusProvider = new MyStatusProvider();
    logger = mock( Logger.class );
    core = new StatusProviderCore<>( eventBus, ONE, statusProvider, logger );
  }

  @Test
  public void initialization() {
    MyStatus actual = core.getStatus();

    assertThat( actual ).isSameAs( ONE );
  }

  @Test
  public void updateStatus() {
    core.updateStatus( () -> TWO, STATUS_INFO_PATTERN );
    MyStatus actual = core.getStatus();

    ArgumentCaptor<StatusEvent> captor = forClass( StatusEvent.class );
    assertThat( actual ).isSameAs( TWO );
    verify( eventBus ).post( captor.capture() );
    assertThat( captor.getValue().getSource( MyStatusProvider.class ) ).hasValue( statusProvider );
    verify( logger ).info( STATUS_INFO_PATTERN, TWO );
  }

  @Test
  public void updateStatusTwiceWithSameNewStatus() {
    core.updateStatus( () -> TWO, STATUS_INFO_PATTERN );
    core.updateStatus( () -> TWO, STATUS_INFO_PATTERN );
    MyStatus actual = core.getStatus();

    ArgumentCaptor<StatusEvent> captor = forClass( StatusEvent.class );
    assertThat( actual ).isSameAs( TWO );
    verify( eventBus ).post( captor.capture() );
    assertThat( captor.getValue().getSource( MyStatusProvider.class ) ).hasValue( statusProvider );
    verify( logger ).info( STATUS_INFO_PATTERN, TWO );
  }

  @Test
  public void updateStatusWithInfoArgumentProvider() {
    core.updateStatus( () -> TWO, STATUS_INFO_PATTERN, status -> TWO.toString().toLowerCase() );
    MyStatus actual = core.getStatus();

    ArgumentCaptor<StatusEvent> captor = forClass( StatusEvent.class );
    assertThat( actual ).isSameAs( TWO );
    verify( eventBus ).post( captor.capture() );
    assertThat( captor.getValue().getSource( MyStatusProvider.class ) ).hasValue( statusProvider );
    verify( logger ).info( STATUS_INFO_PATTERN, TWO.toString().toLowerCase() );
  }

  @Test
  public void updateStatusWithInfoArgumentProviderTwiceWithSameNewStatus() {
    core.updateStatus( () -> TWO, STATUS_INFO_PATTERN, status -> TWO.toString().toLowerCase() );
    core.updateStatus( () -> TWO, STATUS_INFO_PATTERN, status -> TWO.toString().toLowerCase() );
    MyStatus actual = core.getStatus();

    ArgumentCaptor<StatusEvent> captor = forClass( StatusEvent.class );
    assertThat( actual ).isSameAs( TWO );
    verify( eventBus ).post( captor.capture() );
    assertThat( captor.getValue().getSource( MyStatusProvider.class ) ).hasValue( statusProvider );
    verify( logger ).info( STATUS_INFO_PATTERN, TWO.toString().toLowerCase() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new StatusProviderCore<>( null, ONE, statusProvider, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsInitalStatusArgument() {
    new StatusProviderCore<>( eventBus, null, statusProvider, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsStatusProviderArgument() {
    new StatusProviderCore<>( eventBus, ONE, null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new StatusProviderCore<>( eventBus, ONE, statusProvider, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void updateStatusWithNullAsNewStatusSupplierArgument() {
    core.updateStatus( null, STATUS_INFO_PATTERN );
  }

  @Test( expected = IllegalArgumentException.class )
  public void updateStatusWithNullAsStatusInfoPattern() {
    core.updateStatus( () -> TWO, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void updateStatusWithInfoArgumentProviderWithNullAsNewStatusSupplierArgument() {
    core.updateStatus( null, STATUS_INFO_PATTERN, status -> status );
  }

  @Test( expected = IllegalArgumentException.class )
  public void updateStatusWithInfoArgumentProviderWithNullAsStatusInfoPatternArgument() {
    core.updateStatus( () -> TWO, null, status -> status );
  }

  @Test( expected = IllegalArgumentException.class )
  public void updateStatusWithInfoArgumentProviderWithNullAsStatusInfoArgumentProviderArgument() {
    core.updateStatus( () -> TWO, STATUS_INFO_PATTERN, null );
  }
}