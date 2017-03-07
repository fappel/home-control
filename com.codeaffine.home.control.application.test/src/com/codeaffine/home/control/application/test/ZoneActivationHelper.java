package com.codeaffine.home.control.application.test;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.codeaffine.home.control.application.status.ZoneActivation;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class ZoneActivationHelper {

  public static Set<ZoneActivation> asStatus( ZoneActivation ... zoneActivations ) {
    return new HashSet<>( asList( zoneActivations ) );
  }

  @SuppressWarnings("unchecked")
  public static ZoneActivation stubZoneActivation( Entity<?> zone ) {
    ZoneActivation result = mock( ZoneActivation.class );
    when( result.getZone() ).thenReturn( ( Entity<EntityDefinition<?>> )zone );
    when( result.getReleaseTime() ).thenReturn( empty() );
    return result;
  }

  public static ZoneActivation stubZoneActivation( Entity<?> zone, LocalDateTime releaseTime ) {
    ZoneActivation result = stubZoneActivation( zone );
    when( result.getReleaseTime() ).thenReturn( Optional.of( releaseTime ) );
    return result;
  }
}