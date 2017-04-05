package com.codeaffine.home.control.status.internal.activity;

import static com.codeaffine.home.control.status.internal.activity.ActivitySupplierImpl.*;
import static com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition.*;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.test.util.model.ModelRegistryHelper.*;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.status.internal.activity.ActivityRateCalculator;
import com.codeaffine.home.control.status.internal.activity.AllocationRateCalculator;
import com.codeaffine.home.control.status.internal.activity.RateCalculators;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.status.model.SectionProvider.Section;
import com.codeaffine.home.control.status.supplier.Activation;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;

public class RateCalculatorsTest {

  private ActivationSupplier activationSupplier;
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
    registry = stubRegistry( asList( bed, bathroom, livingRoom ), asList( bedSensor, bathRoomSensor ) );
    activationSupplier = stubEmptyActivationSupplier();
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
                                                     activationSupplier,
                                                     OBSERVATION_TIME,
                                                     CALCULATION_INTERVAL );

    assertThat( actual ).containsOnlyKeys( bed, bathroom );
    assertThat( actual.values() ).allMatch( calculator -> calculator != null );
  }

  private static ActivationSupplier stubEmptyActivationSupplier() {
    ActivationSupplier result = mock( ActivationSupplier.class );
    when( result.getStatus() ).thenReturn( new Activation( emptySet() ) );
    return result;
  }
}