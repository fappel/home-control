package com.codeaffine.home.control.item;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.type.StringType;

public interface StringItem extends Item<StringType> {
  String getStatus( String defaultValue );

  void setStatus( StringType status );
  void setStatus( String status );

  void sendStatus( StringType status );
  void sendStatus( String status );
}