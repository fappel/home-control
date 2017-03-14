package com.codeaffine.home.control.application.status;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.type.Percent;

public class Activity {

  private final Map<SectionDefinition, Percent> sectionActivities;
  private final Percent overallActivity;

  public Activity( Percent overallActivity, Map<SectionDefinition, Percent> sectionActivities ) {
    verifyNotNull( sectionActivities, "sectionActivities" );
    verifyNotNull( overallActivity, "overallActivity" );

    this.sectionActivities = new HashMap<>( sectionActivities );
    this.overallActivity = overallActivity;
  }

  public Percent getOverallActivity() {
    return overallActivity;
  }

  public Optional<Percent> getSectionActivity( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    return Optional.ofNullable( sectionActivities.get( sectionDefinition ) );
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + overallActivity.hashCode();
    result = prime * result + sectionActivities.hashCode();
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if( this == obj )
      return true;
    if( obj == null )
      return false;
    if( getClass() != obj.getClass() )
      return false;
    Activity other = ( Activity )obj;
    if( overallActivity != other.overallActivity )
      return false;
    if( !sectionActivities.equals( other.sectionActivities ) )
      return false;
    return true;
  }

  @Override
  public String toString() {
    return   "Activity "
           + overallActivity
           + " "
           + sectionActivities.toString().replaceAll( "\\{", "[ " ).replaceAll( "\\}", " ]" );
  }
}