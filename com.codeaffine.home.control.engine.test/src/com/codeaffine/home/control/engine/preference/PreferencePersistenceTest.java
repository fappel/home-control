package com.codeaffine.home.control.engine.preference;

import static com.codeaffine.home.control.engine.preference.PreferencePersistence.ENV_CONFIGURATION_DIRECTORY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.codeaffine.home.control.event.EventBus;
import com.codeaffine.home.control.preference.DefaultValue;
import com.codeaffine.home.control.preference.Preference;

public class PreferencePersistenceTest {

  private static final int VALUE = 23;
  @Rule
  public final TemporaryFolder tempFolder = new TemporaryFolder();

  @Preference
  interface PersistenceTestPreference {
    @DefaultValue( "12" )
    int getValue();
    void setValue( int value );
  }

  @Before
  public void setUp() throws IOException {
    System.getProperties().put( ENV_CONFIGURATION_DIRECTORY, tempFolder.getRoot().getCanonicalPath() );
  }

  @After
  public void tearDown() {
    System.getProperties().remove( ENV_CONFIGURATION_DIRECTORY );
  }

  @Test
  public void onPersistAndRestore() {
    PreferenceModelImpl model1 = newModel();
    PreferencePersistence persistence1 = new PreferencePersistence( model1 );
    PersistenceTestPreference preference = model1.get( PersistenceTestPreference.class );
    preference.setValue( VALUE );

    persistence1.onPersist( null );
    PreferenceModelImpl model2 = newModel();
    new PreferencePersistence( model2 );
    int actual = model2.get( PersistenceTestPreference.class ).getValue();

    assertThat( actual ).isEqualTo( VALUE );
  }

  @Test( expected = IllegalArgumentException.class )
  public void constructWithNullAsModelArgument() {
    new PreferencePersistence( null );
  }

  private static PreferenceModelImpl newModel() {
    return new PreferenceModelImpl( mock( EventBus.class ) );
  }
}