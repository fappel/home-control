package com.codeaffine.home.control.status.internal.activation;

import static com.codeaffine.home.control.status.internal.activation.PreferenceUtil.*;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.internal.activation.Path;
import com.codeaffine.home.control.status.internal.activation.PathLogEntry;

public class PathLogEntryTest {

  private PathLogEntry pathLogEntry;
  private Path path;

  @Before
  public void setUp() {
    path = new Path( stubPreference( PATH_EXPIRED_TIMEOUT_IN_SECONDS ) );
    pathLogEntry = new PathLogEntry( path );
  }

  @Test
  public void toStringImplementation() {
    path.addOrReplace( createReleasedZone( ZONE_1 ) );
    path.addOrReplace( createZone( ZONE_2 ) );

    String actual = pathLogEntry.toString();

    assertThat( actual ).isEqualTo( "Zone1 <released>, Zone2" );
  }

  @Test
  public void toStringImplementationWithReversedActivations() {
    path.addOrReplace( createZone( ZONE_2 ) );
    path.addOrReplace( createReleasedZone( ZONE_1 ) );

    String actual = pathLogEntry.toString();

    assertThat( actual ).isEqualTo( "Zone2, Zone1 <released>" );
  }

  @Test
  public void toStringImplementationWithSingleActivation() {
    path.addOrReplace( createZone( ZONE_1 ) );

    String actual = pathLogEntry.toString();

    assertThat( actual ).isEqualTo( "Zone1" );
  }

  @Test
  public void toStringImplementationWithSingleReleasedActivation() {
    path.addOrReplace( createReleasedZone( ZONE_1 ) );

    String actual = pathLogEntry.toString();

    assertThat( actual ).isEqualTo( "Zone1 <released>" );
  }
}