package com.codeaffine.home.control.application.status;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;
import static com.codeaffine.home.control.application.test.ActivityAssert.assertThat;
import static com.codeaffine.home.control.application.type.Percent.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.type.Percent;
import com.codeaffine.test.util.lang.EqualsTester;

public class ActivityTest {

  @Test
  public void accessors() {
    Map<SectionDefinition, Percent> sectionActivities = new HashMap<>();
    sectionActivities.put( BED, P_005 );
    sectionActivities.put( BATH_ROOM, P_007 );
    Map<SectionDefinition, Percent> sectionAllocations = new HashMap<>();
    sectionAllocations.put( BED, P_022 );
    sectionAllocations.put( BATH_ROOM, P_003 );

    Activity actual = new Activity( P_012, sectionActivities, sectionAllocations );

    assertThat( actual )
      .hasOverallActivity( P_012 )
      .hasSectionActivity( BED, P_005 )
      .hasSectionActivity( BATH_ROOM, P_007 )
      .hasNoOtherSectionActivityThanFor( BED, BATH_ROOM )
      .hasSectionAllocation( BED, P_022 )
      .hasSectionAllocation( BATH_ROOM, P_003 )
      .hasNoOtherSectionAllocationThanFor( BED, BATH_ROOM );
  }

  @Test
  public void changeSectionActivitiesParameterAfterConstruction() {
    Map<SectionDefinition, Percent> sectionActivities = new HashMap<>();
    sectionActivities.put( BED, P_005 );

    Activity actual = new Activity( P_005, sectionActivities, new HashMap<>() );
    sectionActivities.put( BATH_ROOM, P_007 );

    assertThat( actual )
      .hasOverallActivity( P_005 )
      .hasSectionActivity( BED, P_005 )
      .hasNoOtherSectionActivityThanFor( BED );
  }

  @Test
  public void changeSectionAllocationsParameterAfterConstruction() {
    Map<SectionDefinition, Percent> sectionAllocations = new HashMap<>();
    sectionAllocations.put( BED, P_005 );

    Activity actual = new Activity( P_005, new HashMap<>(), sectionAllocations );
    sectionAllocations.put( BATH_ROOM, P_007 );

    assertThat( actual )
      .hasOverallActivity( P_005 )
      .hasSectionAllocation( BED, P_005 )
      .hasNoOtherSectionAllocationThanFor( BED );
  }

  @Test
  public void equalsAndHashcode() {
    Map<SectionDefinition, Percent> sectionActivities = new HashMap<>();
    sectionActivities.put( BED, P_005 );
    Map<SectionDefinition, Percent> sectionAllocations = new HashMap<>();
    sectionAllocations.put( BED, P_040 );
    Activity defaultActivity = new Activity( P_005, sectionActivities, sectionAllocations );
    EqualsTester<Activity> tester = EqualsTester.newInstance( defaultActivity );

    tester.assertEqual( new Activity( P_005, sectionActivities, sectionAllocations ),
                        new Activity( P_005, sectionActivities, sectionAllocations ) );
    tester.assertNotEqual( new Activity( P_005, sectionActivities, sectionAllocations ),
                           new Activity( P_012, sectionActivities, sectionAllocations ) );
    tester.assertNotEqual( new Activity( P_005, sectionActivities, sectionAllocations ),
                           new Activity( P_005, emptyMap(), sectionAllocations ) );
    tester.assertNotEqual( new Activity( P_005, sectionActivities, sectionAllocations ),
                           new Activity( P_005, sectionActivities, emptyMap() ) );
    tester.assertImplementsEqualsAndHashCode();
  }

  @Test
  public void toStringImplementation() {
    Map<SectionDefinition, Percent> sectionActivities = new HashMap<>();
    sectionActivities.put( BED, P_005 );
    sectionActivities.put( BATH_ROOM, P_007 );
    Map<SectionDefinition, Percent> sectionAllocations = new HashMap<>();
    sectionAllocations.put( BED, P_022 );
    sectionAllocations.put( BATH_ROOM, P_003 );

    String actual = new Activity( P_012, sectionActivities, sectionAllocations ).toString();

    assertThat( actual )
      .contains( P_012.toString(),
                 BED.toString(),
                 P_005.toString(),
                 P_022.toString(),
                 BATH_ROOM.toString(),
                 P_007.toString(),
                 P_003.toString() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsOverallActivityArgument() {
    new Activity( null, emptyMap(), new HashMap<>() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSectionActivitiesArgument() {
    new Activity( P_000, null, new HashMap<>() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsSectionAllocationsArgument() {
    new Activity( P_000, new HashMap<>(), null );
  }

  public void getSectionActivityWithNullAsSectionDefinitionArgument() {
    Activity activity = new Activity( P_000, emptyMap(), emptyMap() );

    Throwable actual = thrownBy( () -> activity.getSectionActivity( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  public void getSectionAllocationWithNullAsSectionDefinitionArgument() {
    Activity activity = new Activity( P_000, emptyMap(), emptyMap() );

    Throwable actual = thrownBy( () -> activity.getSectionAllocation( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }
}