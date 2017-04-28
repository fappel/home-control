package com.codeaffine.home.control.admin.app.mock;

class MockPreferenceImplementation implements MockPreference {

  private boolean booleanValue;
  private String stringValue;
  private EnumType enumValue;
  private int intValue;

  MockPreferenceImplementation() {
    intValue = Integer.valueOf( DEFAULT_INT_VALUE ).intValue();
    stringValue = DEFAULT_STRING_VALUE;
    enumValue = EnumType.valueOf( DEFAULT_ENUM_VALUE );
    booleanValue = Boolean.getBoolean( DEFAULT_BOOLEAN_VALUE );
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
}