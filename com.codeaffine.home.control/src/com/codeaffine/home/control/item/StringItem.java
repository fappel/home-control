package com.codeaffine.home.control.item;

import com.codeaffine.home.control.AdjustableItem;
import com.codeaffine.home.control.type.StringType;

public interface StringItem
  extends AdjustableItem<StringItem, StringType>
{
  String getStatus( String defaultValue );

  void setStatus( String status );

  void updateStatus( String status );
}