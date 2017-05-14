package com.codeaffine.home.control.admin.ui.view;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.codeaffine.home.control.admin.ui.api.Page;
import com.codeaffine.home.control.admin.ui.model.ActionMap;
import com.codeaffine.home.control.admin.ui.model.PageFactoryList;
import com.codeaffine.home.control.admin.ui.test.util.DisplayHelper;

public class ViewContentLifeCycleTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private ViewContentLifeCycle lifeCycle;
  private PageFactoryList pageFactories;
  private ActionMap actionMap;
  private AdminUiView view;
  private Composite parent;

  @Before
  public void setUp() {
    view = mock( AdminUiView.class );
    pageFactories = new PageFactoryList();
    actionMap = new ActionMap();
    lifeCycle = new ViewContentLifeCycle( view, pageFactories, actionMap );
    parent = displayHelper.createShell();
  }

  @Test
  public void createViewContent() {
    Page page = mock( Page.class );
    pageFactories.addPageFactory( () -> page );

    lifeCycle.createViewContent( parent );

    verify( view ).createContent( any(), any() );
    assertThat( actionMap.getAction( page ) ).isNotNull();
    assertThat( captureViewParent() ).isNotSameAs( parent );
    assertThat( capturePages() ).containsExactly( page );
  }

  @Test
  public void disposeViewContent() {
    Page page = mock( Page.class );
    pageFactories.addPageFactory( () -> page );
    lifeCycle.createViewContent( parent );

    lifeCycle.disposeViewContent();

    verify( view ).dispose();
    assertThat( captureViewParent().isDisposed() ).isTrue();
    assertThat( capturePages() ).isEmpty();
    assertThat( actionMap.isEmpty() ).isTrue();
  }

  @Test
  public void runViewAction() {
    Page page = mock( Page.class );
    pageFactories.addPageFactory( () -> page );

    lifeCycle.createViewContent( parent );
    actionMap.getAction( page ).run();

    verify( view ).showPage( page );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsViewArgument() {
    new ViewContentLifeCycle( null, pageFactories, actionMap );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsPagesFactoriesArgument() {
    new ViewContentLifeCycle( view, null, actionMap );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsActionMapArgument() {
    new ViewContentLifeCycle( view, pageFactories, null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void createViewContentWithNullAsParentArgument() {
    lifeCycle.createViewContent( null );
  }

  private Composite captureViewParent() {
    ArgumentCaptor<Composite> captor = forClass( Composite.class );
    verify( view ).createContent( captor.capture(), any() );
    return captor.getValue();
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private List<Page> capturePages() {
    ArgumentCaptor<List> captor = forClass( List.class );
    verify( view ).createContent( any(), captor.capture() );
    return captor.getValue();
  }
}