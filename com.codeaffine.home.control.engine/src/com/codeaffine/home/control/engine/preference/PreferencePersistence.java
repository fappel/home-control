package com.codeaffine.home.control.engine.preference;

import static com.codeaffine.home.control.engine.preference.Messages.ERROR_CONFIGURATION_DIR_NOT_SET;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;
import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.codeaffine.home.control.event.Subscribe;
import com.codeaffine.home.control.preference.PreferenceEvent;
import com.codeaffine.home.control.preference.PreferenceModel;

public class PreferencePersistence {

  public static final String ENV_CONFIGURATION_DIRECTORY = "OPENHAB_CONF";

  private final PreferenceModelImpl model;

  public PreferencePersistence( PreferenceModel model ) {
    verifyNotNull( model, "model" );

    this.model = ( PreferenceModelImpl )model;
    loadModel();
  }

  @Subscribe
  void persist( @SuppressWarnings("unused") PreferenceEvent event ) {
    saveModel();
  }

  private void loadModel() {
    try ( InputStream in = new FileInputStream( getConfigurationFile() ) ){
      model.load( in );
    } catch( IOException ioe ) {
      throw new IllegalStateException( ioe );
    }
  }

  private void saveModel() {
    try ( OutputStream out = new FileOutputStream( getConfigurationFile() ) ){
      model.save( out );
    } catch( IOException ioe ) {
      throw new IllegalStateException( ioe );
    }
  }

  private static File getConfigurationFile() {
    File result = new File( getConfigurationDirectory(), "home-control.cfg" );
    try {
      result.createNewFile();
    } catch( IOException ioe ) {
      throw new IllegalStateException( ioe );
    }
    return result;
  }

  private static String getConfigurationDirectory() {
    String result = System.getenv( ENV_CONFIGURATION_DIRECTORY );
    if( result == null ) {
      result = System.getProperty( ENV_CONFIGURATION_DIRECTORY );
      if( result == null ) {
        throw new IllegalStateException( format(  ERROR_CONFIGURATION_DIR_NOT_SET, ENV_CONFIGURATION_DIRECTORY ) );
      }
    }
    return result;
  }
}