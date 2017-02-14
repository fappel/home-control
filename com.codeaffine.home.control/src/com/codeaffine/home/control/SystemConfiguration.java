package com.codeaffine.home.control;

import com.codeaffine.home.control.entity.EntityProvider.EntityProviderConfiguration;
import com.codeaffine.home.control.entity.EntityRelationProvider.EntityRelationConfiguration;

public interface SystemConfiguration extends EntityRelationConfiguration, EntityProviderConfiguration {
  void configureSystem( Context context );
}