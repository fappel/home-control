package com.codeaffine.home.control.application.internal.sun;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.e175.klaus.solarpositioning.DeltaT.estimate;
import static net.e175.klaus.solarpositioning.SPA.calculateSolarPosition;

import java.util.GregorianCalendar;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.SunPosition;
import com.codeaffine.home.control.application.SunPositionProvider;
import com.codeaffine.home.control.application.control.Event;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;

import net.e175.klaus.solarpositioning.AzimuthZenithAngle;

public class SunPositionProviderImpl implements SunPositionProvider {

  private static final double LATITUDE = 49.006362423037885; // degree
  private static final double LONGITUDE = 8.419432640075684; // degree
  private static final double ELEVATION = 115.13; // meter

  private final EventBus eventBus;
  private final Logger logger;

  private SunPosition sunPosition;

  public SunPositionProviderImpl( EventBus eventBus, Logger logger ) {
    verifyNotNull( eventBus, "eventBus" );
    verifyNotNull( logger, "logger" );

    this.sunPosition = new SunPosition( 0, 0 );
    this.logger = logger;
    this.eventBus = eventBus;
  }

  @Override
  public SunPosition getStatus() {
    return sunPosition;
  }

  @Schedule( period = 1, timeUnit = MINUTES )
  void calculate() {
    calculate( new GregorianCalendar() );
  }

  void calculate( GregorianCalendar date ) {
    AzimuthZenithAngle position = calculateSolarPosition( date, LATITUDE, LONGITUDE, ELEVATION, estimate( date ) );
    SunPosition newSunPosition = new SunPosition( 90 - position.getZenithAngle(), position.getAzimuth() );
    if( !newSunPosition.equals( sunPosition ) ) {
      sunPosition = newSunPosition;
      eventBus.post( new Event( this ) );
      logger.info( sunPosition.toString() );
    }
  }
}