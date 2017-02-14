package com.codeaffine.home.control.item;

import com.codeaffine.home.control.AdjustableItem;
import com.codeaffine.home.control.type.OnOffType;

public interface SwitchItem
  extends AdjustableItem<SwitchItem, OnOffType>
{
  OnOffType getStatus( OnOffType defaultValue );
  void updateStatus( OnOffType status );
}
