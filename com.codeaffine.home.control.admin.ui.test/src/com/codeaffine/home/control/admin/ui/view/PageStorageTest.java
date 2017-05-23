package com.codeaffine.home.control.admin.ui.view;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.rap.rwt.RWT.getUISession;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.api.Page;
import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;

public class PageStorageTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private PageStorage storage;

  @Before
  public void setUp() {
    storage = new PageStorage();
  }

  @Test
  public void register() {
    Page expected = mock( Page.class );

    storage.register( asList( expected ) );
    List<Page> actual = storage.getPages();

    assertThat( actual ).containsExactly( expected );
  }

  @Test
  public void clear() {
    Page expected = mock( Page.class );
    storage.register( asList( expected ) );

    storage.clear();
    List<Page> actual = storage.getPages();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getPagesIfNoPagesWereRegistered() {
    List<Page> actual = storage.getPages();

    assertThat( actual ).isEmpty();
  }

  @Test
  public void getPagesAfterChangingPreviouslyReturnedList() {
    Page expected = mock( Page.class );
    storage.register( asList( expected ) );

    storage.getPages().add( mock( Page.class ) );
    List<Page> actual = storage.getPages();

    assertThat( actual ).containsExactly( expected );
  }

  @Test
  public void getPagesAfterChangingValuesOfInputList() {
    Page expected = mock( Page.class );
    List<Page> input = new ArrayList<>( asList( expected ) );
    storage.register( input );
    input.add( mock( Page.class ) );

    List<Page> actual = storage.getPages();

    assertThat( actual ).containsExactly( expected );
  }

  @Test
  public void getPagesAfterUiSessionInvalidation() {
    Page expected = mock( Page.class );
    storage.register( asList( expected ) );

    simulateUiSessionInvalidation();
    List<Page> actual = storage.getPages();

    assertThat( actual ).isEmpty();
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerWithNullAsPagesArgument() {
    storage.register( null );
  }

  private static void simulateUiSessionInvalidation() {
    getUISession().removeAttribute( PageStorage.KEY );
  }
}