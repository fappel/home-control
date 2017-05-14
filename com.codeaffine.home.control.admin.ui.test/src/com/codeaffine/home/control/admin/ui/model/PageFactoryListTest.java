package com.codeaffine.home.control.admin.ui.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.api.PageFactory;


public class PageFactoryListTest {

  private PageFactoryList list;

  @Before
  public void setUp() {
    list = new PageFactoryList();
  }

  @Test
  public void initialState() {
    assertThat( list.getPageFactories() ).isEmpty();
  }

  @Test
  public void addPageFactory() {
    PageFactory expected = mock( PageFactory.class );

    list.addPageFactory( expected );
    List<PageFactory> actuals = list.getPageFactories();

    assertThat( actuals ).containsExactly( expected );
  }

  @Test
  public void removePageFactory() {
    PageFactory factory = mock( PageFactory.class );
    list.addPageFactory( factory );

    list.removePageFactory( factory );
    List<PageFactory> actuals = list.getPageFactories();

    assertThat( actuals ).isEmpty();
  }

  @Test
  public void getPageFactories() {
    PageFactory expected = mock( PageFactory.class );

    list.addPageFactory( expected );
    List<PageFactory> actuals = list.getPageFactories();

    assertThat( actuals ).containsExactly( expected );
  }

  @Test
  public void getPageFactoriesAfterChangingPreviousLyReturnedList() {
    PageFactory expected = mock( PageFactory.class );
    list.addPageFactory( expected );

    list.getPageFactories().add( mock( PageFactory.class ) );
    List<PageFactory> actuals = list.getPageFactories();

    assertThat( actuals ).containsExactly( expected );
  }

  @Test
  public void addAndRemoveOfPageFactoryWithRegisteredUpdateHook() {
    Runnable hook = mock( Runnable.class );
    list.registerUpdateHook( hook );
    PageFactory factory = mock( PageFactory.class );

    list.addPageFactory( factory );
    list.removePageFactory( factory );

    verify( hook, times( 2 ) ).run();
  }

  @Test
  public void addPageFactoryThatAlreadyExists() {
    Runnable hook = mock( Runnable.class );
    list.registerUpdateHook( hook );
    PageFactory factory = mock( PageFactory.class );

    list.addPageFactory( factory );
    list.addPageFactory( factory );

    verify( hook ).run();
  }

  @Test
  public void addAndRemoveOfPageFactoryAfterDeregisteringUpdateHook() {
    Runnable hook = mock( Runnable.class );
    list.registerUpdateHook( hook );
    PageFactory factory = mock( PageFactory.class );

    list.deregisterUpdateHook( hook );
    list.addPageFactory( factory );
    list.removePageFactory( factory );

    verify( hook, never() ).run();
  }

  @Test
  public void removePageFactoryWithUnknownFactory() {
    list.addPageFactory( mock( PageFactory.class ) );
    Runnable hook = mock( Runnable.class );
    list.registerUpdateHook( hook );

    list.removePageFactory( mock( PageFactory.class ) );

    verify( hook, never() ).run();
  }

  @Test( expected = IllegalArgumentException.class )
  public void addPageFactoryWithNullAsPageFactoryArgument() {
    list.addPageFactory( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void removePageFactoryWithNullAsPageFactoryArgument() {
    list.removePageFactory( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void registerUpdateHookWithNullAsUpdateHookArgument() {
    list.registerUpdateHook( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void deregisterUpdateHookWithNullAsUpdateHookArgument() {
    list.deregisterUpdateHook( null );
  }
}
