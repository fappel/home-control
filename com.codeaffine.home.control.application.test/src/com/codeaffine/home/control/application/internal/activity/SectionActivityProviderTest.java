package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.BED;
import static com.codeaffine.home.control.application.sensor.MotionSensorProvider.MotionSensorDefinition.BED_MOTION;
import static com.codeaffine.home.control.application.test.RegistryHelper.*;
import static com.codeaffine.home.control.application.type.Percent.P_004;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.sensor.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.type.Percent;

public class SectionActivityProviderTest {

  private SectionActivityProvider provider;
  private MotionActivationTracker tracker;
  private MotionSensor motionSensor;
  private Section section;

  @Before
  public void setUp() {
    tracker = mock( MotionActivationTracker.class );
    section = stubSection( BED );
    motionSensor = stubMotionSensor( BED_MOTION );
    equipWithMotionSensor( section, motionSensor );
    provider = new SectionActivityProvider( section, tracker );
  }

  @Test
  public void calculateRate() {
    when( tracker.calculateRate() ).thenReturn( P_004 );

    Percent actual = provider.calculateRate();

    assertThat( actual ).isSameAs( P_004 );
  }

  @Test
  public void captureMotionActivations() {
    when( motionSensor.isEngaged() ).thenReturn( true );

    provider.captureMotionActivations();

    InOrder order = inOrder( tracker );
    order.verify( tracker ).captureMotionActivation();
    order.verify( tracker ).removeExpired();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void captureMotionActivationsIfSensorIsNotEngaged() {
    provider.captureMotionActivations();

    verify( tracker, never() ).captureMotionActivation();
    verify( tracker ).removeExpired();
  }

  @Test
  public void setTimestampSupplier() {
    Supplier<LocalDateTime> expected = () -> null;

    provider.setTimestampSupplier( expected );

    verify( tracker ).setTimestampSupplier( expected );
  }
}
