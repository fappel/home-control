package com.codeaffine.home.control.application.internal.sun;

import static com.codeaffine.home.control.application.internal.sun.Messages.SUN_POSITION_STATUS_INFO_PATTERN;
import static java.util.concurrent.TimeUnit.MINUTES;
import static net.e175.klaus.solarpositioning.DeltaT.estimate;
import static net.e175.klaus.solarpositioning.SPA.calculateSolarPosition;

import java.util.GregorianCalendar;

import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.application.status.SunPosition;
import com.codeaffine.home.control.application.status.SunPositionProvider;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.logger.Logger;
import com.codeaffine.home.control.status.StatusProviderCore;

import net.e175.klaus.solarpositioning.AzimuthZenithAngle;

public class SunPositionProviderImpl implements SunPositionProvider {

  private static final double LATITUDE = 49.006362423037885; // degree
  private static final double LONGITUDE = 8.419432640075684; // degree
  private static final double ELEVATION = 115.13; // meter

  private final StatusProviderCore<SunPosition> statusProviderCore;

  public SunPositionProviderImpl( EventBus eventBus, Logger logger ) {
    statusProviderCore = new StatusProviderCore<>( eventBus, new SunPosition( 0, 0 ), this, logger );
  }

  @Override
  public SunPosition getStatus() {
    return statusProviderCore.getStatus();
  }

  @Schedule( period = 1, timeUnit = MINUTES )
  void calculate() {
    calculate( new GregorianCalendar() );
  }

  void calculate( GregorianCalendar date ) {
    statusProviderCore.updateStatus( () -> doCalculate( date ), SUN_POSITION_STATUS_INFO_PATTERN );
  }

  private static SunPosition doCalculate( GregorianCalendar date ) {
    AzimuthZenithAngle position = calculateSolarPosition( date, LATITUDE, LONGITUDE, ELEVATION, estimate( date ) );
    return new SunPosition( 90 - position.getZenithAngle(), position.getAzimuth() );
  }
}