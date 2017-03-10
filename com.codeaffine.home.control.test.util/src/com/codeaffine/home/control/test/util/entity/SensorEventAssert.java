package com.codeaffine.home.control.test.util.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.AbstractAssert;

import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.entity.Sensor;
import com.codeaffine.home.control.entity.SensorEvent;

public class SensorEventAssert extends AbstractAssert<SensorEventAssert, SensorEvent<?>> {

  public SensorEventAssert( SensorEvent<?> actual ) {
    super( actual, SensorEventAssert.class );
  }

  public static SensorEventAssert assertThat( SensorEvent<?> actual ) {
    return new SensorEventAssert( actual );
  }

  public SensorEventAssert hasSensor( Sensor expected ) {
    isNotNull();
    if( actual.getSensor() != expected ) {
      failWithMessage( "Expected sensor <%s> but was <%s>", expected.getName(), actual.getSensor().getName() );
    }
    return this;
  }

  public SensorEventAssert hasNoAffected() {
    isNotNull();
    if( !actual.getAffected().isEmpty() ) {
      failWithMessage( "Expected affected entity set to be empty but was <%s>", actual.getAffected() );
    }
    return this;
  }

  @SafeVarargs
  public final SensorEventAssert hasAffected( Entity<?> ... expected ) {
    isNotNull();
    String pattern = "Expected affected entity set to contain <%s> but contains of <%s>.";
    verifyContainment( Arrays.asList( expected ), actual.getAffected(), pattern );
    return this;
  }

  public final SensorEventAssert hasSensorStatus( Object expected ) {
    isNotNull();
    if( !actual.getSensorStatus().equals( expected ) ) {
      failWithMessage( "Expected sensor status to be <%s>, but was <%s>.", expected, actual.getSensorStatus() );
    }
    return this;
  }

  private void verifyContainment( List<Entity<?>> expected, Collection<Entity<?>> actual, String pattern ) {
    if( actual.size() != expected.size() ) {
      failWithMessage( pattern, expected, actual );
    }
    if( !actual.containsAll( expected ) ) {
      failWithMessage( pattern, expected, actual );
    }
  }
}