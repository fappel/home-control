package com.codeaffine.home.control;

public interface Registry {
  <T extends Item<? extends Status>> T getItem( String key, Class<T> itemType );
}