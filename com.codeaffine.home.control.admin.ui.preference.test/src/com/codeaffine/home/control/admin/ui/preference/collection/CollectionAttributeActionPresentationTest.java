package com.codeaffine.home.control.admin.ui.preference.collection;

import static com.codeaffine.home.control.admin.ui.preference.collection.CollectionAttributeActionPresentation.values;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith( Parameterized.class )
public class CollectionAttributeActionPresentationTest {

  @Parameter
  public CollectionAttributeActionPresentation element;

  @Parameters
  public static Collection<Object[]> data() {
    return Stream.of( values() ).map( element -> new Object[] { element } ).collect( toList() );
  }

  @Test
  public void elementInitialization() {
    assertThat( element.getLabel() ).isNotNull();
    assertThat( element.getStyle() ).isGreaterThan( 0 );
  }

}