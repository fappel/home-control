package com.codeaffine.home.control.admin.ui.internal.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ConsoleStreamSupplier {

  private final File responseFile;
  private final File commandFile;

  private OutputStream responseStream;
  private InputStream commandStream;

  ConsoleStreamSupplier() {
    commandFile = createTempFile( "commands" );
    responseFile = createTempFile( "response" );
  }

  void close() {
    if( responseStream != null ) {
      try {
        responseStream.close();
      } catch( IOException shouldNotHappen ) {
        throw new IllegalStateException( shouldNotHappen );
      }
    }
    if( commandStream != null ) {
      try {
        commandStream.close();
      } catch( IOException shouldNotHappen ) {
        throw new IllegalStateException( shouldNotHappen );
      }
    }
    commandFile.delete();
    responseFile.delete();
  }

  public InputStream getCommandStream() {
    try {
      if( commandStream != null ) {
        throw new IllegalStateException( "Command stream already in use." );
      }
      commandStream = new FileInputStream( commandFile );
      return commandStream;
    } catch( FileNotFoundException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  File getResponseFile() {
    return responseFile;
  }

  File getCommandFile() {
    return commandFile;
  }

  public OutputStream getResponseStream() {
    try {
      if( responseStream != null ) {
        throw new IllegalStateException( "Response stream already in use." );
      }
      responseStream = new FileOutputStream( responseFile );
      return responseStream;
    } catch( FileNotFoundException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  private static File createTempFile( String prefix ) {
    try {
      File result = File.createTempFile( prefix, "tmp" );
      result.deleteOnExit();
      return result;
    } catch( IOException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }
}