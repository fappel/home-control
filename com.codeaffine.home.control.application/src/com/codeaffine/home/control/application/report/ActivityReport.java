package com.codeaffine.home.control.application.report;

import static com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition.*;

import com.codeaffine.home.control.ByName;
import com.codeaffine.home.control.Schedule;
import com.codeaffine.home.control.item.StringItem;

public class ActivityReport {

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

  @Schedule( period = 2L )
  void update() {
    overallActivity.updateStatus( reportCompiler.getOverallActivityStatus() );
    bed.updateStatus( reportCompiler.getStatusFor( BED ) );
    dressingArea.updateStatus( reportCompiler.getStatusFor( DRESSING_AREA ) );
    livingArea.updateStatus( reportCompiler.getStatusFor( LIVING_AREA ) );
    workArea.updateStatus( reportCompiler.getStatusFor( WORK_AREA ) );
    hall.updateStatus( reportCompiler.getStatusFor( HALL ) );
    cookingArea.updateStatus( reportCompiler.getStatusFor( COOKING_AREA ) );
    diningArea.updateStatus( reportCompiler.getStatusFor( DINING_AREA ) );
    bathroom.updateStatus( reportCompiler.getStatusFor( BATH_ROOM ) );
  }
}