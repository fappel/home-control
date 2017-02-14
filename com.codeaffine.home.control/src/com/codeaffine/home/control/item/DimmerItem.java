package com.codeaffine.home.control.item;

import com.codeaffine.home.control.AdjustableItem;
import com.codeaffine.home.control.type.PercentType;


public interface DimmerItem
  extends AdjustableItem<DimmerItem, PercentType>,
          NumberAccessor<PercentType>
{
}
