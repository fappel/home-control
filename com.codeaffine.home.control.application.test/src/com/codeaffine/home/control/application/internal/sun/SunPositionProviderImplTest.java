package com.codeaffine.home.control.application.internal.sun;

import static com.codeaffine.home.control.application.test.EventBusHelper.captureEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.codeaffine.home.control.application.SunPosition;
import com.codeaffine.home.control.application.SunPositionProvider;
import com.codeaffine.home.control.application.control.Event;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith( JUnitParamsRunner.class )
public class SunPositionProviderImplTest {

  private static final int MILLIS_OF_HOUR = 3600000;
  private static final int HOURS_OF_DAY = 24;

  private SunPositionProviderImpl provider;
  private EventBus eventBus;
  private Logger logger;

  public static class DateProvider {

    public static Object[] provideData()  {
      Collection<Date> result = new LinkedList<>();
      long now = System.currentTimeMillis();
      for( int i = 0; i < HOURS_OF_DAY; i++ ) {
        result.add( new Date( now + i * MILLIS_OF_HOUR ) );
      }
      return result.toArray();
    }
  }

  @Before
  public void setUp() {
    eventBus = mock( EventBus.class );
    logger = mock( Logger.class );
    provider = new SunPositionProviderImpl( eventBus, logger );
  }

  @Test
  public void initialization() {
    assertThat( provider.getStatus() ).isEqualTo( new SunPosition( 0.0, 0.0 ) );
  }

  @Test
  @Parameters( source = DateProvider.class )
  public void calculate( Date date ) {
    provider.calculate( createPointInTime( date ) );

    Optional<SunPositionProvider> actual = captureEvent( eventBus, SunPositionProvider.class );
    assertThat( actual ).hasValue( provider );
    assertThat( provider.getStatus().getZenit() )
      .isGreaterThanOrEqualTo( -90.0 )
      .isLessThanOrEqualTo( 90.0 );
    assertThat( provider.getStatus().getAzimuth() )
      .isGreaterThanOrEqualTo( 0.0 )
      .isLessThanOrEqualTo( 360.0 );
    verify( logger ).info( provider.getStatus().toString() );
  }

  @Test
  public void calculatePositionTwice() {
    Date now = new Date();

    provider.calculate( createPointInTime( now ) );
    provider.calculate( createPointInTime( now ) );

    verify( eventBus, times( 1 ) ).post( any( Event.class ) );
    verify( logger, times( 1 ) ).info( anyString() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new SunPositionProviderImpl( null, mock( Logger.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsLoggerArgument() {
    new SunPositionProviderImpl( mock( EventBus.class ), null );
  }

  private static GregorianCalendar createPointInTime( Date date ) {
    GregorianCalendar result = new GregorianCalendar();
    result.setTime( date );
    return result;
  }
}