package com.codeaffine.home.control.admin.ui.view;

import static com.codeaffine.test.util.lang.EqualsTester.newInstance;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.api.Page;
import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;
import com.codeaffine.test.util.lang.EqualsTester;

public class PageOrderValueTest {

  private static final String PAGE_LABEL_1 = "page1";
  private static final String PAGE_LABEL_2 = "page2";
  private static final String SOME_LABEL = "someLabel";
  private static final Page PAGE_1 = stubPage( PAGE_LABEL_1 );
  private static final Page PAGE_2 = stubPage( PAGE_LABEL_2 );

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private PageStorage pageStorage;

  @Before
  public void setUp() {
    pageStorage = new PageStorage();
    pageStorage.register( asList( PAGE_2, PAGE_1 ) );
  }

  @Test
  public void valueOf() {
    PageOrderValue actual = PageOrderValue.valueOf( SOME_LABEL );

    assertThat( actual.toString() ).isEqualTo( SOME_LABEL );
  }

  @Test
  public void values() {
    PageOrderValue[] values = PageOrderValue.values();

    assertThat( values )
      .containsExactly( PageOrderValue.valueOf( PAGE_LABEL_1 ), PageOrderValue.valueOf( PAGE_LABEL_2 ) );
  }

  @Test
  public void compareTo() {
    PageOrderValue first = PageOrderValue.valueOf( "a" );
    PageOrderValue second = PageOrderValue.valueOf( "b" );
    assertThat( first.compareTo( second ) ).isLessThan( 0 );
    assertThat( second.compareTo( second ) ).isEqualTo( 0 );
    assertThat( second.compareTo( first ) ).isGreaterThan( 0 );
  }

  @Test
  public void toStringImplementation() {
    PageOrderValue value = PageOrderValue.valueOf( SOME_LABEL );

    String actual = value.toString();

    assertThat( actual ).isEqualTo( SOME_LABEL );
  }

  @Test
  public void equalsAndHashcode() {
    EqualsTester<PageOrderValue> tester = newInstance( PageOrderValue.valueOf( SOME_LABEL ) );
    tester.assertImplementsEqualsAndHashCode();
    tester.assertEqual( PageOrderValue.valueOf( PAGE_LABEL_1 ), PageOrderValue.valueOf( PAGE_LABEL_1 ) );
    tester.assertNotEqual( PageOrderValue.valueOf( PAGE_LABEL_1 ), PageOrderValue.valueOf( PAGE_LABEL_2 ) );
  }

  @Test( expected = IllegalArgumentException.class )
  public void compareToWithNullAsOtherArgument() {
    PageOrderValue.values()[ 0 ].compareTo( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void valueOfWithNullAsRepresentationArgument() {
    PageOrderValue.valueOf( null );
  }

  private static Page stubPage( String label ) {
    Page result = mock( Page.class );
    when( result.getLabel() ).thenReturn( label );
    return result;
  }
}