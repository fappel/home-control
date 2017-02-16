package com.codeaffine.home.control.application.internal.motion;

import static com.codeaffine.home.control.type.OpenClosedType.CLOSED;

import java.util.Optional;

import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensor;
import com.codeaffine.home.control.application.MotionSensorProvider.MotionSensorDefinition;
import com.codeaffine.home.control.entity.AllocationProvider.AllocationControl;
import com.codeaffine.home.control.entity.AllocationProvider.AllocationControlFactory;
import com.codeaffine.home.control.entity.EntityProvider.Entity;
import com.codeaffine.home.control.event.ChangeEvent;
import com.codeaffine.home.control.item.ContactItem;
import com.codeaffine.home.control.type.OpenClosedType;

public class MotionSensorImpl implements MotionSensor {

  private final AllocationControl allocationControl;
  private final MotionSensorDefinition definition;
  private final ContactItem item;

  public MotionSensorImpl(
    MotionSensorDefinition definition, ContactItem item, AllocationControlFactory allocationControlFactory )
  {
    this.allocationControl = allocationControlFactory.create( this );
    this.definition = definition;
    this.item = item;
    initialize();
  }

  @Override
  public MotionSensorDefinition getDefinition() {
    return definition;
  }

  @Override
  public void registerAllocatable( Entity<?> allocatable ) {
    allocationControl.registerAllocatable( allocatable );
  }

  @Override
  public void unregisterAllocatable( Entity<?> allocatable ) {
    allocationControl.unregisterAllocatable( allocatable );
  }

  private void initialize() {
    item.addChangeListener( evt -> handleEntityAllocation( evt ) );
  }

  private void handleEntityAllocation( ChangeEvent<ContactItem, OpenClosedType> evt ) {
    if( evt.getNewStatus().equals( Optional.of( CLOSED ) ) ) {
      allocationControl.deallocate();
    } else {
      allocationControl.allocate();
    }
  }
}