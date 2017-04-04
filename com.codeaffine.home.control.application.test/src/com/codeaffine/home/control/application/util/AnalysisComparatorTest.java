package com.codeaffine.home.control.application.util;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.WORK_AREA;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class AnalysisComparatorTest {

  private static final Integer SMALL = Integer.valueOf( 9 );
  private static final Integer MIDDLE = Integer.valueOf( 10 );
  private static final Integer LARGE = Integer.valueOf( 11 );

  @Test
  public void isEqualTo() {
    boolean larger = AnalysisComparator.isEqualTo( WORK_AREA, section -> MIDDLE, SMALL );
    boolean same = AnalysisComparator.isEqualTo( WORK_AREA, section -> MIDDLE, MIDDLE );
    boolean smaller = AnalysisComparator.isEqualTo( WORK_AREA, section -> MIDDLE, LARGE );

    assertThat( larger ).isFalse();
    assertThat( same ).isTrue();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isAtLeast() {
    boolean larger = AnalysisComparator.isAtLeast( WORK_AREA, section -> MIDDLE, SMALL );
    boolean same = AnalysisComparator.isAtLeast( WORK_AREA, section -> MIDDLE, MIDDLE );
    boolean smaller = AnalysisComparator.isAtLeast( WORK_AREA, section -> MIDDLE, LARGE );

    assertThat( larger ).isTrue();
    assertThat( same ).isTrue();
    assertThat( smaller ).isFalse();
  }

  @Test
  public void isAtMost() {
    boolean larger = AnalysisComparator.isAtMost( WORK_AREA, section -> MIDDLE, SMALL );
    boolean same = AnalysisComparator.isAtMost( WORK_AREA, section -> MIDDLE, MIDDLE );
    boolean smaller = AnalysisComparator.isAtMost( WORK_AREA, section -> MIDDLE, LARGE );

    assertThat( larger ).isFalse();
    assertThat( same ).isTrue();
    assertThat( smaller ).isTrue();
  }

  @Test( expected = IllegalArgumentException.class )
  public void isEqualToWithNullAsSectionDefinitionArgument() {
    AnalysisComparator.isEqualTo( null, section -> MIDDLE, SMALL );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isEqualToWithNullAsStatusCalculatorArgument() {
    AnalysisComparator.isEqualTo( WORK_AREA, null, SMALL );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isEqualToWithNullAsStatusArgument() {
    AnalysisComparator.isEqualTo( WORK_AREA, section -> MIDDLE, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isAtLeastWithNullAsSectionDefinitionArgument() {
    AnalysisComparator.isAtLeast( null, section -> MIDDLE, SMALL );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isAtLeastWithNullAsStatusCalculatorArgument() {
    AnalysisComparator.isAtLeast( WORK_AREA, null, SMALL );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isAtLeastWithNullAsStatusArgument() {
    AnalysisComparator.isAtLeast( WORK_AREA, section -> MIDDLE, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isAtMostWithNullAsSectionDefinitionArgument() {
    AnalysisComparator.isAtMost( null, section -> MIDDLE, SMALL );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isAtMostWithNullAsStatusCalculatorArgument() {
    AnalysisComparator.isAtMost( WORK_AREA, null, SMALL );
  }

  @Test( expected = IllegalArgumentException.class )
  public void isAtMostWithNullAsStatusArgument() {
    AnalysisComparator.isAtMost( WORK_AREA, section -> MIDDLE, null );
  }

  @Test( expected = NullPointerException.class )
  public void isEqualToWithNullAsSectionCalculatorReturnValue() {
    AnalysisComparator.isEqualTo( WORK_AREA, section -> null, SMALL );
  }

  @Test( expected = NullPointerException.class )
  public void isAtLeastWithNullAsSectionCalculatorReturnValue() {
    AnalysisComparator.isAtLeast( WORK_AREA, section -> null, SMALL );
  }

  @Test( expected = NullPointerException.class )
  public void isAtMostWithNullAsSectionCalculatorReturnValue() {
    AnalysisComparator.isAtMost( WORK_AREA, section -> null, SMALL );
  }
}