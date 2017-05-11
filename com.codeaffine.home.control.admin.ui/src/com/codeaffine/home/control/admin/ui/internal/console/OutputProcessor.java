package com.codeaffine.home.control.admin.ui.internal.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class OutputProcessor implements Runnable {

  private final LineWriter lineWriter;
  private final File responseFile;

  private volatile boolean shutdown;

  public OutputProcessor( File responseFile, LineWriter lineWriter ) {
    this.responseFile = responseFile;
    this.lineWriter = lineWriter;
  }

  @Override
  public void run() {
    try( BufferedReader reader = new BufferedReader( new FileReader( responseFile ) ) ) {
      while( !shutdown ) {
        processOutput( reader );
      }
    } catch( IOException shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  private void processOutput( BufferedReader reader ) {
    try {
      while( !reader.ready() ) {
        Thread.sleep( 100 );
      }
      lineWriter.writeLine( reader.readLine() );
    } catch( InterruptedException ie ) {
      // shutdown started
    } catch( Exception shouldNotHappen ) {
      throw new IllegalStateException( shouldNotHappen );
    }
  }

  public synchronized void shutdown() {
    shutdown = true;
  }
}