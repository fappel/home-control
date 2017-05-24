package com.codeaffine.home.control.application.scene;

import static com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition.*;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
import static com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition.*;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.status.type.OnOff.ON;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.lamp.LampProvider.Lamp;
import com.codeaffine.home.control.application.lamp.LampProvider.LampDefinition;
import com.codeaffine.home.control.application.test.RegistryHelper;
import com.codeaffine.home.control.engine.component.event.EventBusImpl;
import com.codeaffine.home.control.engine.component.preference.PreferenceModelImpl;
import com.codeaffine.home.control.engine.component.util.TypeUnloadTracker;
import com.codeaffine.home.control.engine.entity.EntityRelationProviderImpl;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityRelationProvider.Facility;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensor;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition;
import com.codeaffine.home.control.status.model.SectionProvider.Section;
import com.codeaffine.home.control.status.type.OnOff;

public class LightThresholdUtilTest {

  private static final Integer LIGHT_VALUE = 100;

  private LightThresholdUtil lightThresholdUtil;
  private LightThresholdPreference preference;
  private EntityRegistry registry;

  @Before
  public void setUp() {
    registry = stubRegistry();
    EntityRelationProviderImpl relationProvider = new EntityRelationProviderImpl( registry );
    relationProvider.establishRelations( facility -> establishRelations( facility ) );
    TypeUnloadTracker typeUnloadTracker = mock( TypeUnloadTracker.class );
    EventBusImpl eventBus = new EventBusImpl( typeUnloadTracker );
    PreferenceModel preferenceModel = new PreferenceModelImpl( eventBus, typeUnloadTracker );
    preference = preferenceModel.get( LightThresholdPreference.class );
    lightThresholdUtil = new LightThresholdUtil( relationProvider, preference );
  }

  @Test
  public void collectLampsOfZonesWithEnoughDayLight() {
    setSwitchOnThreshold( DINING_AREA_LUX, LIGHT_VALUE - 1 );
    setSwitchOnThreshold( LIVING_AREA_LUX, LIGHT_VALUE );
    setSwitchOffThreshold( DINING_AREA_LUX, LIGHT_VALUE * 2 );
    setSwitchOffThreshold( LIVING_AREA_LUX, LIGHT_VALUE * 2 );
    stubLightSensor( DINING_AREA_LUX, LIGHT_VALUE );
    stubLightSensor( LIVING_AREA_LUX, LIGHT_VALUE );

    LampDefinition[] actual = lightThresholdUtil.collectLampsOfZonesWithEnoughDayLight();

    assertThat( actual ).containsExactly( KitchenCeiling );
  }

  @Test
  public void collectLampsOfZonesWithEnoughDayLightWithSwitchedOnLampBetweenOnAndOffThreshold() {
    setSwitchOnThreshold( DINING_AREA_LUX, LIGHT_VALUE );
    setSwitchOnThreshold( LIVING_AREA_LUX, LIGHT_VALUE - 1 );
    setSwitchOffThreshold( DINING_AREA_LUX, LIGHT_VALUE * 2 );
    setSwitchOffThreshold( LIVING_AREA_LUX, LIGHT_VALUE * 2 );
    stubLightSensor( DINING_AREA_LUX, LIGHT_VALUE );
    stubLightSensor( LIVING_AREA_LUX, LIGHT_VALUE );
    stubLight( ChimneyUplight, ON );

    LampDefinition[] actual = lightThresholdUtil.collectLampsOfZonesWithEnoughDayLight();

    assertThat( actual ).containsExactlyInAnyOrder( DeskUplight, WindowUplight );
  }

  @Test
  public void collectLampsOfZonesWithEnoughDayEvenIfSwitchedOn() {
    setSwitchOnThreshold( DINING_AREA_LUX, LIGHT_VALUE );
    setSwitchOnThreshold( LIVING_AREA_LUX, LIGHT_VALUE - 1 );
    setSwitchOffThreshold( DINING_AREA_LUX, LIGHT_VALUE * 2 );
    setSwitchOffThreshold( LIVING_AREA_LUX, LIGHT_VALUE * 2 );
    stubLightSensor( DINING_AREA_LUX, LIGHT_VALUE );
    stubLightSensor( LIVING_AREA_LUX, LIGHT_VALUE * 2 + 1);
    stubLight( ChimneyUplight, ON );

    LampDefinition[] actual = lightThresholdUtil.collectLampsOfZonesWithEnoughDayLight();

    assertThat( actual ).containsExactlyInAnyOrder( DeskUplight, WindowUplight, ChimneyUplight );
  }

  static EntityRegistry stubRegistry() {
    Set<Lamp> lamps = stubLamps( KitchenCeiling, DeskUplight, WindowUplight, ChimneyUplight );
    Set<Section> rooms = stubSections( DINING_AREA, LIVING_AREA, WORK_AREA );
    Set<LightSensor> sensors = stubLightSensors( DINING_AREA_LUX, LIVING_AREA_LUX );
    return RegistryHelper.stubRegistry( rooms, lamps, emptySet(), sensors );
  }

  private static void establishRelations( Facility facility ) {
    facility.equip( DINING_AREA ).with( KitchenCeiling, DINING_AREA_LUX );
    facility.equip( LIVING_AREA ).with( ChimneyUplight, WindowUplight, LIVING_AREA_LUX );
    facility.equip( WORK_AREA ).with( DeskUplight, LIVING_AREA_LUX );
  }

  private void stubLightSensor( LightSensorDefinition lightSensorDefinition, int lightValue ) {
    LightSensor sensor = registry.findByDefinition( lightSensorDefinition );
    when( sensor.getLightValue() ).thenReturn( lightValue );
  }

  private void setSwitchOnThreshold( LightSensorDefinition sensorDefinition, int lightValue ) {
    Map<LightSensorDefinition, Integer> thresholds = preference.getSwitchOnThreshold();
    thresholds.put( sensorDefinition, lightValue );
    preference.setSwitchOnThreshold( thresholds );
  }

  private void setSwitchOffThreshold( LightSensorDefinition sensorDefinition, int lightValue ) {
    Map<LightSensorDefinition, Integer> thresholds = preference.getSwitchOffThreshold();
    thresholds.put( sensorDefinition, lightValue );
    preference.setSwitchOffThreshold( thresholds );
  }

  private void stubLight( LampDefinition lampDefinition, OnOff onOff ) {
    registry.findByDefinition( lampDefinition ).setOnOffStatus( onOff );
  }
}