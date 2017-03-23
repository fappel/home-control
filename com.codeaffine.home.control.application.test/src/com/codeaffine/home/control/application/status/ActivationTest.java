package com.codeaffine.home.control.application.status;

import static com.codeaffine.home.control.application.test.ActivationHelper.*;
import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.test.util.lang.EqualsTester.newInstance;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import org.junit.Test;

import com.codeaffine.home.control.application.status.Activation.Zone;
import com.codeaffine.test.util.lang.EqualsTester;

public class ActivationTest {

  @Test
  public void getAllZones() {
    Set<Zone> expected = asSet( createZone( ZONE_1 ), createZone( ZONE_2 ) );
    Activation activation = new Activation( expected );

    Set<Zone> actual = activation.getAllZones();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getAllZonesWithManipulationOfInitializationSetAfterConstruction() {
    Set<Zone> manipulated = asSet( createZone( ZONE_1 ), createZone( ZONE_2 ) );
    Activation activation = new Activation( manipulated );

    manipulated.add( createZone( ZONE_3 ) );
    Set<Zone> actual = activation.getAllZones();

    assertThat( actual )
      .isNotEqualTo( manipulated )
      .isEqualTo( asSet( createZone( ZONE_1 ), createZone( ZONE_2 ) ) );
  }

  @Test
  public void getAllZonesWithManipulationOfResultSet() {
    Set<Zone> expected = asSet( createZone( ZONE_1 ), createZone( ZONE_2 ) );
    Activation activation = new Activation( expected );

    Set<Zone> manipulated = activation.getAllZones();
    manipulated.add( createZone( ZONE_3 ) );
    Set<Zone> actual = activation.getAllZones();

    assertThat( actual )
      .isEqualTo( expected )
      .isNotEqualTo( manipulated );
  }

  @Test
  public void getZone() {
    Activation activation = new Activation( asSet( createZone( ZONE_1 ), createZone( ZONE_2 ) ) );

    Optional<Zone> actual = activation.getZone( ZONE_DEFINITION_1 );

    assertThat( actual ).hasValue( createZone( ZONE_1 ) );
  }

  @Test
  public void getZoneThatIsNotActivated() {
    Activation activation = new Activation( asSet( createZone( ZONE_1 ), createZone( ZONE_2 ) ) );

    Optional<Zone> actual = activation.getZone( ZONE_DEFINITION_3 );

    assertThat( actual ).isEmpty();
  }

  @Test
  public void isZoneActivated() {
    Activation activation = new Activation( asSet( createZone( ZONE_1 ) ) );

    boolean actual = activation.isZoneActivated( ZONE_DEFINITION_1 );

    assertThat( actual ).isTrue();
  }

  @Test
  public void isZoneActivatedOnInActiveZoneDefinition() {
    Activation activation = new Activation( asSet( createZone( ZONE_1 ) ) );

    boolean actual = activation.isZoneActivated( ZONE_DEFINITION_2 );

    assertThat( actual ).isFalse();
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<Activation> tester = newInstance( new Activation( emptySet() ) );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertEqual( new Activation( asSet( createZone( ZONE_1 ) ) ),
                        new Activation( asSet( createZone( ZONE_1 ) ) ) );
    tester.assertNotEqual( new Activation( asSet( createZone( ZONE_1 ) ) ),
                           new Activation( asSet( createZone( ZONE_2 ) ) ) );
    tester.assertNotEqual( new Activation( asSet( createZone( ZONE_1 ), createZone( ZONE_2 ) ) ),
                           new Activation( asSet( createZone( ZONE_1 ) ) ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsZonesArgument() {
    new Activation( null );
  }
}