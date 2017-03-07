package com.codeaffine.home.control.item;

import com.codeaffine.home.control.Item;
import com.codeaffine.home.control.type.OpenClosedType;

public interface ContactItem extends Item<ContactItem, OpenClosedType> {
  OpenClosedType getStatus( OpenClosedType defaultValue );
}