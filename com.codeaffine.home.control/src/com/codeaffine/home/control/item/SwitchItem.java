package com.codeaffine.home.control.item;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.type.OnOffType;

public interface SwitchItem extends Item<SwitchItem, OnOffType> {
  OnOffType getStatus( OnOffType defaultValue );
  void setStatus( OnOffType status );
  void updateStatus( OnOffType status );
}
