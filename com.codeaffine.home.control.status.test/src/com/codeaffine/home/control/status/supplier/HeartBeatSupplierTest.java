package com.codeaffine.home.control.status.supplier;

import static com.codeaffine.home.control.status.supplier.Messages.STATUS_INFO_HEARTBEAT;
import static com.codeaffine.home.control.test.util.event.EventBusHelper.captureEvent;
import static com.codeaffine.home.control.test.util.logger.LoggerHelper.captureSingleDebugArgument;
import static com.codeaffine.home.control.test.util.thread.ThreadHelper.sleep;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;

public class HeartBeatSupplierTest {

  private HeartBeatSupplier supplier;
  private EventBus eventBus;
  private Logger logger;

  @Before
  public void setUp() {
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    supplier = new HeartBeatSupplier( eventBus, logger );
  }

  @Test
  public void pulse() {
    sleep( 10 );

    supplier.pulse();

    assertThat( captureEvent( eventBus, HeartBeatSupplier.class ) ).hasValue( supplier );
    assertThat( captureSingleDebugArgument( logger, STATUS_INFO_HEARTBEAT ) ).isInstanceOf( LocalDateTime.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new HeartBeatSupplier( null, logger );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new HeartBeatSupplier( eventBus, null );
  }
}