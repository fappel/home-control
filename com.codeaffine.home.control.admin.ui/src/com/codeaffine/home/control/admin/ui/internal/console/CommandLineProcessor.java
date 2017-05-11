package com.codeaffine.home.control.admin.ui.internal.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import com.codeaffine.home.control.admin.ui.internal.console.SessionFactory.Session;


class CommandLineProcessor {

  private final PrintWriter printWriter;
  private final StringBuilder command;
  private final Session session;

  CommandLineProcessor( File commandFile, Session session ) {
    this.session = session;
    this.command = new StringBuilder();
    try {
      this.printWriter = new PrintWriter( commandFile );
    } catch( FileNotFoundException e ) {
      throw new IllegalStateException( e );
    }
  }

  void processCommand() {
    String commandValue = command.toString();
    printWriter.println( commandValue );
    printWriter.flush();
    command.setLength( 0 );
    try {
      session.execute( commandValue );
    } catch( Exception e ) {
      throw new IllegalStateException( e );
    }
  }

  void removeLastCommandCharacter() {
    if( command.length() > 0 ) {
      command.deleteCharAt( command.length() - 1 );
    }
  }

  public void appendCommandCharacter( String character ) {
    command.append( character );
  }
}