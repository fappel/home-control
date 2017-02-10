package com.codeaffine.home.control.item;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.type.StringType;

public interface StringItem extends Item<StringItem, StringType> {
  String getStatus( String defaultValue );

  void setStatus( StringType status );
  void setStatus( String status );

  void updateStatus( StringType status );
  void updateStatus( String status );
}