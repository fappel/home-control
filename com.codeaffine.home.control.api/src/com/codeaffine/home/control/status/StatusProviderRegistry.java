package com.codeaffine.home.control.status;

import com.codeaffine.home.control.Context;

public interface StatusProviderRegistry {
  <T extends StatusProvider<?>> void register( Class<T> type, Class<? extends T> implementation );
  Context getContext();
}