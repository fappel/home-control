package com.codeaffine.home.control.status.util;

import static com.codeaffine.home.control.status.type.Percent.*;
import static com.codeaffine.home.control.status.util.RootMath.nthRootOf;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.math.RoundingMode.HALF_UP;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toSet;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.Activation.Zone;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.Activity;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.type.Percent;

class ActivityMath {

  private static final BigDecimal MINIMUM_IDENTITY = toBigDecimal( P_100.intValue() );
  private static final BigDecimal MAXIMUM_IDENTITY = toBigDecimal( P_000.intValue() );
  private static final BigDecimal MULTIPLICATION_IDENTIY = toBigDecimal( 1 );
  private static final BigDecimal SUM_IDENTITY = toBigDecimal( 0 );

  private final BiFunction<Activity, Zone, Optional<Percent>> allocationFinder;
  private final BiFunction<Activity, Zone, Optional<Percent>> activityFinder;
  private final ActivationSupplier activationSupplier;
  private final ActivitySupplier activitySupplier;

  ActivityMath( ActivitySupplier activitySupplier, ActivationSupplier activationSupplier ) {
    verifyNotNull( activationSupplier, "activationSupplier" );
    verifyNotNull( activitySupplier, "activitySupplier" );

    this.allocationFinder = ( activity, zone ) -> findSectionAllocation( activity, zone );
    this.activityFinder = ( activity, zone ) -> findSectionActivity( activity, zone );
    this.activationSupplier = activationSupplier;
    this.activitySupplier = activitySupplier;
  }

  public Optional<Percent> calculateArithmeticMeanOfPathActivityFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    return performFor( sectionDefinition, section -> calculateArithmeticMean( section, activityFinder ) );
  }

  public Optional<Percent> calculateArithmeticMeanOfPathAllocationFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    return performFor( sectionDefinition, section -> calculateArithmeticMean( section, allocationFinder ) );
  }

  public Optional<Percent> calculateGeometricMeanOfPathActivityFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    return performFor( sectionDefinition, section -> calculateGeometricMean( section, activityFinder ) );
  }

  public Optional<Percent> calculateGeometricMeanOfPathAllocationFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    return performFor( sectionDefinition, section -> calculateGeometricMean( section, allocationFinder ) );
  }

  public Optional<Percent> calculateMinimumOfPathActivityFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    return performFor( sectionDefinition, section -> calculateMinimum( section, activityFinder ) );
  }

  public Optional<Percent> calculateMinimumOfPathAllocationFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    return performFor( sectionDefinition, section -> calculateMinimum( section, allocationFinder ) );
  }

  public Optional<Percent> calculateMaximumOfPathActivityFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    return performFor( sectionDefinition, section -> calculateMaximum( section, activityFinder ) );
  }

  public Optional<Percent> calculateMaximumOfPathAllocationFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    return performFor( sectionDefinition, section -> calculateMaximum( section, allocationFinder ) );
  }

  private boolean isPresent( SectionDefinition sectionDefinition ) {
    return activationSupplier.getStatus().getZone( sectionDefinition ).isPresent();
  }

  private static Optional<Percent> percentOptionalOf( Integer value) {
    return Optional.of( Percent.valueOf( value.intValue() ) );
  }

  private Integer calculateArithmeticMean(
    SectionDefinition sectionDefinition, BiFunction<Activity, Zone, Optional<Percent>> rateFinder )
  {
    int activationCount = countNonZeroActivityZones( sectionDefinition, rateFinder );
    if( activationCount != 0 ) {
      return calculateArithmeticMeanOfNonZeroActivations( sectionDefinition, rateFinder, activationCount );
    }
    return Integer.valueOf( 0 );
  }

  private Integer calculateArithmeticMeanOfNonZeroActivations(
    SectionDefinition sectionDefinition, BiFunction<Activity, Zone, Optional<Percent>> rateFinder, int activationCount )
  {
    BigDecimal sum = collectZoneActivities( sectionDefinition, rateFinder )
      .stream()
      .reduce( SUM_IDENTITY, ( value, percent ) -> sum( value, percent ) );
    return toInteger( sum.divide( toBigDecimal( activationCount ), HALF_UP ).intValue() );
  }

  private Integer calculateGeometricMean(
    SectionDefinition sectionDefinition, BiFunction<Activity, Zone, Optional<Percent>> rateFinder )
  {
    BigDecimal product = collectZoneActivities( sectionDefinition, rateFinder )
      .stream()
      .reduce( MULTIPLICATION_IDENTIY, ( value, percent ) -> multiply( value, percent ) );
    return toInteger( nthRootOf( product, countNonZeroActivityZones( sectionDefinition, rateFinder ), 0 ).intValue() );
  }

  private int countNonZeroActivityZones(
    SectionDefinition sectionDefinition, BiFunction<Activity, Zone, Optional<Percent>> rateFinder )
  {
    return collectZoneActivities( sectionDefinition, rateFinder ).size();
  }

  private Integer calculateMinimum(
    SectionDefinition sectionDefinition, BiFunction<Activity, Zone, Optional<Percent>> rateFinder )
  {
    return toInteger( collectZoneActivities( sectionDefinition, rateFinder )
     .stream()
     .reduce( MINIMUM_IDENTITY, ( value, percent ) -> minimum( value, percent ) )
     .intValue() );
  }

  private Integer calculateMaximum(
    SectionDefinition sectionDefinition, BiFunction<Activity, Zone, Optional<Percent>> rateFinder )
  {
    return toInteger( collectZoneActivities( sectionDefinition, rateFinder )
      .stream()
      .reduce( MAXIMUM_IDENTITY, ( value, percent ) -> maximum( value, percent ) )
      .intValue() );
  }

  private Optional<Percent> performFor(
    SectionDefinition sectionDefinition, Function<SectionDefinition, Integer> calculator )
  {
    if( isPresent( sectionDefinition ) ) {
      return percentOptionalOf( calculator.apply( sectionDefinition ) );
    }
    return empty();
  }

  private Set<BigDecimal> collectZoneActivities(
    SectionDefinition sectionDefinition, BiFunction<Activity, Zone, Optional<Percent>> rateFinder )
  {
    Activity activity = activitySupplier.getStatus();
    return activationSupplier
      .getStatus()
      .getZone( sectionDefinition )
      .get()
      .getZonesOfRelatedPaths()
      .stream()
      .filter( zone -> rateFinder.apply( activity, zone ).isPresent() )
      .map( zone -> toBigDecimal( rateFinder.apply( activity, zone ).get().intValue() ) )
      .filter( zoneActivity -> zoneActivity.intValue() > 0 )
      .collect( toSet() );
  }

  private static Optional<Percent> findSectionActivity( Activity activity, Zone zone ) {
    return activity.getSectionActivity( ( SectionDefinition )zone.getZoneEntity().getDefinition() );
  }

  private static Optional<Percent> findSectionAllocation( Activity activity, Zone zone ) {
    return activity.getSectionAllocation( ( SectionDefinition )zone.getZoneEntity().getDefinition() );
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

  private static Integer toInteger( int value ) {
    return Integer.valueOf( value );
  }
}