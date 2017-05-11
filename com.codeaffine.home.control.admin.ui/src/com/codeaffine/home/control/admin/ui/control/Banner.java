package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.home.control.admin.ui.Theme.*;
import static org.eclipse.rap.rwt.RWT.CUSTOM_VARIANT;
import static org.eclipse.swt.SWT.NONE;

import java.util.function.Consumer;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public class Banner {

  private final Composite control;

  private Label separator;
  private Label logo;
  private Label title;

  private NavigationBar navigationBar;

  public static Banner newBanner( Composite parent, Layout layout ) {
    return new Banner( parent, layout );
  }

  Banner( Composite parent, Layout layout ) {
    control = new Composite( parent, NONE );
    control.setLayout( layout );
  }

  public Banner layout( Consumer<Banner> layoutHandler ) {
    layoutHandler.accept( this );
    return this;
  }

  public Control getControl() {
    return control;
  }

  public Banner withLogo( String text ) {
    logo = new Label( control, NONE );
    logo.setText( text );
    logo.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_BANNER_LOGO );
    return this;
  }

  public Control getLogo()  {
    return logo;
  }

  public Banner withTitle( String text ) {
    title = new Label( control, NONE );
    title.setText( text );
    title.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_BANNER_APPLICATION_NAME );
    return this;
  }

  public Control getTitle() {
    return title;
  }

  public Banner withSeparator() {
    separator = new Label( control, NONE );
    separator.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_BANNER_SEPARATOR );
    return this;
  }

  public Control getSeparator() {
    return separator;
  }

  public Banner withNavigationBar( ActionMap actions ) {
    navigationBar = new NavigationBar( control, actions );
    return this;
  }

  public NavigationBar getNavigationBar() {
    return navigationBar;
  }
}