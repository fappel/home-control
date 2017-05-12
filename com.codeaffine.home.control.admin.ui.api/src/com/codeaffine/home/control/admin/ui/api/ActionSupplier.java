package com.codeaffine.home.control.admin.ui.api;

public interface ActionSupplier {
  Runnable getAction( Object actionId );
}