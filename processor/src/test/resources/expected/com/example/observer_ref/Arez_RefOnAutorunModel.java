package com.example.observer_ref;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import org.realityforge.arez.Arez;
import org.realityforge.arez.ArezContext;
import org.realityforge.arez.Disposable;
import org.realityforge.arez.Observer;
import org.realityforge.braincheck.Guards;

@Generated("org.realityforge.arez.processor.ArezProcessor")
public final class Arez_RefOnAutorunModel extends RefOnAutorunModel implements Disposable {
  private static volatile long $$arez$$_nextId;

  private final long $$arez$$_id;

  private boolean $$arez$$_disposed;

  @Nonnull
  private final ArezContext $$arez$$_context;

  @Nonnull
  private final Observer $$arez$$_doStuff;

  public Arez_RefOnAutorunModel() {
    super();
    this.$$arez$$_context = Arez.context();
    this.$$arez$$_id = $$arez$$_nextId++;
    this.$$arez$$_doStuff = this.$$arez$$_context.autorun( Arez.areNamesEnabled() ? $$arez$$_name() + ".doStuff" : null, true, () -> super.doStuff(), false );
    this.$$arez$$_context.triggerScheduler();
  }

  final long $$arez$$_id() {
    return $$arez$$_id;
  }

  String $$arez$$_name() {
    return "RefOnAutorunModel." + $$arez$$_id();
  }

  @Override
  public boolean isDisposed() {
    return $$arez$$_disposed;
  }

  @Override
  public void dispose() {
    if ( !isDisposed() ) {
      $$arez$$_disposed = true;
      this.$$arez$$_context.safeAction( Arez.areNamesEnabled() ? $$arez$$_name() + ".dispose" : null, () -> { {
        $$arez$$_doStuff.dispose();
      } } );
    }
  }

  @Override
  public void doStuff() {
    Guards.invariant( () -> !$$arez$$_disposed, () -> "Method invoked on invalid component '" + $$arez$$_name() + "'" );
    this.$$arez$$_context.safeAction( Arez.areNamesEnabled() ? $$arez$$_name() + ".doStuff" : null, true, () -> super.doStuff() );
  }

  @Override
  Observer getDoStuffObserver() {
    Guards.invariant( () -> !$$arez$$_disposed, () -> "Method invoked on invalid component '" + $$arez$$_name() + "'" );
    return $$arez$$_doStuff;
  }

  @Override
  public final int hashCode() {
    return Long.hashCode( $$arez$$_id() );
  }

  @Override
  public final boolean equals(final Object o) {
    if ( this == o ) {
      return true;
    } else if ( null == o || !(o instanceof Arez_RefOnAutorunModel) ) {
      return false;
    } else {
      final Arez_RefOnAutorunModel that = (Arez_RefOnAutorunModel) o;;
      return $$arez$$_id() == that.$$arez$$_id();
    }
  }

  @Override
  public final String toString() {
    if ( Arez.areNamesEnabled() ) {
      return "ArezComponent[" + $$arez$$_name() + "]";
    } else {
      return super.toString();
    }
  }
}