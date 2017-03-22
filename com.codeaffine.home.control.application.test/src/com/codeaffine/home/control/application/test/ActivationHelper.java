package com.codeaffine.home.control.application.test;

import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.*;
import static java.util.Optional.empty;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import com.codeaffine.home.control.application.internal.zone.PathAdjacency;
import com.codeaffine.home.control.application.internal.zone.ZoneImpl;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;
import com.codeaffine.home.control.entity.Sensor;

public class ActivationHelper {

  public static final EntityDefinition<?> ZONE_DEFINITION_1 = stubEntityDefinition( "Zone1" );
  public static final EntityDefinition<?> ZONE_DEFINITION_2 = stubEntityDefinition( "Zone2" );
  public static final EntityDefinition<?> ZONE_DEFINITION_3 = stubEntityDefinition( "Zone3" );
  public static final Entity<EntityDefinition<?>> ZONE_1 = stubEntity( ZONE_DEFINITION_1 );
  public static final Entity<EntityDefinition<?>> ZONE_2 = stubEntity( ZONE_DEFINITION_2 );
  public static final Entity<EntityDefinition<?>> ZONE_3 = stubEntity( ZONE_DEFINITION_3 );

  public static Activation asStatus( Zone ... zoneActivations ) {
    return new Activation( asSet( zoneActivations ) );
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static Zone stubZone( Entity zoneEntity ) {
    Zone result = mock( Zone.class );
    when( result.getZoneEntity() ).thenReturn( zoneEntity );
    when( result.getReleaseTime() ).thenReturn( empty() );
    return result;
  }

  public static Zone stubZone( Entity<?> zoneEntity, LocalDateTime releaseTime ) {
    Zone result = stubZone( zoneEntity );
    when( result.getReleaseTime() ).thenReturn( Optional.of( releaseTime ) );
    return result;
  }

  public static Zone createReleasedZone( Entity<?> zoneEntity, Sensor ... sensors ) {
    return createZone( zoneEntity, sensors ).markAsReleased();
  }

  public static Zone createInPathReleasedZone( Entity<?> zoneEntity, Sensor ... sensors ) {
    return createZone( zoneEntity, sensors ).markForInPathRelease();
  }

  public static ZoneImpl createZone( Entity<?> zoneEntity, Sensor ... sensors ) {
    return new ZoneImpl( zoneEntity, mock( PathAdjacency.class ), sensors );
  }
}