package com.codeaffine.home.control.status.test.util.supplier;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.item.StringItem;
import com.codeaffine.home.control.status.internal.scene.NamedSceneSupplierImpl;
import com.codeaffine.home.control.status.supplier.NamedSceneSupplier;
import com.codeaffine.home.control.type.StringType;

public class NamedSceneSupplierTestProvider {

  private final Context context;

  private NamedSceneSupplierImpl supplier;

  public NamedSceneSupplierTestProvider( Context context ) {
    this.context = context;
  }

  public NamedSceneSupplier get() {
    supplier = context.create( NamedSceneSupplierImpl.class );
    return supplier;
  }

  public void onActiveSceneItemChange( ChangeEvent<StringItem, StringType> event ) {
    supplier.onActiveSceneItemChange( event );
  }
}