package com.codeaffine.home.control.status.internal.report;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.item.StringItem;

public class ActivityReport {

  private static final long REPORT_UPDATE_SCHEDULE = 2L;

  private final ActivityReportCompiler reportCompiler;
  private final StringItem overallActivity;
  private final StringItem dressingArea;
  private final StringItem cookingArea;
  private final StringItem diningArea;
  private final StringItem livingArea;
  private final StringItem bathroom;
  private final StringItem workArea;
  private final StringItem hall;
  private final StringItem bed;

  ActivityReport(
    @ByName( "S_OVERALL_ACTIVITY" ) StringItem overallActivity,
    @ByName( "S_BED" ) StringItem bed,
    @ByName( "S_DRESSING_AREA" ) StringItem dressingArea,
    @ByName( "S_LIVING_AREA" ) StringItem livingArea,
    @ByName( "S_WORK_AREA" ) StringItem workArea,
    @ByName( "S_HALL" ) StringItem hall,
    @ByName( "S_COOKING_AREA" ) StringItem cookingArea,
    @ByName( "S_DINING_AREA" ) StringItem diningArea,
    @ByName( "S_BATH_ROOM" ) StringItem bathroom,
    ActivityReportCompiler reportCompiler )
  {
    this.reportCompiler = reportCompiler;
    this.overallActivity = overallActivity;
    this.dressingArea = dressingArea;
    this.cookingArea = cookingArea;
    this.diningArea = diningArea;
    this.livingArea = livingArea;
    this.bathroom = bathroom;
    this.workArea = workArea;
    this.hall = hall;
    this.bed = bed;
  }

  @Schedule( period = REPORT_UPDATE_SCHEDULE )
  void report() {
    overallActivity.updateStatus( reportCompiler.getOverallActivityReport() );
    bed.updateStatus( reportCompiler.getReportFor( BED ) );
    dressingArea.updateStatus( reportCompiler.getReportFor( DRESSING_AREA ) );
    livingArea.updateStatus( reportCompiler.getReportFor( LIVING_AREA ) );
    workArea.updateStatus( reportCompiler.getReportFor( WORK_AREA ) );
    hall.updateStatus( reportCompiler.getReportFor( HALL ) );
    cookingArea.updateStatus( reportCompiler.getReportFor( COOKING_AREA ) );
    diningArea.updateStatus( reportCompiler.getReportFor( DINING_AREA ) );
    bathroom.updateStatus( reportCompiler.getReportFor( BATH_ROOM ) );
  }
}