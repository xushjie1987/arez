package com.example.context_ref;

import org.realityforge.arez.ArezContext;
import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.arez.annotations.ContextRef;

@ArezComponent( allowEmpty = true )
class PrivateModel
{
  @ContextRef
  private ArezContext getContext()
  {
    throw new IllegalStateException();
  }
}