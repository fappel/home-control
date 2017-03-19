package com.codeaffine.home.control.application.test;

import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.*;
import static java.util.Optional.empty;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import com.codeaffine.home.control.application.internal.zone.PathAdjacency;
import com.codeaffine.home.control.application.internal.zone.ZoneActivationImpl;
import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class ZoneActivationHelper {

  public static final EntityDefinition<?> ZONE_DEFINITION_1 = stubEntityDefinition( "Zone1" );
  public static final EntityDefinition<?> ZONE_DEFINITION_2 = stubEntityDefinition( "Zone2" );
  public static final EntityDefinition<?> ZONE_DEFINITION_3 = stubEntityDefinition( "Zone3" );
  public static final Entity<EntityDefinition<?>> ZONE_1 = stubEntity( ZONE_DEFINITION_1 );
  public static final Entity<EntityDefinition<?>> ZONE_2 = stubEntity( ZONE_DEFINITION_2 );
  public static final Entity<EntityDefinition<?>> ZONE_3 = stubEntity( ZONE_DEFINITION_3 );

  public static Set<ZoneActivation> asStatus( ZoneActivation ... zoneActivations ) {
    return asSet( zoneActivations );
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static ZoneActivation stubZoneActivation( Entity zone ) {
    ZoneActivation result = mock( ZoneActivation.class );
    when( result.getZone() ).thenReturn( zone );
    when( result.getReleaseTime() ).thenReturn( empty() );
    return result;
  }

  public static ZoneActivation stubZoneActivation( Entity<?> zone, LocalDateTime releaseTime ) {
    ZoneActivation result = stubZoneActivation( zone );
    when( result.getReleaseTime() ).thenReturn( Optional.of( releaseTime ) );
    return result;
  }

  public static ZoneActivation createReleasedZoneActivation( Entity<?> zone ) {
    ZoneActivationImpl result = createZoneActivation( zone );
    result.markAsReleased();
    return result;
  }

  public static ZoneActivation createInPathReleasedZoneActivation( Entity<?> zone ) {
    ZoneActivationImpl result = createZoneActivation( zone );
    result.markForInPathRelease();
    return result;
  }

  public static ZoneActivationImpl createZoneActivation( Entity<?> zone ) {
    return new ZoneActivationImpl( zone, mock( PathAdjacency.class ) );
  }
}