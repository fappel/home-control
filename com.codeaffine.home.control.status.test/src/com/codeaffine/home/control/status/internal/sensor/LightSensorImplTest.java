package com.codeaffine.home.control.status.internal.sensor;

import static com.codeaffine.home.control.status.model.LightSensorProvider.LightSensorDefinition.BED_LUX;
import static com.codeaffine.home.control.test.util.entity.SensorEventAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.function.BiFunction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.item.NumberItem;
import com.codeaffine.home.control.status.model.LightEvent;
import com.codeaffine.home.control.status.model.LightSensorProvider.LightSensor;
import com.codeaffine.home.control.type.DecimalType;

public class LightSensorImplTest {

  private static final Integer LIGHT_VALUE = Integer.valueOf( 4 );

  private ChangeListener<NumberItem, DecimalType> sensorNumberStateObserver;
  private SensorControlFactory sensorControlFactory;
  private SensorControl sensorControl;
  private LightSensorImpl sensor;
  private NumberItem sensorItem;

  @Before
  public void setUp() {
    sensorItem = stubSensorItem();
    sensorControl = mock( SensorControl.class );
    sensorControlFactory = stubSensorControlFactory( sensorControl );
    sensor = new LightSensorImpl( BED_LUX, sensorItem, sensorControlFactory );
    sensorNumberStateObserver = captureSensorNumberStateObserver();
  }

  @Test
  public void initialization() {
    assertThat( sensor.getDefinition() ).isSameAs( BED_LUX );
    assertThat( sensor.getLightValue() ).isEqualTo( LightSensorImpl.INITIAL_LIGHT_VALUE.intValue() );
    assertThat( sensor.getName() ).isEqualTo( BED_LUX.toString() );
  }

  @Test
  public void lightSensorEventFactoryInvocation() {
    Entity<?> affected = mock( Entity.class );
    BiFunction<Object, Entity<?>[], SensorEvent<?>> eventFactory = captureSensorEventFactory();

    SensorEvent<?> actual = eventFactory.apply( LIGHT_VALUE, new Entity[] { affected } );

    assertThat( actual )
      .isInstanceOf( LightEvent.class )
      .hasAffected( affected )
      .hasSensor( sensor )
      .hasSensorStatus( LIGHT_VALUE );
  }

  @Test
  public void registerAffected() {
    Entity<?> expected = mock( Entity.class );

    sensor.registerAffected( expected );

    verify( sensorControl ).registerAffected( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerAffectedWithNullAsArgument() {
    sensor.registerAffected( null );
  }

  @Test
  public void unregisterAffected() {
    Entity<?> expected = mock( Entity.class );

    sensor.unregisterAffected( expected );

    verify( sensorControl ).unregisterAffected( expected );
  }

  @Test( expected = IllegalArgumentException.class )
  public void unregisterAffectedWithNullAsArgument() {
    sensor.unregisterAffected( null );
  }

  @Test
  public void notifyAboutItemStatusChange() {
    ChangeEvent<NumberItem, DecimalType> event = stubEvent( new DecimalType( LIGHT_VALUE ) );

    sensorNumberStateObserver.itemChanged( event );

    verify( sensorControl ).notifyAboutSensorStatusChange( LIGHT_VALUE );
  }

  @Test
  public void releaseWithUndefinedStatus() {
    ChangeEvent<NumberItem, DecimalType> event = stubEvent( null );

    sensorNumberStateObserver.itemChanged( event );

    verify( sensorControl ).notifyAboutSensorStatusChange( LightSensorImpl.INITIAL_LIGHT_VALUE.intValue() );
  }

  @Test
  public void getLightValue() {
    when( sensorItem.getStatus() ).thenReturn( Optional.of( new DecimalType( LIGHT_VALUE ) ) );

    int actual = sensor.getLightValue();

    assertThat( actual ).isEqualTo( LIGHT_VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsDefinitionArgument() {
    new LightSensorImpl( null, sensorItem, sensorControlFactory );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsSensorItemArgument() {
    new LightSensorImpl( BED_LUX, null, sensorControlFactory );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsSensorControlFactoryArgument() {
    new LightSensorImpl( BED_LUX, sensorItem, null );
  }

  private static NumberItem stubSensorItem() {
    NumberItem result = mock( NumberItem.class );
    when( result.getStatus() ).thenReturn( Optional.empty() );
    return result;
  }

  @SuppressWarnings("unchecked")
  private static SensorControlFactory stubSensorControlFactory( SensorControl sensorControl ) {
    SensorControlFactory result = mock( SensorControlFactory.class );
    when( result.create( any( LightSensor.class ), any( BiFunction.class ) ) ).thenReturn( sensorControl );
    return result;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private BiFunction<Object, Entity<?>[], SensorEvent<?>> captureSensorEventFactory() {
    ArgumentCaptor<BiFunction> captor = forClass( BiFunction.class );
    verify( sensorControlFactory ).create( eq( sensor ), captor.capture() );
    return captor.getValue();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private ChangeListener<NumberItem, DecimalType> captureSensorNumberStateObserver() {
    ArgumentCaptor<ChangeListener> captor = forClass( ChangeListener.class );
    verify( sensorItem ).addChangeListener( captor.capture() );
    return captor.getValue();
  }

  private static ChangeEvent<NumberItem, DecimalType> stubEvent( DecimalType status ) {
    @SuppressWarnings("unchecked")
    ChangeEvent<NumberItem, DecimalType> result = mock( ChangeEvent.class );
    when( result.getNewStatus() ).thenReturn( Optional.ofNullable( status ) );
    return result;
  }
}