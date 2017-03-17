package com.codeaffine.home.control.application.internal.zone;

import static com.codeaffine.home.control.test.util.entity.EntityHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class PathLogEntryTest {

  private static final EntityDefinition<?> ZONE_DEFINITION_1 = stubEntityDefinition( "Zone1" );
  private static final EntityDefinition<?> ZONE_DEFINITION_2 = stubEntityDefinition( "Zone2" );
  private static final Entity<EntityDefinition<?>> ZONE_1 = stubEntity( ZONE_DEFINITION_1 );
  private static final Entity<EntityDefinition<?>> ZONE_2 = stubEntity( ZONE_DEFINITION_2 );

  private PathLogEntry pathLogEntry;
  private Path path;

  @Before
  public void setUp() {
    path = new Path();
    pathLogEntry = new PathLogEntry( path );
  }

  @Test
  public void toStringImplementation() {
    path.addOrReplace( createReleasedActivation( ZONE_1 ) );
    path.addOrReplace( createActivation( ZONE_2 ) );

    String actual = pathLogEntry.toString();

    assertThat( actual ).isEqualTo( "Zone1 <released>, Zone2" );
  }

  @Test
  public void toStringImplementationWithReversedActivations() {
    path.addOrReplace( createActivation( ZONE_2 ) );
    path.addOrReplace( createReleasedActivation( ZONE_1 ) );

    String actual = pathLogEntry.toString();

    assertThat( actual ).isEqualTo( "Zone2, Zone1 <released>" );
  }

  @Test
  public void toStringImplementationWithSingleActivation() {
    path.addOrReplace( createActivation( ZONE_1 ) );

    String actual = pathLogEntry.toString();

    assertThat( actual ).isEqualTo( "Zone1" );
  }

  @Test
  public void toStringImplementationWithSingleReleasedActivation() {
    path.addOrReplace( createReleasedActivation( ZONE_1 ) );

    String actual = pathLogEntry.toString();

    assertThat( actual ).isEqualTo( "Zone1 <released>" );
  }

  private static ZoneActivationImpl createReleasedActivation( Entity<EntityDefinition<?>> zone ) {
    ZoneActivationImpl result = createActivation( zone );
    result.markAsReleased();
    return result;
  }

  private static ZoneActivationImpl createActivation( Entity<EntityDefinition<?>> zone ) {
    return new ZoneActivationImpl( zone, mock( PathAdjacency.class ) );
  }
}
