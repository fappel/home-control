package com.codeaffine.home.control.admin.ui.preference;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.codeaffine.home.control.admin.HomeControlAdminService;
import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.PageContribution;

public class PreferenceContribution implements PageContribution {

  private final HomeControlAdminService adminService;

  private Control control;

  public PreferenceContribution( HomeControlAdminService adminService ) {
    verifyNotNull( adminService, "adminService" );

    this.adminService = adminService;
  }

  @Override
  public String getId() {
    return "Preferences";
  }

  @Override
  public Control createContent( Composite shell ) {
    PreferenceView preferenceView = new PreferenceView( shell );
    preferenceView.setInput( getPreferenceInfos() );
    control = preferenceView.getControl();
    return control;
  }

  private PreferenceInfo[] getPreferenceInfos() {
    Set<PreferenceInfo> infos = adminService.getPreferenceIntrospection().getPreferenceInfos();
    return infos.toArray( new PreferenceInfo[ infos.size() ] );
  }

  @Override
  public void setFocus() {
    control.setFocus();
  }
}