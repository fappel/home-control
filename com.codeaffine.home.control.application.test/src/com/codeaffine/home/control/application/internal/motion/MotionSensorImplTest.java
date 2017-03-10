package com.codeaffine.home.control.application.internal.motion;

import static com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorDefinition.bathRoomMotion1;
import static com.codeaffine.home.control.test.util.entity.SensorEventAssert.assertThat;
import static com.codeaffine.home.control.type.OnOffType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.function.BiFunction;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.motion.MotionSensorProvider.MotionSensorEvent;
import com.codeaffine.home.control.application.type.OnOff;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.SensorControl;
import com.codeaffine.home.control.entity.SensorControl.SensorControlFactory;
import com.codeaffine.home.control.entity.SensorEvent;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.event.ChangeListener;
import com.codeaffine.home.control.item.SwitchItem;
import com.codeaffine.home.control.type.OnOffType;

public class MotionSensorImplTest {

  private ChangeListener<SwitchItem, OnOffType> sensorSwitchStateObserver;
  private SensorControlFactory sensorControlFactory;
  private SensorControl sensorControl;
  private SwitchItem sensorItem;
  private MotionSensorImpl sensor;

  @Before
  public void setUp() {
    sensorItem = stubSensorItem();
    sensorControl = mock( SensorControl.class );
    sensorControlFactory = stubSensorControlFactory( sensorControl );
    sensor = new MotionSensorImpl( bathRoomMotion1, sensorItem, sensorControlFactory );
    sensorSwitchStateObserver = captureSensorSwitchStateObserver();
  }

  @Test
  public void initialization() {
    assertThat( sensor.getDefinition() ).isSameAs( bathRoomMotion1 );
    assertThat( sensor.isEngaged() ).isFalse();
    assertThat( sensor.getName() ).isEqualTo( bathRoomMotion1.toString() );
  }

  @Test
  public void motionSensorEventFactoryInvocation() {
    Entity<?> affected = mock( Entity.class );
    BiFunction<Object, Entity<?>[], SensorEvent<?>> eventFactory = captureSensorEventFactory();

    SensorEvent<?> actual = eventFactory.apply( OnOff.ON, new Entity[] { affected } );

    assertThat( actual )
      .isInstanceOf( MotionSensorEvent.class )
      .hasAffected( affected )
      .hasSensor( sensor )
      .hasSensorStatus( OnOff.ON );
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
  public void engage() {
    ChangeEvent<SwitchItem, OnOffType> event = stubEvent( ON );

    sensorSwitchStateObserver.itemChanged( event );

    verify( sensorControl ).notifyAboutSensorStatusChange( ON );
  }

  @Test
  public void release() {
    ChangeEvent<SwitchItem, OnOffType> event = stubEvent( OFF );

    sensorSwitchStateObserver.itemChanged( event );

    verify( sensorControl ).notifyAboutSensorStatusChange( OFF );
  }

  @Test
  public void releaseWithUndefinedStatus() {
    ChangeEvent<SwitchItem, OnOffType> event = stubEvent( null );

    sensorSwitchStateObserver.itemChanged( event );

    verify( sensorControl ).notifyAboutSensorStatusChange( OFF );
  }

  @Test
  public void isEngaged() {
    when( sensorItem.getStatus() ).thenReturn( Optional.of( ON ) );

    boolean actual = sensor.isEngaged();

    assertThat( actual ).isTrue();
  }

  @Test
  public void isEngagedIfOff() {
    when( sensorItem.getStatus() ).thenReturn( Optional.of( OFF ) );

    boolean actual = sensor.isEngaged();

    assertThat( actual ).isFalse();
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsDefinitionArgument() {
    new MotionSensorImpl( null, sensorItem, sensorControlFactory );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsSensorItemArgument() {
    new MotionSensorImpl( bathRoomMotion1, null, sensorControlFactory );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsSensorControlFactoryArgument() {
    new MotionSensorImpl( bathRoomMotion1, sensorItem, null );
  }

  private static SwitchItem stubSensorItem() {
    SwitchItem result = mock( SwitchItem.class );
    when( result.getStatus() ).thenReturn( Optional.empty() );
    return result;
  }

  @SuppressWarnings("unchecked")
  private static SensorControlFactory stubSensorControlFactory( SensorControl sensorControl ) {
    SensorControlFactory result = mock( SensorControlFactory.class );
    when( result.create( any( MotionSensor.class ), any( BiFunction.class ) ) ).thenReturn( sensorControl );
    return result;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private BiFunction<Object, Entity<?>[], SensorEvent<?>> captureSensorEventFactory() {
    ArgumentCaptor<BiFunction> captor = forClass( BiFunction.class );
    verify( sensorControlFactory ).create( eq( sensor ), captor.capture() );
    return captor.getValue();
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private ChangeListener<SwitchItem, OnOffType> captureSensorSwitchStateObserver() {
    ArgumentCaptor<ChangeListener> captor = forClass( ChangeListener.class );
    verify( sensorItem ).addChangeListener( captor.capture() );
    return captor.getValue();
  }

  private static ChangeEvent<SwitchItem, OnOffType> stubEvent( OnOffType status ) {
    @SuppressWarnings("unchecked")
    ChangeEvent<SwitchItem, OnOffType> result = mock( ChangeEvent.class );
    when( result.getNewStatus() ).thenReturn( Optional.ofNullable( status ) );
    return result;
  }
}