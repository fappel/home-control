package com.codeaffine.home.control.application.operation;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.operation.LampTimeoutModus.ON;
import static com.codeaffine.home.control.application.test.RegistryHelper.stubLamp;
import static com.codeaffine.home.control.application.util.TimeoutPreferenceHelper.stubPreference;
import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.test.util.thread.ThreadHelper.sleep;
import static java.time.temporal.ChronoUnit.*;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.util.Timeout;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.test.util.supplier.StatusSupplierHelper;

public class LampTimeoutControlTest {

  private static final Lamp LAMP_BEDROOM_CEILING = stubLamp( BedRoomCeiling );
  private static final Lamp LAMP_WINDOW_UPLIGHT = stubLamp( WindowUplight );
  private static final Lamp LAMP_BED_STAND = stubLamp( BedStand );
  private static final long EXPIRATION_TIME_IN_MILLIS = 10L;

  private LampSwitchOperationPreference preference;
  private ActivationSupplier activationSupplier;
  private LampTimeoutControl timeoutControl;
  private StatusSupplierHelper stubHelper;
  private LampCollector lampCollector;

  @Before
  public void setUp() {
    stubHelper = new StatusSupplierHelper();
    activationSupplier = stubHelper.getActivationSupplier();
    lampCollector = mock( LampCollector.class );
    preference = mock( LampSwitchOperationPreference.class );
    preference = stubPreference( 30L, SECONDS, LampSwitchOperationPreference.class );
    timeoutControl = new LampTimeoutControl( activationSupplier, lampCollector, preference );
  }

  @Test
  public void getLampsToSwitchOn() {
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_BED_STAND ) );
    Set<Lamp> actual = timeoutControl.getLampsToSwitchOn();

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND ) );
  }

  @Test
  public void getLampsToSwitchOnAfterChangeOfSwitchOnSet() {
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_WINDOW_UPLIGHT ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_BED_STAND ) );
    Set<Lamp> actual = timeoutControl.getLampsToSwitchOn();

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND ) );
  }

  @Test
  public void getLampsToSwitchOnWithLampTimeoutAfterChangeOfSwitchOnSet() {
    stubHelper.stubActivationSupplier( emptySet() );
    timeoutControl.setTimeoutModus( ON );

    timeoutControl.setLampsToSwitchOn( asSet( LAMP_WINDOW_UPLIGHT ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_BED_STAND ) );
    Set<Lamp> actual = timeoutControl.getLampsToSwitchOn();

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND, LAMP_WINDOW_UPLIGHT ) );
  }

  @Test
  public void getLampsToSwitchOnWithExpiredLampTimeoutBeforeChangeOfSwitchOnSet() {
    stubHelper.stubActivationSupplier( emptySet() );
    timeoutControl.setTimeoutModus( ON );

    timeoutControl.setTimeoutSupplier( () -> new Timeout( stubPreference( 1L, MINUTES ) ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_WINDOW_UPLIGHT ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_BED_STAND ) );
    Set<Lamp> actual = timeoutControl.getLampsToSwitchOn();

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND ) );
  }

  @Test
  public void getLampsToSwitchOnWithExpiredLampTimeoutAfterChangeOfSwitchOnSet() {
    stubHelper.stubActivationSupplier( emptySet() );
    timeoutControl.setTimeoutModus( ON );
    stubPreference( preference, EXPIRATION_TIME_IN_MILLIS, MILLIS );
    timeoutControl.setTimeoutSupplier( () -> LampTimeoutControl.createHotTimeout( preference ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_WINDOW_UPLIGHT ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_BED_STAND ) );

    sleep( EXPIRATION_TIME_IN_MILLIS * 2 );
    Set<Lamp> actual = timeoutControl.getLampsToSwitchOn();

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND ) );
  }

  @Test
  public void getLampsToSwitchOnWithLampTimeoutAfterChangeWithAdditionalLamp() {
    stubHelper.stubActivationSupplier( emptySet() );
    timeoutControl.setTimeoutModus( ON );

    timeoutControl.setLampsToSwitchOn( asSet( LAMP_WINDOW_UPLIGHT ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_WINDOW_UPLIGHT, LAMP_BED_STAND ) );
    Set<Lamp> actual = timeoutControl.getLampsToSwitchOn();

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND, LAMP_WINDOW_UPLIGHT ) );
  }

  @Test
  public void getLampsToSwitchOnWithLampTimeoutAfterChangeOfSwitchOnSetWhichBelongsToZoneActivation() {
    stubLampCollectorWithZoneLamps( LIVING_AREA, asSet( LAMP_WINDOW_UPLIGHT ) );
    stubHelper.stubActivationSupplier( stubHelper.createZones( LIVING_AREA ) );
    timeoutControl.setTimeoutModus( ON );

    timeoutControl.setLampsToSwitchOn( asSet( LAMP_WINDOW_UPLIGHT ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_BED_STAND ) );
    Set<Lamp> actual = timeoutControl.getLampsToSwitchOn();

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND ) );
  }

  @Test
  public void getLampsToSwitchOnWithLampTimeoutAfterChangeOfSwitchOnSetWithZoneActivationRelatedSections() {
    stubLampCollectorWithZoneLamps( LIVING_AREA, asSet( LAMP_WINDOW_UPLIGHT ) );
    stubLampCollectorWithZoneLamps( DRESSING_AREA, asSet( LAMP_BEDROOM_CEILING ) );
    stubLampCollectorWithZoneLamps( BED, asSet( LAMP_BED_STAND ) );
    stubHelper.stubActivationSupplier( stubHelper.createZones( BED ) );
    timeoutControl.setTimeoutModus( ON );
    timeoutControl.addGroupOfRelatedSections( asSet( BED, DRESSING_AREA ) );

    timeoutControl.setLampsToSwitchOn( asSet( LAMP_WINDOW_UPLIGHT, LAMP_BEDROOM_CEILING ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_BED_STAND ) );
    Set<Lamp> actual = timeoutControl.getLampsToSwitchOn();

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND, LAMP_WINDOW_UPLIGHT ) );
  }

  @Test
  public void getLampsToSwitchOnWithLampsToSwitchOff() {
    stubLampCollectorWithZoneLamps( LIVING_AREA, asSet( LAMP_WINDOW_UPLIGHT ) );
    stubLampCollectorWithZoneLamps( DRESSING_AREA, asSet( LAMP_BEDROOM_CEILING ) );
    stubLampCollectorWithZoneLamps( BED, asSet( LAMP_BED_STAND ) );
    stubHelper.stubActivationSupplier( stubHelper.createZones( BED ) );
    timeoutControl.setTimeoutModus( ON );
    timeoutControl.addGroupOfRelatedSections( asSet( BED, DRESSING_AREA ) );

    timeoutControl.setLampsToSwitchOn( asSet( LAMP_WINDOW_UPLIGHT, LAMP_BEDROOM_CEILING ) );
    timeoutControl.addLampsToSwitchOff( asSet( LAMP_WINDOW_UPLIGHT ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_BED_STAND ) );
    Set<Lamp> actual = timeoutControl.getLampsToSwitchOn();

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND ) );
  }

  @Test
  public void getLampsToSwitchOnAfterPrepare() {
    stubLampCollectorWithZoneLamps( LIVING_AREA, asSet( LAMP_WINDOW_UPLIGHT ) );
    stubLampCollectorWithZoneLamps( DRESSING_AREA, asSet( LAMP_BEDROOM_CEILING ) );
    stubLampCollectorWithZoneLamps( BED, asSet( LAMP_BED_STAND ) );
    stubHelper.stubActivationSupplier( stubHelper.createZones( BED ) );
    timeoutControl.setTimeoutModus( ON );
    timeoutControl.addGroupOfRelatedSections( asSet( BED, DRESSING_AREA ) );
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_WINDOW_UPLIGHT, LAMP_BEDROOM_CEILING ) );
    timeoutControl.addLampsToSwitchOff( asSet( LAMP_WINDOW_UPLIGHT ) );

    timeoutControl.prepare();
    timeoutControl.setLampsToSwitchOn( asSet( LAMP_BED_STAND ) );
    Set<Lamp> actual = timeoutControl.getLampsToSwitchOn();

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND, LAMP_BEDROOM_CEILING, LAMP_WINDOW_UPLIGHT ) );
  }

  @Test
  public void getLampsToSwitchOff() {
    timeoutControl.addLampsToSwitchOff( asSet( LAMP_BED_STAND ) );

    Set<Lamp> actual = timeoutControl.getLampsToSwitchOff();
    timeoutControl.getLampsToSwitchOff().add( LAMP_WINDOW_UPLIGHT );

    assertThat( actual ).isEqualTo( asSet( LAMP_BED_STAND ) );
  }

  private void stubLampCollectorWithZoneLamps( SectionDefinition sectionDefinition, Set<Lamp> zoneLamps ) {
    when( lampCollector.collectZoneLamps( sectionDefinition ) ).thenReturn( zoneLamps );
  }
}