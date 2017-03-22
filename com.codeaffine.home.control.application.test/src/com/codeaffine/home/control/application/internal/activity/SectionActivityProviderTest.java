package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.BED;
import static com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensorDefinition.BED_MOTION;
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
import com.codeaffine.home.control.application.sensor.ActivationSensorProvider.ActivationSensor;
import com.codeaffine.home.control.application.type.Percent;

public class SectionActivityProviderTest {

  private SectionActivityProvider provider;
  private ActivationTracker tracker;
  private ActivationSensor sensor;
  private Section section;

  @Before
  public void setUp() {
    tracker = mock( ActivationTracker.class );
    section = stubSection( BED );
    sensor = stubActivationSensor( BED_MOTION );
    equipWithActivationSensor( section, sensor );
    provider = new SectionActivityProvider( section, tracker );
  }

  @Test
  public void calculateRate() {
    when( tracker.calculateRate() ).thenReturn( P_004 );

    Percent actual = provider.calculateRate();

    assertThat( actual ).isSameAs( P_004 );
  }

  @Test
  public void captureSensorActivations() {
    when( sensor.isEngaged() ).thenReturn( true );

    provider.captureSensorActivations();

    InOrder order = inOrder( tracker );
    order.verify( tracker ).captureActivation();
    order.verify( tracker ).removeExpired();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void captureSensorActivationsIfSensorIsNotEngaged() {
    provider.captureSensorActivations();

    verify( tracker, never() ).captureActivation();
    verify( tracker ).removeExpired();
  }

  @Test
  public void setTimestampSupplier() {
    Supplier<LocalDateTime> expected = () -> null;

    provider.setTimestampSupplier( expected );

    verify( tracker ).setTimestampSupplier( expected );
  }
}