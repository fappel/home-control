package com.codeaffine.home.control.admin.ui.console;

import java.io.File;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.service.ServerPushSession;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import com.codeaffine.home.control.admin.ui.console.SessionFactory.Session;

public class OSGiConsole {

  private final SessionFactory sessionFactory;
  private final ConsolePreference preference;

  private ConsoleStreamSupplier streamSupplier;
  private ServerPushSession serverPushSession;
  private OutputProcessor outputProcessor;
  private ExecutorService executorService;
  private Session session;
  private Text consoleWidget;

  public OSGiConsole( ConsolePreference preference ) {
    this.preference = preference;
    sessionFactory = new SessionFactory();
  }

  public void create( Composite parent ) {
    executorService = Executors.newCachedThreadPool();
    serverPushSession = new ServerPushSession();
    serverPushSession.start();
    consoleWidget = new Text( parent, SWT.MULTI );
    consoleWidget.setData( RWT.CUSTOM_VARIANT, "osgiConsole" );
    streamSupplier = new ConsoleStreamSupplier();
    registerOutputProcessor( consoleWidget, streamSupplier.getResponseFile() );
    session = registerConsoleSession( streamSupplier );
    registerCommandParser( consoleWidget, session, streamSupplier.getCommandFile() );
  }

  public Control getControl() {
    return consoleWidget;
  }

  private static void registerCommandParser( Text consoleWidget, Session session, File commandFile ) {
    CommandLineProcessor commandLineProcessor = new CommandLineProcessor( commandFile, session );
    consoleWidget.addKeyListener( new CommandParser( commandLineProcessor, consoleWidget ) );
  }

  private void registerOutputProcessor( Text consoleWidget, File responseFile ) {
    outputProcessor = new OutputProcessor( responseFile, new LineWriter( consoleWidget ) );
    executorService.execute( outputProcessor );
  }

  private Session registerConsoleSession( ConsoleStreamSupplier streamSupplier ) {
    PrintStream out = new PrintStream( streamSupplier.getResponseStream() );
    session = sessionFactory.create( streamSupplier.getCommandStream(), out, out );
    executorService.execute( () -> {
      try {
        session.put( "SCOPE", "equinox:*" );
        session.put( "prompt", preference.getPrompt() + " " );
        session.execute( "gosh --login --noshutdown" );
      } catch( Exception e ) {
        throw new IllegalStateException( e );
      }
    } );
    return session;
  }

  public void dispose() {
    serverPushSession.stop();
    outputProcessor.shutdown();
    executorService.shutdownNow();
    session.close();
    streamSupplier.close();
  }
}