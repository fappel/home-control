package com.codeaffine.home.control.engine.component.preference;

import static com.codeaffine.home.control.engine.component.preference.Messages.*;
import static com.codeaffine.home.control.engine.component.preference.MyPreferenceValue.*;
import static com.codeaffine.home.control.engine.component.preference.PreferenceModelImplTest.MyEnum.*;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Maps.newHashMap;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.engine.component.util.TypeUnloadTracker;
import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;
import com.codeaffine.home.control.preference.PreferenceEvent;

public class PreferenceModelImplTest {

  private static final String STRING_VALUE = "value";
  private static final String DEFAULT_INT_VALUE = "23";
  private static final String DEFAULT_LONG_VALUE = "12";
  private static final String DEFAULT_DOUBLE_VALUE = "2.0";
  private static final String DEFAULT_FLOAT_VALUE = "3.0";
  private static final String DEFAULT_BOOLEAN_VALUE = "true";
  private static final String DEFAULT_BYTE_VALUE = "123";
  private static final String DEFAULT_SHORT_VALUE = "111";
  private static final String DEFAULT_STRING_VALUE = "default-value";
  private static final String DEFAULT_MAP_VALUE = "{ONE=1, TWO=2}";
  private static final String DEFAULT_LIST_VALUE = "{ONE, ONE}";
  private static final String DEFAULT_SET_VALUE = "{ONE}";
  private static final String DEFAULT_MY_ENUM_VALUE = "ONE";
  private static final String DEFAULT_EMPTY_COLLECTION = "{}";
  private static final String DEFAULT_MY_PREFERENCE_VALUE = VALUE_TWO_REPRESENTATION;
  private static final boolean BOOLEAN_VALUE = true;
  private static final int INT_VALUE = 10;

  private TypeUnloadTracker typeUnloadTracker;
  private PreferenceModelImpl preferenceModel;
  private EventBus eventBus;

  enum MyEnum { ONE, TWO }

  @Preference
  interface MyPreference {
    @DefaultValue( DEFAULT_INT_VALUE )
    int getIntValue();
    void setIntValue( int value );
    @DefaultValue( DEFAULT_LONG_VALUE )
    long getLongValue();
    void setLongValue( long value );
    @DefaultValue( DEFAULT_DOUBLE_VALUE )
    double getDoubleValue();
    void setDoubleValue( double value );
    @DefaultValue( DEFAULT_FLOAT_VALUE )
    float getFloatValue();
    void setFloatValue( float value );
    @DefaultValue( DEFAULT_BOOLEAN_VALUE )
    boolean isBooleanValue();
    void setBooleanValue( boolean value );
    @DefaultValue( DEFAULT_BYTE_VALUE )
    byte getByteValue();
    void setByteValue( byte value );
    @DefaultValue( DEFAULT_SHORT_VALUE )
    short getShortValue();
    void setShortValue( short value );
    @DefaultValue( DEFAULT_STRING_VALUE )
    String getStringValue();
    void setStringValue( String value );
    @DefaultValue( DEFAULT_MY_ENUM_VALUE )
    MyEnum getMyEnumValue();
    void setMyEnumValue( MyEnum value );
    @DefaultValue( DEFAULT_SET_VALUE )
    Set<MyEnum> getSet();
    void setSet( Set<MyEnum> value );
    @DefaultValue( DEFAULT_LIST_VALUE )
    List<MyEnum> getList();
    void setList( List<MyEnum> value );
    @DefaultValue( DEFAULT_MAP_VALUE )
    Map<MyEnum, Integer> getMap();
    void setMap( Map<MyEnum, Integer> value );
    @DefaultValue( DEFAULT_EMPTY_COLLECTION )
    Set<MyEnum> getEmptyDefaultSet();
    void setEmptyDefaultSet( Set<MyEnum> value );
    @DefaultValue( DEFAULT_EMPTY_COLLECTION )
    List<MyEnum> getEmptyDefaultList();
    void setEmptyDefaultList( List<MyEnum> value );
    @DefaultValue( DEFAULT_EMPTY_COLLECTION )
    Map<MyEnum, Integer> getEmptyDefaultMap();
    void setEmptyDefaultMap( Map<MyEnum, Integer> value );
    @DefaultValue( DEFAULT_MY_PREFERENCE_VALUE )
    MyPreferenceValue getMyPreferenceValue();
    void setMyPreferenceValue( MyPreferenceValue value );
    @DefaultValue( "SECONDS" )
    ChronoUnit getChronoUnit();
    void setChronoUnit( ChronoUnit value );
  }

  @Preference
  interface UnsupportedAttributeTypePreference {
    @DefaultValue( "UNSUPPORTED_TYPE" )
    Object getDefaultOfTypeWithoutValueOfFactoryMethod();
    void setDefaultOfTypeWithoutValueOfFactoryMethod( Object value );
  }

  @Preference
  interface PreferenceWithNonAccessorMethod {
    void callNonBeanPropertyConformMethod();
  }

  @Before
  public void setUp() {
    eventBus = mock( EventBus.class );
    typeUnloadTracker = mock( TypeUnloadTracker.class );
    preferenceModel = new PreferenceModelImpl( eventBus, typeUnloadTracker );
  }

  @Test
  public void get() {
    MyPreference actual = preferenceModel.get( MyPreference.class );

    assertThat( actual ).isNotNull();
  }

  @Test
  public void getWithoutPreferenceAnnotatedType() {
    Throwable actual = thrownBy( () -> preferenceModel.get( Runnable.class ) );

    assertThat( actual )
      .hasMessage( format( ERROR_NOT_A_PREFERENCE, Runnable.class.getName() ) )
      .isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void getWithNonInterfaceType() {
    Throwable actual = thrownBy( () -> preferenceModel.get( Object.class ) );

    assertThat( actual )
      .hasMessage( format( ERROR_NOT_A_INTERFACE, Object.class.getName() ) )
      .isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void getSamePreferenceTypeMoreThanOnce() {
    MyPreference first = preferenceModel.get( MyPreference.class );
    MyPreference second = preferenceModel.get( MyPreference.class );

    assertThat( first ).isSameAs( second );
  }

  @Test
  public void setAndGetOfIntValues() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    preference.setIntValue( INT_VALUE );
    int actual = preference.getIntValue();

    assertThat( actual ).isEqualTo( INT_VALUE );
  }

  @Test
  public void setAndGetOfSetValues() {
    MyPreference preference = preferenceModel.get( MyPreference.class );
    Set<MyEnum> expected = new HashSet<>( asList( ONE, TWO ) );

    preference.setSet( expected );
    Set<MyEnum> actual = preference.getSet();

    assertThat( actual )
      .isNotSameAs( expected )
      .isEqualTo( expected );
  }

  @Test
  public void setAndGetOfListValues() {
    MyPreference preference = preferenceModel.get( MyPreference.class );
    List<MyEnum> expected = asList( ONE, TWO );

    preference.setList( expected );
    List<MyEnum> actual = preference.getList();

    assertThat( actual )
      .isNotSameAs( expected )
      .isEqualTo( expected );
  }

  @Test
  public void setAndGetOfMapValues() {
    MyPreference preference = preferenceModel.get( MyPreference.class );
    Map<MyEnum, Integer> expected = new HashMap<>();
    expected.put( ONE, Integer.valueOf( 87 ) );
    expected.put( TWO, Integer.valueOf( 42 ) );

    preference.setMap( expected );
    Map<MyEnum, Integer> actual = preference.getMap();

    assertThat( actual )
      .isNotSameAs( expected )
      .isEqualTo( expected );
  }

  @Test
  public void setAndGetOfPreferenceValueImplementations() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    preference.setMyPreferenceValue( VALUE_ONE );
    MyPreferenceValue actual = preference.getMyPreferenceValue();

    assertThat( actual ).isEqualTo( VALUE_ONE );
  }

  @Test
  public void getIntValueIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    int actual = preference.getIntValue();

    assertThat( actual ).isEqualTo( Integer.valueOf( DEFAULT_INT_VALUE ) );
  }

  @Test
  public void getSetIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );
    Set<MyEnum> expected = new HashSet<>( asList( ONE ));

    Set<MyEnum> actual = preference.getSet();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getListIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );
    List<MyEnum> expected = asList( ONE, ONE );

    List<MyEnum> actual = preference.getList();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getEmptyCollectionDefaults() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    assertThat( preference.getEmptyDefaultSet() ).isEmpty();
    assertThat( preference.getEmptyDefaultList() ).isEmpty();
    assertThat( preference.getEmptyDefaultMap() ).isEmpty();
  }

  @Test
  public void getMapIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );
    Map<MyEnum, Integer> expected = new HashMap<>();
    expected.put( ONE, Integer.valueOf( 1 ) );
    expected.put( TWO, Integer.valueOf( 2 ) );

    Map<MyEnum, Integer> actual = preference.getMap();

    assertThat( actual ).isEqualTo( expected );
  }

  @Test
  public void getLongValueIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    long actual = preference.getLongValue();

    assertThat( actual ).isEqualTo( Long.valueOf( DEFAULT_LONG_VALUE ) );
  }

  @Test
  public void getDoubleValueIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    double actual = preference.getDoubleValue();

    assertThat( actual ).isEqualTo( Double.valueOf( DEFAULT_DOUBLE_VALUE ) );
  }

  @Test
  public void getFloatValueIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    float actual = preference.getFloatValue();

    assertThat( actual ).isEqualTo( Float.valueOf( DEFAULT_FLOAT_VALUE ) );
  }

  @Test
  public void isBooleanValueIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    boolean actual = preference.isBooleanValue();

    assertThat( actual ).isEqualTo( Boolean.valueOf( DEFAULT_BOOLEAN_VALUE  ) );
  }

  @Test
  public void getByteValueIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    byte actual = preference.getByteValue();

    assertThat( actual ).isEqualTo( Byte.valueOf( DEFAULT_BYTE_VALUE ) );
  }

  @Test
  public void getShortValueIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    short actual = preference.getShortValue();

    assertThat( actual ).isEqualTo( Short.valueOf( DEFAULT_SHORT_VALUE ) );
  }

  @Test
  public void getStringIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    String actual = preference.getStringValue();

    assertThat( actual ).isEqualTo( DEFAULT_STRING_VALUE );
  }

  @Test
  public void getEnumIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    MyEnum actual = preference.getMyEnumValue();

    assertThat( actual ).isEqualTo( ONE );
  }

  @Test
  public void getChronoUnitIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    ChronoUnit actual = preference.getChronoUnit();

    assertThat( actual ).isSameAs( ChronoUnit.SECONDS );
  }

  @Test
  public void getPreferenceValueImplementationIfNotSet() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    MyPreferenceValue actual = preference.getMyPreferenceValue();

    assertThat( actual ).isEqualTo( VALUE_TWO );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getDefaultOfTypeOfPreferenceWithoutValueOfFactoryMethod() {
    preferenceModel.get( UnsupportedAttributeTypePreference.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void invokeANonBeanPropertyConformMethod() {
    preferenceModel.get( PreferenceWithNonAccessorMethod.class );
  }

  @Test
  public void setObjectAttributeValueWithNullAsValueArgument() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    Throwable actual = thrownBy( () -> preference.setStringValue( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test
  public void setAttributeValueWithEventDelegation() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    preference.setStringValue( "oldValue" );
    reset( eventBus );
    preference.setStringValue( "newValue" );

    ArgumentCaptor<PreferenceEvent> captor = forClass( PreferenceEvent.class );
    verify( eventBus ).post( captor.capture() );
    assertThat( captor.getValue().getSource() ).isSameAs( preference );
    assertThat( captor.getValue().getNewValue() ).isEqualTo( "newValue" );
    assertThat( captor.getValue().getOldValue() ).isEqualTo( "oldValue" );
    assertThat( captor.getValue().getAttributeName() ).isEqualTo( "stringValue" );
  }

  @Test
  public void setAttributeValueWithEventDelegationOnMap() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    Map<MyEnum, Integer> initialValue = newHashMap( TWO, INT_VALUE );
    preference.setMap( initialValue );
    reset( eventBus );
    Map<MyEnum, Integer> newValue = newHashMap( ONE, INT_VALUE );
    preference.setMap( newValue );

    ArgumentCaptor<PreferenceEvent> captor = forClass( PreferenceEvent.class );
    verify( eventBus ).post( captor.capture() );
    assertThat( captor.getValue().getSource() ).isSameAs( preference );
    assertThat( captor.getValue().getNewValue() ).isEqualTo( newValue );
    assertThat( captor.getValue().getOldValue() ).isEqualTo( initialValue );
    assertThat( captor.getValue().getAttributeName() ).isEqualTo( "map" );
  }

  @Test
  public void setAttributeValueIfValueDoesNotChange() {
    MyPreference preference = preferenceModel.get( MyPreference.class );

    preference.setStringValue( STRING_VALUE );
    reset( eventBus );
    preference.setStringValue( STRING_VALUE );

    verify( eventBus, never() ).post( any( PreferenceEvent.class ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void getWithNullAsPreferenceTypeArgument() {
    preferenceModel.get( null );
  }

  @Test
  public void getAllPreferenceTypes() {
    preferenceModel.get( MyPreference.class );

    Set<Class<?>> actual = preferenceModel.getAllPreferenceTypes();

    assertThat( actual ).containsExactly( MyPreference.class );
  }

  @Test
  public void getAllPreferenceTypesIfModelIsEmpty() {
    Set<Class<?>> actual = preferenceModel.getAllPreferenceTypes();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getAllPreferenceTypesAgainAfterPreviousCallResultHasBeenAltered() {
    preferenceModel.get( MyPreference.class );

    Set<Class<?>> first = preferenceModel.getAllPreferenceTypes();
    first.remove( MyPreference.class );
    Set<Class<?>> second = preferenceModel.getAllPreferenceTypes();

    assertThat( second ).containsExactly( MyPreference.class );
  }

  @Test
  public void getAllPreferenceTypesAndChangeModelAfterwards() {
    Set<Class<?>> first = preferenceModel.getAllPreferenceTypes();
    preferenceModel.get( MyPreference.class );
    Set<Class<?>> second = preferenceModel.getAllPreferenceTypes();

    assertThat( first ).isEmpty();
    assertThat( second ).containsExactly( MyPreference.class );
  }

  @Test
  public void unloadPreferenceClass() {
    preferenceModel.get( MyPreference.class );
    Runnable deactivationHook = captureUnloadHook();

    deactivationHook.run();
    Set<Class<?>> actual = preferenceModel.getAllPreferenceTypes();

    assertThat( actual ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsEventBusArgument() {
    new PreferenceModelImpl( null, typeUnloadTracker );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsTypeUnloadTrackerArgument() {
    new PreferenceModelImpl( eventBus, null );
  }

  @Test
  public void toStringImplementation() {
    MyPreference preference = preferenceModel.get( MyPreference.class );
    preference.setStringValue( STRING_VALUE );
    preference.setIntValue( INT_VALUE );
    preference.setBooleanValue( BOOLEAN_VALUE );
    preference.setMyEnumValue( TWO );

    String actual = preference.toString();

    assertThat( actual )
      .contains( MyPreference.class.getSimpleName(),
                 "stringValue=" + STRING_VALUE,
                 "intValue=" + String.valueOf( INT_VALUE ),
                 "myEnumValue=" + TWO,
                 "booleanValue=" + String.valueOf( BOOLEAN_VALUE ),
                 "shortValue=" + DEFAULT_SHORT_VALUE,
                 "list=[ONE, ONE]",
                 "set=[ONE]" );
  }

  @Test
  public void saveAndLoad() {
    MyPreference preference = preferenceModel.get( MyPreference.class );
    preference.setStringValue( STRING_VALUE );
    preference.setIntValue( INT_VALUE );
    preference.setBooleanValue( BOOLEAN_VALUE );
    preference.setMyEnumValue( ONE );
    preference.setMyPreferenceValue( VALUE_ONE );

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    preferenceModel.save( out );
    ByteArrayInputStream in = new ByteArrayInputStream( out.toByteArray() );
    PreferenceModelImpl otherModel = new PreferenceModelImpl( eventBus, typeUnloadTracker );
    otherModel.load( in );
    MyPreference otherPreferenceInstance = otherModel.get( MyPreference.class );

    assertThat( otherPreferenceInstance.getStringValue() ).isEqualTo( STRING_VALUE );
    assertThat( otherPreferenceInstance.getIntValue() ).isEqualTo( INT_VALUE );
    assertThat( otherPreferenceInstance.isBooleanValue() ).isTrue();
    assertThat( otherPreferenceInstance.getMyEnumValue() ).isSameAs( ONE );
    assertThat( otherPreferenceInstance.getMyPreferenceValue() ).isSameAs( VALUE_ONE );
  }

  @Test
  public void saveWithProblem() throws IOException {
    IOException cause = new IOException();
    OutputStream out = stubOutputStream( cause );

    Throwable actual = thrownBy( () -> preferenceModel.save( out ) );

    assertThat( actual )
      .isInstanceOf( IllegalStateException.class )
      .hasCause( cause );
  }

  @Test
  public void loadFromEmptyInputStream() {
    MyPreference preference = preferenceModel.get( MyPreference.class );
    preference.setStringValue( STRING_VALUE );

    preferenceModel.load( new ByteArrayInputStream( new byte[ 0 ] ) );
    String actual = preference.getStringValue();

    assertThat( actual ).isEqualTo( STRING_VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void loadWithNullAsInArgument() {
    preferenceModel.load( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void saveWithNullAsOutArgument() {
    preferenceModel.save( null );
  }

  private Runnable captureUnloadHook() {
    ArgumentCaptor<Runnable> captor = forClass( Runnable.class );
    verify( typeUnloadTracker ).registerUnloadHook( eq( MyPreference.class ), captor.capture() );
    return captor.getValue();
  }

  private static OutputStream stubOutputStream( IOException cause ) throws IOException {
    OutputStream result = mock( OutputStream.class );
    doThrow( cause ).when( result ).write( any( byte[].class ), anyInt(), anyInt() );
    return result;
  }
}