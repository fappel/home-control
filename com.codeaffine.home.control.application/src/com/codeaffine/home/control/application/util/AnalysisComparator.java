package com.codeaffine.home.control.application.util;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.function.Function;

import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;

class AnalysisComparator {
  
  static <T extends Comparable<T>> boolean isEqualTo(
    SectionDefinition sectionDefinition, Function<SectionDefinition, T> statusCalculator, T status )
  {
    return compare( sectionDefinition, statusCalculator, status ) == 0;
  }
  
  static <T extends Comparable<T>> boolean isAtLeast(
    SectionDefinition sectionDefinition, Function<SectionDefinition, T> statusCalculator, T status )
  {
    return compare( sectionDefinition, statusCalculator, status ) >= 0;
  }

  static <T extends Comparable<T>> boolean isAtMost(
    SectionDefinition sectionDefinition, Function<SectionDefinition, T> statusCalculator, T status )
  {
    return compare( sectionDefinition, statusCalculator, status ) <= 0;
  }

  private static <T extends Comparable<T>> int compare(
    SectionDefinition sectionDefinition, Function<SectionDefinition, T> statusCalculator, T status )
  {
    verifyNotNull( sectionDefinition, "sectionDefinition" );
    verifyNotNull( statusCalculator, "statusCalculator" );
    verifyNotNull( status, "status" );

    return statusCalculator.apply( sectionDefinition ).compareTo( status );
  }
}