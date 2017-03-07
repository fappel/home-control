package com.codeaffine.home.control;

public interface Registry {
  <I extends Item<I,? extends Status>> I getItem( String key, Class<I> itemType );
}