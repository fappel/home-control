package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.internal.activity.ActivityProviderImpl.*;
import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensorDefinition.*;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.ActivationProvider;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;

public class RateCalculatorsTest {

  private ActivationProvider activationProvider;
  private EntityRegistry registry;
  private Section bathroom;
  private Section bed;

  @Before
  public void setUp() {
    bed = stubSection( BED );
    bathroom = stubSection( BATH_ROOM );
    Section livingRoom = stubSection( LIVING_ROOM );
    ActivationSensor bedSensor = stubActivationSensor( BED_MOTION );
    ActivationSensor bathRoomSensor = stubActivationSensor( BATH_ROOM_MOTION );
    equipWithActivationSensor( bed, bedSensor );
    equipWithActivationSensor( bathroom, bathRoomSensor );
    registry = stubRegistry( asList( bed, bathroom, livingRoom ), emptySet(), asList( bedSensor, bathRoomSensor ) );
    activationProvider = stubEmptyActivationProvider();
  }

  @Test
  public void createActivityCalculators() {
    Map<Section, ActivityRateCalculator> actual
      = RateCalculators.createActivityCalculators( registry, OBSERVATION_TIME, CALCULATION_INTERVAL );

    assertThat( actual ).containsOnlyKeys( bed, bathroom );
    assertThat( actual.values() ).allMatch( calculator -> calculator != null );
  }

  @Test
  public void createAllocationCalculators() {
    Map<Section, AllocationRateCalculator> actual
      = RateCalculators.createAllocationCalculators( registry,
                                                     activationProvider,
                                                     OBSERVATION_TIME,
                                                     CALCULATION_INTERVAL );

    assertThat( actual ).containsOnlyKeys( bed, bathroom );
    assertThat( actual.values() ).allMatch( calculator -> calculator != null );
  }

  private static ActivationProvider stubEmptyActivationProvider() {
    ActivationProvider result = mock( ActivationProvider.class );
    when( result.getStatus() ).thenReturn( new Activation( emptySet() ) );
    return result;
  }
}