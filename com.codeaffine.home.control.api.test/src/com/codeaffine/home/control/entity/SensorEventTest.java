package com.codeaffine.home.control.entity;

import static com.codeaffine.home.control.test.util.entity.SensorEventAssert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.EntityProvider.EntityDefinition;

public class SensorEventTest {

  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> AFFECTED_1 = mock( Entity.class );
  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> AFFECTED_2 = mock( Entity.class );
  @SuppressWarnings("unchecked")
  private static final Entity<EntityDefinition<?>> SENSOR = mock( Entity.class );
  private static final Object SENSOR_STATUS = new Object();

  @Test
  public void accessors() {
    SensorEvent<Object> actual = new SensorEvent<>( SENSOR, SENSOR_STATUS, AFFECTED_1 );

    assertThat( actual )
      .hasSensor( SENSOR )
      .hasAffected( AFFECTED_1 )
      .hasSensorStatus( SENSOR_STATUS );
  }

  @Test
  public void accessorsWithEmptyAffected() {
    SensorEvent<Object> actual = new SensorEvent<>( SENSOR, SENSOR_STATUS );

    assertThat( actual )
      .hasSensor( SENSOR )
      .hasNoAffected()
      .hasSensorStatus( SENSOR_STATUS );
  }

  @Test
  public void changeReturnedAffectedSet() {
    SensorEvent<Object> actual = new SensorEvent<>( SENSOR, SENSOR_STATUS, AFFECTED_1 );

    actual.getAffected().add( AFFECTED_2 );

    assertThat( actual )
      .hasSensor( SENSOR )
      .hasAffected( AFFECTED_1 )
      .hasSensorStatus( SENSOR_STATUS );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsSensorArgument() {
    new SensorEvent<>( null, SENSOR_STATUS );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsAffectedArgument() {
    new SensorEvent<>( SENSOR, SENSOR_STATUS, ( Entity<EntityDefinition<?>> )null );
  }

  @Test( expected = IllegalArgumentException.class )
  @SuppressWarnings("unchecked")
  public void createWithNullAsAffectedArrayArgument() {
    new SensorEvent<>( SENSOR, SENSOR_STATUS, new Entity[ 1 ] );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createWithNullAsSensorStatusArgument() {
    new SensorEvent<>( SENSOR, null );
  }
}