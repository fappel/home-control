package com.codeaffine.home.control.admin.ui.control;

import static com.codeaffine.home.control.admin.ui.Theme.*;
import static com.codeaffine.home.control.admin.ui.control.Messages.ERROR_BANNER_COMPONENT_EXISTS;
import static com.codeaffine.home.control.admin.ui.control.StateVerification.verifyState;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static org.eclipse.rap.rwt.RWT.CUSTOM_VARIANT;
import static org.eclipse.swt.SWT.NONE;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

import com.codeaffine.home.control.admin.ui.api.ActionSupplier;

public class Banner {

  private final Composite control;

  private NavigationBar navigationBar;
  private Label separator;
  private Label title;
  private Label logo;

  public static Banner newBanner( Composite parent, Layout layout ) {
    verifyNotNull( parent, "parent" );
    verifyNotNull( layout, "layout" );

    return new Banner( parent, layout );
  }

  Banner( Composite parent, Layout layout ) {
    control = new Composite( parent, NONE );
    control.setLayout( layout );
  }

  public Control getControl() {
    return control;
  }

  public Banner withLogo( String text ) {
    verifyNotNull( text, "text" );
    verifyState( logo == null, ERROR_BANNER_COMPONENT_EXISTS, "logo" );

    logo = new Label( control, NONE );
    logo.setText( text );
    logo.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_BANNER_LOGO );
    return this;
  }

  public Control getLogo()  {
    return logo;
  }

  public Banner withTitle( String text ) {
    verifyNotNull( text, "text" );
    verifyState( title == null, ERROR_BANNER_COMPONENT_EXISTS, "title" );

    title = new Label( control, NONE );
    title.setText( text );
    title.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_BANNER_APPLICATION_NAME );
    return this;
  }

  public Control getTitle() {
    return title;
  }

  public Banner withSeparator() {
    verifyState( separator == null, ERROR_BANNER_COMPONENT_EXISTS, "separator" );

    separator = new Label( control, NONE );
    separator.setData( CUSTOM_VARIANT, CUSTOM_VARIANT_BANNER_SEPARATOR );
    return this;
  }

  public Control getSeparator() {
    return separator;
  }

  public Banner withNavigationBar( ActionSupplier actionSupplier ) {
    verifyNotNull( actionSupplier, "actionSupplier" );
    verifyState( navigationBar == null, ERROR_BANNER_COMPONENT_EXISTS, "navigation bar" );

    navigationBar = new NavigationBar( control, actionSupplier );
    return this;
  }

  public NavigationBar getNavigationBar() {
    return navigationBar;
  }
}