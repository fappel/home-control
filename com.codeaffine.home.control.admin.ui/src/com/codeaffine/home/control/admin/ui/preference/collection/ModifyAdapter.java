package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.AddElementDialog.*;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

public class ModifyAdapter {

  private final Runnable updateAction;
  private final Shell dialogParent;

  public ModifyAdapter( Shell dialogParent, Runnable updateAction ) {
    verifyNotNull( dialogParent, "dialogParent" );
    verifyNotNull( updateAction, "updateAction" );

    this.dialogParent = dialogParent;
    this.updateAction = updateAction;
  }

  public void triggerUpdate() {
    updateAction.run();
  }

  public void handleAddition( CollectionValue collectionValue ) {
    AddElementDialog dialog = new AddElementDialog( dialogParent, collectionValue );
    dialogParent.getDisplay().asyncExec( () -> {
      dialog.open( returnCode -> {
        if( returnCode == SWT.OK ) {
          handleAddition( collectionValue, dialog );
        }
      } );
    } );
  }

  @SuppressWarnings("unchecked")
  private void handleAddition( CollectionValue collectionValue, AddElementDialog dialog ) {
    Class<?> attributeType = collectionValue.getAttributeInfo().getAttributeType();
    Object value = null;
    Map<String, Object> valueDefinition = dialog.getElementValueDefinition();
    if( attributeType == Set.class ) {
      Set<Object> set = new HashSet<>( ( Set<Object> )collectionValue.getValue() );
      set.add( valueDefinition.get( VALUE ) );
      value = set;
    }
    if( attributeType == List.class ) {
      List<Object> list = new ArrayList<>( ( List<Object> )collectionValue.getValue() );
      list.add( valueDefinition.get( VALUE ) );
      value = list;
    }
    if( attributeType == Map.class ) {
      Map<Object, Object> map = new HashMap<>( ( Map<Object, Object> )collectionValue.getValue() );
      map.put( valueDefinition.get( KEY ), valueDefinition.get( VALUE ) );
      value = map;
    }
    if( value != null ) {
      collectionValue.getObjectInfo().setAttributeValue( collectionValue.getAttributeInfo().getName(), value );
      triggerUpdate();
    }
  }
}