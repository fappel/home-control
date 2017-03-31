package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.BED;
import static com.codeaffine.home.control.application.test.ActivationHelper.createZone;
import static com.codeaffine.home.control.application.test.RegistryHelper.stubSection;
import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.stubEntity;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.status.Activation;
import com.codeaffine.home.control.application.status.ActivationProvider;

public class AllocationRateCalculatorTest {

  private ActivationProvider activationProvider;
  private AllocationRateCalculator calculator;
  private ActivationTracker tracker;
  private Section section;

  @Before
  public void setUp() {
    tracker = mock( ActivationTracker.class );
    section = stubSection( BED );
    activationProvider = mock( ActivationProvider.class );
    calculator = new AllocationRateCalculator( section, activationProvider, tracker );
  }

  @Test
  public void isActiveIfActivationStatusContainsSectionZone() {
    stubActivationProvider( new Activation( asSet( createZone( stubEntity( BED ) ) ) ) );

    boolean actual = calculator.isActive();

    assertThat( actual ).isTrue();
  }

  @Test
  public void isActiveIfActivationStatusDoesNotContainSectionZone() {
    stubActivationProvider( new Activation( emptySet() ) );

    boolean actual = calculator.isActive();

    assertThat( actual ).isFalse();
  }

  private void stubActivationProvider( Activation activation ) {
    when( activationProvider.getStatus() ).thenReturn( activation );
  }
}