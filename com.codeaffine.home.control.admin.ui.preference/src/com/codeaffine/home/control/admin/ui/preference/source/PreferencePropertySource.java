package com.codeaffine.home.control.admin.ui.preference.source;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.stream.Stream;

import com.codeaffine.home.control.admin.PreferenceInfo;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertyDescriptor;
import com.codeaffine.home.control.admin.ui.util.viewer.property.IPropertySource;
import com.codeaffine.home.control.admin.ui.util.viewer.property.PropertyDescriptor;

public class PreferencePropertySource implements IPropertySource {

  private final Map<String, PreferenceInfo> infos;

  public PreferencePropertySource( PreferenceInfo ... infos ) {
    verifyNotNull( infos, "infos" );

    this.infos = Stream.of( infos ).collect( toMap( key -> key.getName(), value -> value ) );

  }

  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    return infos
      .keySet()
      .stream()
      .map( attributeName -> new PropertyDescriptor( attributeName, attributeName ) )
      .toArray( IPropertyDescriptor[]::new );
  }

  @Override
  public boolean isPropertySet( Object id ) {
    verifyNotNull( id, "id" );

    return true;
  }

  @Override
  public Object getPropertyValue( Object id ) {
    verifyNotNull( id, "id" );

    return infos.get( id );
  }

  @Override
  public void resetPropertyValue( Object id ) {
  }

  @Override
  public void setPropertyValue( Object id, Object value ) {
  }

  @Override
  public Object getEditableValue() {
    return null;
  }
}