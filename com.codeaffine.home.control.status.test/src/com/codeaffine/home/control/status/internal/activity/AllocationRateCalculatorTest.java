package com.codeaffine.home.control.status.internal.activity;

import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.BED;
import static com.codeaffine.home.control.status.test.util.supplier.ActivationHelper.createZone;
import static com.codeaffine.home.control.status.test.util.model.ModelRegistryHelper.stubSection;
import static com.codeaffine.home.control.test.util.entity.EntityHelper.stubEntity;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.status.internal.activity.ActivationTracker;
import com.codeaffine.home.control.status.internal.activity.AllocationRateCalculator;
import com.codeaffine.home.control.status.model.SectionProvider.Section;
import com.codeaffine.home.control.status.supplier.Activation;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;

public class AllocationRateCalculatorTest {

  private ActivationSupplier activationSupplier;
  private AllocationRateCalculator calculator;
  private ActivationTracker tracker;
  private Section section;

  @Before
  public void setUp() {
    tracker = mock( ActivationTracker.class );
    section = stubSection( BED );
    activationSupplier = mock( ActivationSupplier.class );
    calculator = new AllocationRateCalculator( section, activationSupplier, tracker );
  }

  @Test
  public void isActiveIfActivationStatusContainsSectionZone() {
    stubActivationSupplier( new Activation( asSet( createZone( stubEntity( BED ) ) ) ) );

    boolean actual = calculator.isActive();

    assertThat( actual ).isTrue();
  }

  @Test
  public void isActiveIfActivationStatusDoesNotContainSectionZone() {
    stubActivationSupplier( new Activation( emptySet() ) );

    boolean actual = calculator.isActive();

    assertThat( actual ).isFalse();
  }

  private void stubActivationSupplier( Activation activation ) {
    when( activationSupplier.getStatus() ).thenReturn( activation );
  }
}