package com.codeaffine.home.control.application.report;

import com.codeaffine.home.control.application.section.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.application.util.Analysis;

public class ActivityReportCompiler {

  private final Analysis analysis;

  ActivityReportCompiler( Analysis analysis ) {
    this.analysis = analysis;
  }

  String getOverallActivityStatus() {
    return analysis.getOverallActivityStatus().toString() + " (" + analysis.getOverallActivity() + ")";
  }

  String getStatusFor( SectionDefinition sectionDefinition ) {
    return   analysis.getActivityStatus( sectionDefinition ).toString()
           + ", "
           + analysis.getAllocationStatus( sectionDefinition ).toString()
           + ", "
           + analysis.getMotionStatus( sectionDefinition ).toString()
           + " ("
           + analysis.getActivity( sectionDefinition ).toString()
           + ", "
           + analysis.getAllocation( sectionDefinition ).toString()
           + ")";
  }
}