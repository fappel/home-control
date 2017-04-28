package com.codeaffine.home.control.admin;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toSet;

import java.util.Set;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.preference.PreferenceModel;

public class PreferenceIntrospection {

  private final ComponentAccessService service;
  private final PreferenceModel model;

  PreferenceIntrospection( PreferenceModel model, ComponentAccessService componentAccessService ) {
    verifyNotNull( componentAccessService, "componentAccessService" );
    verifyNotNull( model, "model" );

    this.service = componentAccessService;
    this.model = model;
   }

  public Set<PreferenceInfo> getPreferenceInfos() {
    return model
      .getAllPreferenceTypes()
      .stream()
      .map( type -> new PreferenceInfo( type, model.get( type ), service ) )
      .collect( toSet() );
  }
}