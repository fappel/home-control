package com.codeaffine.home.control.status.supplier;

import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import java.util.HashMap;
import java.util.Map;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;

public class LightStatus {

  static final Integer FALL_BACK_LIGHT_VALUE = Integer.valueOf( 0 );

  private final Map<SectionDefinition, Integer> statusMap;

  public LightStatus( Map<SectionDefinition, Integer> statusMap ) {
    verifyNotNull( statusMap, "statusMap" );

    this.statusMap = new HashMap<>( statusMap );
  }

  public int getLightValue( SectionDefinition sectionDefinition ) {
    verifyNotNull( sectionDefinition, "sectionDefinition" );

    if( statusMap.containsKey( sectionDefinition ) ) {
      return statusMap.get( sectionDefinition ).intValue();
    }
    return FALL_BACK_LIGHT_VALUE.intValue();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + statusMap.hashCode();
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
    LightStatus other = ( LightStatus )obj;
    if( !statusMap.equals( other.statusMap ) )
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "LightStatus [ " + statusMap.toString().replaceAll( "\\{", "" ).replaceAll( "\\}", "" ) + " ]";
  }
}