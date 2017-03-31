package com.codeaffine.home.control.application.internal.activity;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.BED;
import static com.codeaffine.home.control.application.test.RegistryHelper.stubSection;
import static com.codeaffine.home.control.application.type.Percent.P_004;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.application.section.SectionProvider.Section;
import com.codeaffine.home.control.application.type.Percent;

public class RateCalculatorTest {

  private Supplier<Boolean> activeSupplier;
  private TestRateCalculator calculator;
  private ActivationTracker tracker;
  private Section section;

  static class TestRateCalculator extends RateCalculator {

    private final Supplier<Boolean> activeSupplier;

    TestRateCalculator( Section section, ActivationTracker tracker, Supplier<Boolean> activeSupplier ) {
      super( section, tracker );
      this.activeSupplier = activeSupplier;
    }

    @Override
    protected boolean isActive() {
      return activeSupplier.get();
    }

  }

  @Before
  @SuppressWarnings( "unchecked" )
  public void setUp() {
    tracker = mock( ActivationTracker.class );
    section = stubSection( BED );
    activeSupplier = mock( Supplier.class );
    calculator = new TestRateCalculator( section, tracker, activeSupplier );
  }

  @Test
  public void calculate() {
    when( tracker.calculateRate() ).thenReturn( P_004 );

    Percent actual = calculator.calculate();

    assertThat( actual ).isSameAs( P_004 );
  }

  @Test
  public void captureActivations() {
    when( activeSupplier.get() ).thenReturn( true );

    calculator.captureActivations();

    InOrder order = inOrder( tracker );
    order.verify( tracker ).captureActivation();
    order.verify( tracker ).removeExpired();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void captureActivationsIfActiveSupplierReturnsFalse() {
    when( activeSupplier.get() ).thenReturn( false );

    calculator.captureActivations();

    InOrder order = inOrder( tracker );
    order.verify( tracker ).removeOldest();
    order.verify( tracker ).removeExpired();
    order.verifyNoMoreInteractions();
  }

  @Test
  public void setTimestampSupplier() {
    Supplier<LocalDateTime> expected = () -> null;

    calculator.setTimestampSupplier( expected );

    verify( tracker ).setTimestampSupplier( expected );
  }

  @Test
  public void getSection() {
    Section actual = calculator.getSection();

    assertThat( actual ).isSameAs( section );
  }
}