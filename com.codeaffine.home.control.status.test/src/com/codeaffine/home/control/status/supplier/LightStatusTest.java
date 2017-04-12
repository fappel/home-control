package com.codeaffine.home.control.status.supplier;

import static com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition.*;
import static com.codeaffine.test.util.lang.EqualsTester.newInstance;
import static com.codeaffine.test.util.lang.ThrowableCaptor.thrownBy;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.codeaffine.home.control.status.model.SectionProvider.SectionDefinition;
import com.codeaffine.test.util.lang.EqualsTester;

public class LightStatusTest {

  private static final Integer LIGHT_VALUE = Integer.valueOf( 5 );

  @Test
  public void getLightValue() {
    LightStatus lightStatus = new LightStatus( createStatusMap( BED, LIGHT_VALUE ) );

    int actual = lightStatus.getLightValue( BED );

    assertThat( actual ).isEqualTo( LIGHT_VALUE );
  }

  @Test
  public void getLightValueOfSectionThatIsNotMapped() {
    LightStatus lightStatus = new LightStatus( emptyMap() );

    int actual = lightStatus.getLightValue( BED );

    assertThat( actual ).isEqualTo( LightStatus.FALL_BACK_LIGHT_VALUE );
  }

  @Test
  public void getLightValueIfStatusMapChangesAfterInitialization() {
    HashMap<SectionDefinition, Integer> statusMap = new HashMap<>();
    LightStatus lightStatus = new LightStatus( statusMap );
    statusMap.put( BED, LIGHT_VALUE );

    int actual = lightStatus.getLightValue( BED );

    assertThat( actual ).isEqualTo( LightStatus.FALL_BACK_LIGHT_VALUE );
  }

  @Test
  public void getLightValueWithNullAsSectionDefinitionArgument() {
    LightStatus lightStatus = new LightStatus( emptyMap() );

    Throwable actual = thrownBy( () -> lightStatus.getLightValue( null ) );

    assertThat( actual ).isInstanceOf( IllegalArgumentException.class );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsStatusMapArgument() {
    new LightStatus( null );
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<LightStatus> tester = newInstance( new LightStatus( createStatusMap( BED, LIGHT_VALUE ) ) );
    tester.assertImplementsEqualsAndHashCode();

    LightStatus status = new LightStatus( emptyMap() );
    status.getLightValue( BATH_ROOM );
    tester.assertEqual( status, new LightStatus( emptyMap() ) );
    tester.assertEqual( new LightStatus( createStatusMap( BED_SIDE, LIGHT_VALUE ) ),
                        new LightStatus( createStatusMap( BED_SIDE, LIGHT_VALUE ) ) );

    tester.assertNotEqual( new LightStatus( createStatusMap( BED, LIGHT_VALUE ) ),
                           new LightStatus( createStatusMap( BED_SIDE, LIGHT_VALUE ) ) );
    tester.assertNotEqual( new LightStatus( createStatusMap( BED, LIGHT_VALUE ) ),
                           new LightStatus( createStatusMap( BED, LIGHT_VALUE + 1 ) ));
  }

  @Test
  public void toStringImplementation() {
    String actual = new LightStatus( createStatusMap( BED, LIGHT_VALUE ) ) .toString();

    assertThat( actual ).contains( BED.toString(), LIGHT_VALUE.toString() );
  }

  private static Map<SectionDefinition, Integer> createStatusMap(
    SectionDefinition sectionDefinition, Integer lightValue )
  {
    Map<SectionDefinition, Integer> result = new HashMap<>();
    result.put( sectionDefinition, lightValue );
    return result;
  }
}