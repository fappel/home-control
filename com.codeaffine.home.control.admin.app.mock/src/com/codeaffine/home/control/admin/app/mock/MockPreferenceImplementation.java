package com.codeaffine.home.control.admin.app.mock;

import static java.util.stream.Collectors.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

class MockPreferenceImplementation implements MockPreference {

  private boolean booleanValue;
  private String stringValue;
  private EnumType enumValue;
  private int intValue;
  private Map<EnumType, Integer> mapValue;
  private List<EnumType> listValue;
  private Set<EnumType> setValue;

  MockPreferenceImplementation() {
    intValue = Integer.valueOf( DEFAULT_INT_VALUE ).intValue();
    stringValue = DEFAULT_STRING_VALUE;
    enumValue = EnumType.valueOf( DEFAULT_ENUM_VALUE );
    booleanValue = Boolean.getBoolean( DEFAULT_BOOLEAN_VALUE );
    mapValue = initializeMapValue();
    listValue = initializeListValue();
    setValue = initializeSetValue();
  }

  @Override
  public void setIntValue( int intValue ) {
    this.intValue = intValue;
  }

  @Override
  public int getIntValue() {
    return intValue;
  }

  @Override
  public String getStringValue() {
    return stringValue;
  }

  @Override
  public void setStringValue( String stringValue ) {
    this.stringValue = stringValue;
  }

  @Override
  public EnumType getEnumValue() {
    return enumValue;
  }

  @Override
  public void setEnumValue( EnumType enumValue ) {
    this.enumValue = enumValue;
  }

  @Override
  public boolean isBooleanValue() {
    return booleanValue;
  }

  @Override
  public void setBooleanValue( boolean booleanValue ) {
    this.booleanValue = booleanValue;
  }

  @Override
  public Map<EnumType, Integer> getMapValue() {
    return mapValue;
  }

  @Override
  public List<EnumType> getListValue() {
    return listValue;
  }

  @Override
  public void setListValue( List<EnumType> listValue ) {
    this.listValue = listValue;
  }

  @Override
  public Set<EnumType> getSetValue() {
    return setValue;
  }

  @Override
  public void setSetValue( Set<EnumType> setValue ) {
    this.setValue = setValue;
  }

  @Override
  public void setMapValue( Map<EnumType, Integer> mapValue ) {
    this.mapValue = mapValue;
  }

  private static Map<EnumType, Integer> initializeMapValue() {
    return Stream.of( EnumType.values() ).collect( toMap( key -> key, key -> computeMapValue( key ) ) );
  }

  private static Integer computeMapValue( EnumType key ) {
    return Integer.valueOf( ( key.ordinal() + 2 ) * 2 );
  }

  private static List<EnumType> initializeListValue() {
    return Stream.of( EnumType.values() ).sorted( ( e1, e2 ) -> -e1.compareTo( e2 ) ).collect( toList() );
  }

  private static Set<EnumType> initializeSetValue() {
    return Stream.of( EnumType.values() ).collect( toSet() );
  }
}