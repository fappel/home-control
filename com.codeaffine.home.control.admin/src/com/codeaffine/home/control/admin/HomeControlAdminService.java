package com.codeaffine.home.control.admin;

import static com.codeaffine.home.control.admin.Messages.ERROR_NOT_INITIALIZED;
import static com.codeaffine.util.ArgumentVerification.verifyNotNull;

import com.codeaffine.home.control.ComponentAccessService;
import com.codeaffine.home.control.ComponentAccessService.ComponentSupplier;
import com.codeaffine.home.control.preference.PreferenceModel;

public class HomeControlAdminService {

  private ComponentAccessService componentAccessService;
  private final PreferenceProxyFactory preferenceProxyFactory;

  public HomeControlAdminService() {
    preferenceProxyFactory = new PreferenceProxyFactory();
  }

  public PreferenceIntrospection getPreferenceIntrospection() {
    checkInitialization();
    return componentAccessService.submit( supplier -> newIntrospection( supplier ) );
  }

  public <T> T getPreference( Class<T> preferenceType ) {
    checkInitialization();
    return componentAccessService.submit( supplier -> getPreference( supplier, preferenceType ) );
  }

  public void bind( ComponentAccessService componentAccessService ) {
    verifyNotNull( componentAccessService, "componentAccessService" );

    this.componentAccessService = componentAccessService;
  }

  public void unbind( ComponentAccessService componentAccessService ) {
    verifyNotNull( componentAccessService, "componentAccessService" );

    this.componentAccessService = null;
  }

  private PreferenceIntrospection newIntrospection( ComponentSupplier supplier ) {
    return new PreferenceIntrospection( supplier.get( PreferenceModel.class ), componentAccessService );
  }

  private void checkInitialization() throws IllegalStateException {
    if( componentAccessService == null ) {
      throw new IllegalStateException( ERROR_NOT_INITIALIZED );
    }
  }

  private <T> T getPreference( ComponentSupplier supplier, Class<T> preferenceType ) {
    T delegate = supplier.get( PreferenceModel.class ).get( preferenceType );
    return preferenceProxyFactory.create( delegate, preferenceType, componentAccessService );
  }
}
