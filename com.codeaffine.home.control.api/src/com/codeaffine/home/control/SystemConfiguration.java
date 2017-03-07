package com.codeaffine.home.control;

import com.codeaffine.home.control.entity.EntityProvider.EntityProviderConfiguration;
import com.codeaffine.home.control.entity.EntityRelationProvider.EntityRelationConfiguration;
import com.codeaffine.home.control.status.ControlCenter.ControlCenterConfiguration;

public interface SystemConfiguration
  extends EntityRelationConfiguration,
          EntityProviderConfiguration,
          ControlCenterConfiguration
{
  default void configureSystem( @SuppressWarnings("unused") Context context ) {}
}