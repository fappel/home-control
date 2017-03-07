package com.codeaffine.home.control.item;

import com.codeaffine.home.control.AdjustableItem;
import com.codeaffine.home.control.type.DecimalType;

public interface NumberItem
  extends AdjustableItem<NumberItem, DecimalType>,
          NumberAccessor<DecimalType>
{
}
