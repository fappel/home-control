package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.scene.RootMath.nthRootOf;
import static com.codeaffine.home.control.application.type.Percent.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toSet;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;

import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.application.status.Activity;
import com.codeaffine.home.control.application.status.ActivityProvider;
import com.codeaffine.home.control.application.type.Percent;

public class ActivityMath {

  private static final BigDecimal MINIMUM_IDENTITY = toBigDecimal( P_100.intValue() );
  private static final BigDecimal MAXIMUM_IDENTITY = toBigDecimal( P_000.intValue() );
  private static final BigDecimal MULTIPLICATION_IDENTIY = toBigDecimal( 1 );
  private static final BigDecimal SUM_IDENTITY = toBigDecimal( 0 );

  private final ActivationProvider activationProvider;
  private final ActivityProvider activityProvider;

  public ActivityMath( ActivityProvider activityProvider, ActivationProvider activationProvider ) {
    verifyNotNull( activationProvider, "activationProvider" );
    verifyNotNull( activityProvider, "activityProvider" );

    this.activationProvider = activationProvider;
    this.activityProvider = activityProvider;
  }

  public Optional<Percent> calculateArithmeticMeanOfPathActivityFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    if( isPresent( sectionDefinition ) ) {
      return percentOptionalOf( calculateArithmeticMeanFor( sectionDefinition ) );
    }
    return empty();
  }

  public Optional<Percent> calculateGeometricMeanOfPathActivityFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    if( isPresent( sectionDefinition ) ) {
      return percentOptionalOf( calculateGeometricMeanFor( sectionDefinition ) );
    }
    return empty();
  }

  public Optional<Percent> calculateMinimumOfPathActivityFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    if( isPresent( sectionDefinition ) ) {
      return percentOptionalOf( calculateMinimumFor( sectionDefinition ) );
    }
    return empty();
  }

  public Optional<Percent> calculateMaximumOfPathActivityFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    if( isPresent( sectionDefinition ) ) {
      return percentOptionalOf( calculateMaximumFor( sectionDefinition ) );
    }
    return empty();
  }

  private boolean isPresent( SectionDefinition sectionDefinition ) {
    return activationProvider.getStatus().getZone( sectionDefinition ).isPresent();
  }

  private static Optional<Percent> percentOptionalOf( int value ) {
    return Optional.of( Percent.valueOf( value ) );
  }

  private int calculateArithmeticMeanFor( SectionDefinition sectionDefinition ) {
    BigDecimal sum = collectZoneActivities( sectionDefinition )
      .stream()
      .reduce( SUM_IDENTITY, ( value, percent ) -> sum( value, percent ) );
    return sum.divide( toBigDecimal( calculateZoneActivityCount( sectionDefinition ) ) ).intValue();
  }

  private int calculateGeometricMeanFor( SectionDefinition sectionDefinition ) {
    BigDecimal product = collectZoneActivities( sectionDefinition )
      .stream()
      .reduce( MULTIPLICATION_IDENTIY, ( value, percent ) -> multiply( value, percent ) );
    return nthRootOf( product, calculateZoneActivityCount( sectionDefinition ), 0 ).intValue();
  }

  private int calculateZoneActivityCount( SectionDefinition sectionDefinition ) {
    return collectZoneActivities( sectionDefinition ).size();
  }

  private int calculateMinimumFor( SectionDefinition sectionDefinition ) {
    return collectZoneActivities( sectionDefinition )
     .stream()
     .reduce( MINIMUM_IDENTITY, ( value, percent ) -> minimum( value, percent ) )
     .intValue();
  }

  private int calculateMaximumFor( SectionDefinition sectionDefinition ) {
    return collectZoneActivities( sectionDefinition )
      .stream()
      .reduce( MAXIMUM_IDENTITY, ( value, percent ) -> maximum( value, percent ) )
      .intValue();
  }

  private Set<BigDecimal> collectZoneActivities( SectionDefinition sectionDefinition ) {
    Activity activity = activityProvider.getStatus();
    return activationProvider
      .getStatus()
      .getZone( sectionDefinition )
      .get()
      .getZonesOfRelatedPaths()
      .stream()
      .filter( zone -> findSectionActivity( activity, zone ).isPresent() )
      .map( zone -> toBigDecimal( getSectionActivity( activity, zone ).intValue() ) )
      .filter( zoneActivity -> zoneActivity.intValue() > 0 )
      .collect( toSet() );
  }

  private static Optional<Percent> findSectionActivity( Activity activity, Zone zone ) {
    return activity.getSectionActivity( ( SectionDefinition )zone.getZoneEntity().getDefinition() );
  }

  private static BigDecimal getSectionActivity( Activity activity, Zone zone ) {
    return toBigDecimal( findSectionActivity( activity, zone ).get().intValue() );
  }

  private static BigDecimal sum( BigDecimal value1, BigDecimal value2 ) {
    return value1.add( value2 );
  }

  private static BigDecimal multiply( BigDecimal value1, BigDecimal value2 ) {
    return value1.multiply( value2 );
  }

  private static BigDecimal maximum( BigDecimal value1, BigDecimal value2 ) {
    return value1.compareTo( value2 ) > 0 ? value1 : value2;
  }

  private static BigDecimal minimum( BigDecimal value1, BigDecimal value2 ) {
    return value1.compareTo( value2 ) < 0 ? value1 : value2;
  }

  private static BigDecimal toBigDecimal( int value ) {
    return BigDecimal.valueOf( value );
  }
}