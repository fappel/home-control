package com.codeaffine.home.control.admin.ui.view;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.preference.PreferenceValue;

public class PageOrderValue implements PreferenceValue<PageOrderValue> {

  private final String representation;

  public static PageOrderValue valueOf( String representation ) {
    verifyNotNull( representation, "representation" );

    return new PageOrderValue( representation );
  }

  public static PageOrderValue[] values() {
    return new PageStorage()
      .getPages()
      .stream()
      .map( page -> new PageOrderValue( page.getLabel() ) )
      .sorted()
      .toArray( PageOrderValue[]::new );
  }

  @Override
  public int compareTo( PageOrderValue other ) {
    verifyNotNull( other, "other" );

    return representation.compareTo( other.representation );
  }

  @Override
  public String toString() {
    return representation;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + representation.hashCode();
    return result;
  }

  @Override
  public boolean equals( Object obj ) {
    if( this == obj )
      return true;
    if( obj == null )
      return false;
    if( getClass() != obj.getClass() )
      return false;
    PageOrderValue other = ( PageOrderValue )obj;
    if( !representation.equals( other.representation ) )
      return false;
    return true;
  }

  private PageOrderValue( String representation ) {
    this.representation = representation;
  }
}