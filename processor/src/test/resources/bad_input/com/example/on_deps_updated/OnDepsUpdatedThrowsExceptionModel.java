package com.example.on_deps_updated;

import org.realityforge.arez.annotations.ArezComponent;
import org.realityforge.arez.annotations.OnDepsUpdated;
import org.realityforge.arez.annotations.Tracked;

@ArezComponent
public class OnDepsUpdatedThrowsExceptionModel
{
  @Tracked
  public void render()
  {
  }

  @OnDepsUpdated
  void onRenderDepsUpdated()
    throws Exception
  {
  }
}