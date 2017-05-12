package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.home.control.admin.ui.internal.util.FormDatas.attach;
import static com.codeaffine.home.control.admin.ui.test.ShellHelper.createShell;
import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.home.control.admin.ui.model.ActionMap;
import com.codeaffine.home.control.admin.ui.test.DisplayHelper;

public class BannerTest {

  @Rule
  public final DisplayHelper displayHelper = new DisplayHelper();

  private Banner banner;
  private Shell parent;

  @Before
  public void setUp() {
    parent = createShell( displayHelper );
    banner = Banner.newBanner( parent, new FormLayout() )
      .withLogo( "logo" )
      .withTitle( "title" )
      .withNavigationBar( new ActionMap() )
      .withSeparator();
  }

  @Test
  public void initialization() {
    assertThat( banner.getControl() ).isSameAs( parent.getChildren()[ 0 ] );
    assertThat( banner.getLogo() ).isNotNull();
    assertThat( banner.getTitle() ).isNotNull();
    assertThat( banner.getSeparator() ).isNotNull();
    assertThat( banner.getNavigationBar() ).isNotNull();
  }

  @Test
  public void layout() {
    attach( banner.getLogo() ).toLeft();
    attach( banner.getTitle() ).atLeftTo( banner.getLogo() );
    attach( banner.getSeparator() ).atLeftTo( banner.getTitle() ).withWidth( 2 );
    attach( banner.getNavigationBar().getControl() ).atLeftTo( banner.getSeparator() );
    parent.setSize( 200, 100 );

    assertThat( banner.getTitle().getLocation().x )
      .isGreaterThan( banner.getLogo().getLocation().x );
    assertThat( banner.getSeparator().getLocation().x )
      .isGreaterThan( banner.getTitle().getLocation().x );
    assertThat( banner.getNavigationBar().getControl().getLocation().x )
      .isGreaterThan( banner.getSeparator().getLocation().x );
  }

  @Test( expected = IllegalStateException.class )
  public void withLogoIfLogoAlreadyExists() {
    banner.withLogo( "logo" );
  }

  @Test( expected = IllegalStateException.class )
  public void withTitleIfTitleAlreadyExists() {
    banner.withTitle( "Title" );
  }

  @Test( expected = IllegalStateException.class )
  public void withSeperatorIfSeparatorAlreadyExists() {
    banner.withSeparator();
  }

  @Test( expected = IllegalStateException.class )
  public void withNavigationBarIfNavigationBarAlreadyExists() {
    banner.withNavigationBar( new ActionMap() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void withLogoWithNullAsLogoArgument() {
    banner.withLogo( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void withTitleWithNullAsTitleArgument() {
    banner.withTitle( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void withNavigationWithNullAsActionSupplierArgument() {
    banner.withNavigationBar( null );
  }

  @Test( expected = IllegalArgumentException.class )
  public void newBannerWithNullAsParentArgument() {
    Banner.newBanner( null, new FillLayout() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void newBannerWithNullAsLayoutArgument() {
    Banner.newBanner( parent, null );
  }
}