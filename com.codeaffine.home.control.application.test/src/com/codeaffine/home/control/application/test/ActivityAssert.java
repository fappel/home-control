package com.codeaffine.home.control.application.test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.assertj.core.api.AbstractAssert;

import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.status.Activity;
import com.codeaffine.home.control.application.type.Percent;

public class ActivityAssert extends AbstractAssert<ActivityAssert, Activity> {

  public ActivityAssert( Activity actual ) {
    super( actual, ActivityAssert.class );
  }

  public static ActivityAssert assertThat( Activity actual ) {
    return new ActivityAssert( actual );
  }

  public ActivityAssert hasOverallActivity( Percent expected ) {
    isNotNull();
    if( !actual.getOverallActivity().equals( expected ) ) {
      failWithMessage( "Expected overall activity to be <%s> but was <%s>.", expected, actual.getOverallActivity() );
    }
    return this;
  }

  public ActivityAssert sectionActivityIsPresentFor( SectionDefinition sectionDefinition ) {
    isNotNull();
    if( !actual.getSectionActivity( sectionDefinition ).isPresent() ) {
      failWithMessage( "Expected section activity for section <%s> to be present but wasn't.", sectionDefinition );
    }
    return this;
  }

  public ActivityAssert sectionActivityIsNotPresentFor( SectionDefinition sectionDefinition ) {
    isNotNull();
    if( actual.getSectionActivity( sectionDefinition ).isPresent() ) {
      failWithMessage( "Expected section activity for section <%s> not to be present but it was.", sectionDefinition );
    }
    return this;
  }

  public ActivityAssert hasSectionActivity( SectionDefinition sectionDefinition, Percent expected ) {
    isNotNull();
    sectionActivityIsPresentFor( sectionDefinition );
    if( !actual.getSectionActivity( sectionDefinition ).get().equals( expected ) ) {
      failWithMessage( "Expected section activity for section <%s> to be <%s> but was <%s>.",
                       sectionDefinition,
                       expected,
                       actual.getSectionActivity( sectionDefinition ) );
    }
    return this;
  }

  public ActivityAssert hasNoOtherSectionActivityThanFor( SectionDefinition ... sectionDefinitions ) {
    Set<SectionDefinition> toCheck = new HashSet<>( Arrays.asList( SectionDefinition.values() ) );
    toCheck.removeAll( Arrays.asList( sectionDefinitions ) );
    toCheck.forEach( sectionDefinition -> sectionActivityIsNotPresentFor( sectionDefinition ) );
    return this;
  }
}