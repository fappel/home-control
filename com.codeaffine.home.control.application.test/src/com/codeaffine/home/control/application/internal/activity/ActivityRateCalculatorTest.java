package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.BED;
import static com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensorDefinition.BED_MOTION;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensor;

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