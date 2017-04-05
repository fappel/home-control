package com.codeaffine.home.control.status.internal.report;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.home.control.status.util.Analysis;

public class ActivityReportCompiler {

  private final Analysis analysis;

  ActivityReportCompiler( Analysis analysis ) {
    verifyNotNull( analysis, "analysis" );

    this.analysis = analysis;
  }

  String getOverallActivityReport() {
    return analysis.getOverallActivityStatus().toString() + " (" + analysis.getOverallActivity() + ")";
  }

  String getReportFor( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

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