package com.example.dependency;

import arez.annotations.ArezComponent;
import arez.annotations.Dependency;
import arez.annotations.Observable;
import arez.component.DisposeTrackable;
import javax.annotation.Nonnull;

@ArezComponent
public abstract class SetNullOnNonnullDependency
{
  @Nonnull
  @Observable
  @Dependency( action = Dependency.Action.SET_NULL )
  abstract DisposeTrackable getValue();

  abstract void setValue( @Nonnull DisposeTrackable value );
}
