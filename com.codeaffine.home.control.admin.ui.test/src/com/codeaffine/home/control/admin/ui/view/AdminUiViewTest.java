package com.codeaffine.home.control.admin.ui.view;

import static com.codeaffine.home.control.admin.ui.view.UiActions.activatePage;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;

import com.codeaffine.home.control.admin.ui.api.Page;
import com.codeaffine.home.control.admin.ui.model.ActionMap;
import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;

public class AdminUiViewTest {

  private static final String PAGE_1 = "page 1";
  private static final String PAGE_2 = "page 2";

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private AdminUiPreference preference;
  private ActionMap actionMap;
  private AdminUiView view;
  private Shell parent;

  @Before
  public void setUp() {
    parent = displayHelper.createShell();
    actionMap = new ActionMap();
    preference = mock( AdminUiPreference.class );
    view = new AdminUiView( actionMap, preference );
    parent.open();
  }

  @Test
  public void createContent() {
    TestPage page1 = new TestPage( PAGE_1 );
    TestPage page2 = new TestPage( PAGE_2 );
    List<Page> pages = asList( page1, page2 );
    pages.forEach( page -> actionMap.putAction( page, () -> activatePage( page, view ) ) );

    view.createContent( parent, pages );

    assertThat( page1.getControl().isVisible() ).isTrue();
    assertThat( page2.getControl().isVisible() ).isFalse();
  }

  @Test
  public void createContentWithReversePageOrderPreference() {
    TestPage page1 = spy( new TestPage( PAGE_1 ) );
    TestPage page2 = spy( new TestPage( PAGE_2 ) );
    List<Page> pages = asList( page1, page2 );
    pages.forEach( page -> actionMap.putAction( page, () -> activatePage( page, view ) ) );
    when( preference.getPageOrder() ).thenReturn( asPageOrderValues( PAGE_2, PAGE_1 ) );

    view.createContent( parent, pages );

    InOrder order = inOrder( page1, page2 );
    order.verify( page2 ).createContent( any() );
    order.verify( page1 ).createContent( any() );
    assertThat( page1.getControl().isVisible() ).isFalse();
    assertThat( page2.getControl().isVisible() ).isTrue();
  }

  @Test
  public void dispose() {
    TestPage page1 = new TestPage( PAGE_1 );
    List<Page> pages = asList( page1 );
    pages.forEach( page -> actionMap.putAction( page, () -> activatePage( page, view ) ) );
    view.createContent( parent, pages );

    view.dispose();

    assertThat( page1.getControl().isDisposed() ).isTrue();
  }

  @Test
  public void show() {
    TestPage page1 = new TestPage( PAGE_1 );
    TestPage page2 = new TestPage( PAGE_2 );
    List<Page> pages = asList( page1, page2 );
    pages.forEach( page -> actionMap.putAction( page, () -> activatePage( page, view ) ) );
    view.createContent( parent, pages );

    view.showPage( page2 );

    assertThat( page1.getControl().isVisible() ).isFalse();
    assertThat( page2.getControl().isVisible() ).isTrue();
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActionsArgument() {
    new AdminUiView( null, preference );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsPreferenceArgument() {
    new AdminUiView( actionMap, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void showPageWithNullAsPageIdArgument() {
    view.showPage( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createContentWithNullAsParentArgument() {
    view.createContent( null, emptyList() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createContentWithNullAsPagesArgument() {
    view.createContent( parent, null );
  }

  private static List<PageOrderValue> asPageOrderValues( String ... pageLabels ) {
    return asList( pageLabels ).stream().map( page -> PageOrderValue.valueOf( page ) ).collect( toList() );
  }
}