package com.codeaffine.home.control.status.supplier;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.type.Percent;

public class Activity {

  private final Map<SectionDefinition, Percent> sectionActivities;
  private final Map<SectionDefinition, Percent> sectionAllocations;
  private final Percent overallActivity;

  public Activity( Percent overallActivity,
                   Map<SectionDefinition, Percent> sectionActivities,
                   Map<SectionDefinition, Percent> sectionAllocations )
  {
    verifyNotNull( sectionAllocations, "sectionAllocations" );
    verifyNotNull( sectionActivities, "sectionActivities" );
    verifyNotNull( overallActivity, "overallActivity" );

    this.sectionAllocations = new HashMap<>( sectionAllocations );
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

  public Optional<Percent> getSectionAllocation( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    return Optional.ofNullable( sectionAllocations.get( sectionDefinition ) );
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + overallActivity.hashCode();
    result = prime * result + sectionActivities.hashCode();
    result = prime * result + sectionAllocations.hashCode();
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
    if( !sectionAllocations.equals( other.sectionAllocations ) )
      return false;
    return true;
  }

  @Override
  public String toString() {
    return   "Activity "
           + overallActivity
           + " "
           + joinActivitiesAndAllocations();
  }

  private String joinActivitiesAndAllocations() {
    return sectionActivities
      .keySet()
      .stream()
      .map( sectionDefinition -> provideActivityAndAllocationDataFor( sectionDefinition ) )
      .collect( joining( ", ", "[ ", " ]" ) );
  }

  private String provideActivityAndAllocationDataFor( SectionDefinition sectionDefinition ) {
    return sectionDefinition
         + "="
        + sectionActivities.get( sectionDefinition )
        + "/"
        + sectionAllocations.get( sectionDefinition );
  }
}