package com.codeaffine.home.control.status;

import com.codeaffine.home.control.Context;
import com.codeaffine.home.control.SystemConfiguration;
import com.codeaffine.home.control.entity.EntityProvider.EntityRegistry;
import com.codeaffine.home.control.entity.EntityRelationProvider.Facility;
import com.codeaffine.home.control.status.internal.activation.ActivationSupplierImpl;
import com.codeaffine.home.control.status.internal.activity.ActivitySupplierImpl;
import com.codeaffine.home.control.status.internal.computer.ComputerStatusSupplierImpl;
import com.codeaffine.home.control.status.internal.light.LightStatusSupplierImpl;
import com.codeaffine.home.control.status.internal.report.ActivityReport;
import com.codeaffine.home.control.status.internal.report.ActivityReportCompiler;
import com.codeaffine.home.control.status.internal.scene.NamedSceneSupplierImpl;
import com.codeaffine.home.control.status.internal.sun.SunPositionSupplierImpl;
import com.codeaffine.home.control.status.model.ActivationSensorProvider;
import com.codeaffine.home.control.status.model.LightSensorProvider;
import com.codeaffine.home.control.status.model.SectionProvider;
import com.codeaffine.home.control.status.supplier.ActivationSupplier;
import com.codeaffine.home.control.status.supplier.ActivitySupplier;
import com.codeaffine.home.control.status.supplier.ComputerStatusSupplier;
import com.codeaffine.home.control.status.supplier.HeartBeatSupplier;
import com.codeaffine.home.control.status.supplier.LightStatusSupplier;
import com.codeaffine.home.control.status.supplier.NamedSceneSupplier;
import com.codeaffine.home.control.status.supplier.SunPositionSupplier;
import com.codeaffine.home.control.status.util.Analysis;
import com.codeaffine.home.control.status.util.MotionStatusCalculator;

public class StatusConfiguration implements SystemConfiguration {

  @Override
  public void configureEntities( EntityRegistry entityRegistry ) {
    entityRegistry.register( ActivationSensorProvider.class );
    entityRegistry.register( LightSensorProvider.class );
    entityRegistry.register( SectionProvider.class );
  }

  @Override
  public void configureFacility( Facility facility ) {}

  @Override
  public void configureStatusSupplier( StatusSupplierRegistry statusSupplierRegistry ) {
    Context context = statusSupplierRegistry.getContext();
    statusSupplierRegistry.register( ActivationSupplier.class, ActivationSupplierImpl.class );
    statusSupplierRegistry.register( ActivitySupplier.class, ActivitySupplierImpl.class );
    statusSupplierRegistry.register( SunPositionSupplier.class, SunPositionSupplierImpl.class );
    statusSupplierRegistry.register( NamedSceneSupplier.class, NamedSceneSupplierImpl.class );
    statusSupplierRegistry.register( ComputerStatusSupplier.class, ComputerStatusSupplierImpl.class );
    statusSupplierRegistry.register( HeartBeatSupplier.class, HeartBeatSupplier.class );
    statusSupplierRegistry.register( LightStatusSupplier.class, LightStatusSupplierImpl.class );
    context.set( MotionStatusCalculator.class, context.create( MotionStatusCalculator.class ) );
    context.set( Analysis.class, context.create( Analysis.class ) );
    context.set( ActivityReportCompiler.class, context.create( ActivityReportCompiler.class ) );
    context.set( ActivityReport.class, context.create( ActivityReport.class ) );
  }

  @Override
  public void configureSceneSelection( SceneSelector sceneSelector ) {}

  @Override
  public void configureHomeControlOperations( ControlCenter controlCenter ) {}
}