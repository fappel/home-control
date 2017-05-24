package com.codeaffine.home.control.status.internal.sun;

import static com.codeaffine.home.control.status.internal.sun.Messages.SUN_POSITION_STATUS_INFO_PATTERN;
import static com.codeaffine.home.control.test.util.event.EventBusHelper.captureEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusEvent;
import com.codeaffine.home.control.status.supplier.SunPosition;
import com.codeaffine.home.control.status.supplier.SunPositionSupplier;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith( JUnitParamsRunner.class )
public class SunPositionSupplierImplTest {

  private static final int MILLIS_OF_HOUR = 3600000;
  private static final int HOURS_OF_DAY = 24;

  private SunPositionSupplierImpl supplier;
  private EventBus eventBus;
  private Logger logger;

  public static Object[] provideData()  {
    Collection<Date> result = new LinkedList<>();
    long now = System.currentTimeMillis();
    for( int i = 0; i < HOURS_OF_DAY; i++ ) {
      result.add( new Date( now + i * MILLIS_OF_HOUR ) );
    }
    return result.toArray();
  }

  @Before
  public void setUp() {
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    supplier = new SunPositionSupplierImpl( eventBus, logger );
  }

  @Test
  public void initialization() {
    assertThat( supplier.getStatus() ).isEqualTo( new SunPosition( 0.0, 0.0 ) );
  }

  @Test
  @Parameters( source = SunPositionSupplierImplTest.class )
  public void calculate( Date date ) {
    supplier.calculate( createPointInTime( date ) );
    Optional<SunPositionSupplier> actual = captureEvent( eventBus, SunPositionSupplier.class );

    assertThat( actual ).hasValue( supplier );
    assertThat( supplier.getStatus().getZenit() )
      .isGreaterThanOrEqualTo( -90.0 )
      .isLessThanOrEqualTo( 90.0 );
    assertThat( supplier.getStatus().getAzimuth() )
      .isGreaterThanOrEqualTo( 0.0 )
      .isLessThanOrEqualTo( 360.0 );
    verify( logger ).debug( SUN_POSITION_STATUS_INFO_PATTERN, supplier.getStatus() );
  }

  @Test
  @SuppressWarnings("cast")
  public void calculatePositionTwice() {
    Date now = new Date();

    supplier.calculate( createPointInTime( now ) );
    supplier.calculate( createPointInTime( now ) );

    verify( eventBus, times( 1 ) ).post( any( StatusEvent.class ) );
    verify( logger, times( 1 ) ).debug( eq( SUN_POSITION_STATUS_INFO_PATTERN ), ( Object )anyObject() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new SunPositionSupplierImpl( null, mock( Logger.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new SunPositionSupplierImpl( mock( EventBus.class ), null );
  }

  private static GregorianCalendar createPointInTime( Date date ) {
    GregorianCalendar result = new GregorianCalendar();
    result.setTime( date );
    return result;
  }
}