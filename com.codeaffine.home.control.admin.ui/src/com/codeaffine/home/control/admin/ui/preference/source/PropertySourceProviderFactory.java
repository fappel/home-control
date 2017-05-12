package com.codeaffine.home.control.admin.ui.preference.source;

import static com.codeaffine.home.control.admin.ui.preference.source.Messages.ERROR_UNSUPPORTED_COLLECTION_VALUE_TYPE;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.preference.collection.CollectionValue;
import com.codeaffine.home.control.admin.ui.preference.collection.ListObjectInfo;
import com.codeaffine.home.control.admin.ui.preference.collection.MapObjectInfo;
import com.codeaffine.home.control.admin.ui.preference.collection.ModifyAdapter;
import com.codeaffine.home.control.admin.ui.preference.collection.SetObjectInfo;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertySource;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertySourceProvider;

public class PropertySourceProviderFactory {

  public IPropertySourceProvider create( ModifyAdapter modifyAdapter ) {
    verifyNotNull( modifyAdapter, "modifyAdapter" );

    return object -> getPropertySource( object, modifyAdapter );
  }

  private static IPropertySource getPropertySource( Object object, ModifyAdapter modifyAdapter ) {
    if( object instanceof IPropertySource ) {
      return ( IPropertySource )object;
    }
    if( object instanceof PreferenceInfo ) {
      return new AttributePropertySource( new PreferenceObjectInfo( ( PreferenceInfo )object, modifyAdapter ) );
    }
    if( object instanceof CollectionValue ) {
      return getPropertySource( ( CollectionValue )object, modifyAdapter );
    }
    return new ValuePropertySource( object );
  }

  private static IPropertySource getPropertySource( CollectionValue collectionValue, ModifyAdapter modifyAdapter ) {
    if( collectionValue.getValue() instanceof Map ) {
      return new AttributePropertySource( new MapObjectInfo( collectionValue, modifyAdapter ) );
    }
    if( collectionValue.getValue() instanceof List ) {
      return new AttributePropertySource( new ListObjectInfo( collectionValue, modifyAdapter ) );
    }
    if( collectionValue.getValue() instanceof Set ) {
      return new AttributePropertySource( new SetObjectInfo( collectionValue, modifyAdapter ) );
    }
    String typeName = collectionValue.getValue().getClass().getName();
    throw new IllegalStateException( format( ERROR_UNSUPPORTED_COLLECTION_VALUE_TYPE, typeName ) );
  }
}