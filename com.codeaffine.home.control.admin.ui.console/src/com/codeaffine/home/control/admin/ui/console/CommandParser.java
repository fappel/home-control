package com.codeaffine.home.control.admin.ui.console;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Text;


class CommandParser implements KeyListener {

  private static final long serialVersionUID = 1L;

  private final CommandLineProcessor commandLineProcessor;
  private final Text consoleWidget;

  CommandParser( CommandLineProcessor commandLineProcessor, Text consoleWidget ) {
    this.commandLineProcessor = commandLineProcessor;
    this.consoleWidget = consoleWidget;
  }

  @Override
  public void keyReleased( KeyEvent event ) {
    if( enterPressed( event ) ) {
      commandLineProcessor.processCommand();
    }
    consoleWidget.getParent().layout( true, true );
  }

  @Override
  public void keyPressed( KeyEvent event ) {
    String value = consoleWidget.getText();
    String newValue = value;
    if( backSpacePressed( event ) ) {
      newValue = removeLastCommandCharacter( value );
    } else if( anyKeyPressed( event ) ) {
      newValue = appendLastCommandCharacter( event, value );
    }
    if( !enterPressed( event ) ) {
      event.doit = false;
    }
    consoleWidget.setText( newValue );
    consoleWidget.setSelection( consoleWidget.getText().length() );
    consoleWidget.getParent().layout( true, true );
  }

  private String appendLastCommandCharacter( KeyEvent event, String value ) {
    String filtered = filterWhiteSpacesExceptSpace( event );
    commandLineProcessor.appendCommandCharacter( filtered );
    return value + filtered;
  }

  private String removeLastCommandCharacter( String value ) {
    commandLineProcessor.removeLastCommandCharacter();
    return value.substring( 0, value.length() - 1 );
  }

  private static String filterWhiteSpacesExceptSpace( KeyEvent event ) {
    String result = new StringBuilder().append( event.character ).toString();
    if( event.character != ' ' ) {
      result = result.trim();
    }
    return result;
  }

  private static boolean anyKeyPressed( KeyEvent event ) {
    return event.character != 13;
  }

  private static boolean backSpacePressed( KeyEvent event ) {
    return event.character == 8;
  }

  private static boolean enterPressed( KeyEvent event ) {
    return event.character == 13;
  }
}