package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.engine.entity.Sets.asSet;
import static org.mockito.Mockito.*;

import com.codeaffine.home.control.SystemExecutor;
import com.codeaffine.home.control.preference.PreferenceModel;
import com.codeaffine.home.control.test.util.context.TestContext;

class PreferenceHelper {

  static PreferenceModel stubPreferenceModel( TestPreference preference ) {
    PreferenceModel result = mock( PreferenceModel.class );
    when( result.getAllPreferenceTypes() ).thenReturn( asSet( TestPreference.class ) );
    when( result.get( TestPreference.class ) ).thenReturn( preference );
    return result;
  }

  static TestContext createContext( PreferenceModel model, SystemExecutor executor ) {
    TestContext result = new TestContext();
    result.set( PreferenceModel.class, model );
    result.set( SystemExecutor.class, executor );
    return result;
  }
}