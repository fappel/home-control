package com.codeaffine.home.control.status.type;

import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;

import com.codeaffine.home.control.status.type.OnOff;
import com.codeaffine.home.control.status.type.Percent;
import com.codeaffine.home.control.status.type.TypeConverter;
import com.codeaffine.home.control.type.OnOffType;
import com.codeaffine.home.control.type.PercentType;


public class TypeConverterTest {

  @Test
  public void convertFromOnOffValueOn() {
    OnOffType actual = TypeConverter.convertFromOnOff( OnOff.ON );

    assertThat( actual ).isSameAs( OnOffType.ON );
  }

  @Test
  public void convertFromOnOffValueOff() {
    OnOffType actual = TypeConverter.convertFromOnOff( OnOff.OFF );

    assertThat( actual ).isSameAs( OnOffType.OFF );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertFromOnOffWithNullAsValueArgument() {
    TypeConverter.convertFromOnOff( null );
  }

  @Test
  public void convertToOnOffFromValueOn() {
    OnOff actual = TypeConverter.convertToOnOff( Optional.of( OnOffType.ON ), OnOffType.OFF );

    assertThat( actual ).isSameAs( OnOff.ON );
  }

  @Test
  public void convertToOnOffFromValueOff() {
    OnOff actual = TypeConverter.convertToOnOff( Optional.of( OnOffType.OFF ), OnOffType.ON );

    assertThat( actual ).isSameAs( OnOff.OFF );
  }

  @Test
  public void convertToOnOffFromEmptyValue() {
    OnOff actual = TypeConverter.convertToOnOff( empty(), OnOffType.ON );

    assertThat( actual ).isSameAs( OnOff.ON );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToOnOffWithNullAsValueArgument() {
    TypeConverter.convertToOnOff( null, OnOffType.OFF );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToOnOffWithNullAsDefaultValueArgument() {
    TypeConverter.convertToOnOff( empty(), null );
  }

  @Test
  public void convertFromPercent() {
    PercentType actual = TypeConverter.convertFromPercent( Percent.P_100 );

    assertThat( actual ).isEqualTo( PercentType.HUNDRED );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertFromPercentWithNullAsValueArgument() {
    TypeConverter.convertFromPercent( null );
  }

  @Test
  public void convertToPercent() {
    Percent actual = TypeConverter.convertToPercent( Optional.of( PercentType.ZERO ), PercentType.HUNDRED );

    assertThat( actual ).isSameAs( Percent.P_000 );
  }

  @Test
  public void convertToPercentWithEmptyValue() {
    Percent actual = TypeConverter.convertToPercent( empty(), PercentType.ZERO );

    assertThat( actual ).isSameAs( Percent.P_000 );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToPercentWithNullAsValueArgument() {
    TypeConverter.convertToPercent( null, PercentType.HUNDRED );
  }

  @Test( expected = IllegalArgumentException.class )
  public void convertToPercentWithNullAsDefaultValueArgument() {
    TypeConverter.convertToPercent( empty(), null );
  }
}