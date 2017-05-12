package com.codeaffine.home.control.admin.ui.api;

import java.util.List;

public interface PageFactorySupplier {
  List<PageFactory> getPageFactories();
  void deregisterUpdateHook( Runnable updateHook );
  void registerUpdateHook( Runnable updateHook );
}