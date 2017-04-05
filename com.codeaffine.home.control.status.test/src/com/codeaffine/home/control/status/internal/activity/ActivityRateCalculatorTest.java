package com.codeaffine.home.control.status.internal.activity;

import static com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensorDefinition.BED_MOTION;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.BED;
import static com.codeaffine.home.control.status.test.util.model.ModelRegistryHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.internal.activity.ActivationTracker;
import com.codeaffine.home.control.status.internal.activity.ActivityRateCalculator;
import com.codeaffine.home.control.status.model.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.status.model.SectionProvider.Section;

public class ActivityRateCalculatorTest {

  private ActivityRateCalculator calculator;
  private ActivationTracker tracker;
  private ActivationSensor sensor;
  private Section section;

  @Before
  public void setUp() {
    tracker = mock( ActivationTracker.class );
    section = stubSection( BED );
    sensor = stubActivationSensor( BED_MOTION );
    equipWithActivationSensor( section, sensor );
    calculator = new ActivityRateCalculator( section, tracker );
  }

  @Test
  public void isActiveIfSensorIsEngaged() {
    when( sensor.isEngaged() ).thenReturn( true );

    boolean actual = calculator.isActive();

    assertThat( actual ).isTrue();
  }

  @Test
  public void isActiveIfSensorIsNotEngaged() {
    when( sensor.isEngaged() ).thenReturn( false );

    boolean actual = calculator.isActive();

    assertThat( actual ).isFalse();
  }
}