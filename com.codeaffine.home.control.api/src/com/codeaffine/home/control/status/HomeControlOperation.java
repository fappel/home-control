package com.codeaffine.home.control.status;

import java.util.Collection;

public interface HomeControlOperation {
  void reset();
  void executeOn( StatusEvent event );
  Collection<Class<? extends StatusSupplier<?>>> getRelatedStatusSupplierTypes();
}