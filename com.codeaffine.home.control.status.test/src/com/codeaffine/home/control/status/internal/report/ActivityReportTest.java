package com.codeaffine.home.control.status.internal.report;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.item.StringItem;
import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;

public class ActivityReportTest {

  private static final String OVER_ALL_ACTIVITY = "OVER_ALL_ACTIVITY";

  private ActivityReportCompiler reportCompiler;
  private ActivityReport activityReport;
  private StringItem overallActivity;
  private StringItem dressingArea;
  private StringItem cookingArea;
  private StringItem livingArea;
  private StringItem diningArea;
  private StringItem workArea;
  private StringItem bathroom;
  private StringItem bedSide;
  private StringItem hall;
  private StringItem bed;

  @Before
  public void setUp() {
    overallActivity = mock( StringItem.class );
    bed = mock( StringItem.class );
    bedSide = mock( StringItem.class );
    dressingArea = mock( StringItem.class );
    livingArea = mock( StringItem.class );
    workArea = mock( StringItem.class );
    hall = mock( StringItem.class );
    cookingArea = mock( StringItem.class );
    diningArea = mock( StringItem.class );
    bathroom = mock( StringItem.class );
    reportCompiler = stubReportCompiler();
    activityReport = createActivityReport();
  }

  private static ActivityReportCompiler stubReportCompiler() {
    ActivityReportCompiler result = mock( ActivityReportCompiler.class );
    when( result.getOverallActivityReport() ).thenReturn( OVER_ALL_ACTIVITY );
    when( result.getReportFor( any( SectionDefinition.class ) ) )
      .then( invokation -> invokation.getArguments()[ 0 ].toString() );
    return result;
  }

  @Test
  public void report() {
    activityReport.report();

    verify( overallActivity ).updateStatus( OVER_ALL_ACTIVITY );
    verify( bed ).updateStatus( BED.toString() );
    verify( bedSide ).updateStatus( BED_SIDE.toString() );
    verify( dressingArea ).updateStatus( DRESSING_AREA.toString() );
    verify( livingArea ).updateStatus( LIVING_AREA.toString() );
    verify( workArea ).updateStatus( WORK_AREA.toString() );
    verify( hall ).updateStatus( HALL.toString() );
    verify( cookingArea ).updateStatus( COOKING_AREA.toString() );
    verify( diningArea ).updateStatus( DINING_AREA.toString() );
    verify( bathroom ).updateStatus( BATH_ROOM.toString() );
  }

  private ActivityReport createActivityReport() {
    return new ActivityReport( overallActivity,
                               bed,
                               bedSide,
                               dressingArea,
                               livingArea,
                               workArea,
                               hall,
                               cookingArea,
                               diningArea,
                               bathroom,
                               reportCompiler );
  }
}