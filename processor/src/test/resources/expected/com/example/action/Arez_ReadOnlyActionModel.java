package com.example.action;

import javax.annotation.Generated;
import javax.annotation.Nonnull;
import org.realityforge.arez.Arez;
import org.realityforge.arez.ArezContext;
import org.realityforge.arez.Disposable;
import org.realityforge.arez.spy.ActionCompletedEvent;
import org.realityforge.arez.spy.ActionStartedEvent;

@Generated("org.realityforge.arez.processor.ArezProcessor")
public final class Arez_ReadOnlyActionModel extends ReadOnlyActionModel implements Disposable {
  private static volatile long $$arez$$_nextId;

  private final long $$arez$$_id;

  private boolean $$arez$$_disposed;

  @Nonnull
  private final ArezContext $$arez$$_context;

  public Arez_ReadOnlyActionModel() {
    super();
    this.$$arez$$_context = Arez.context();
    this.$$arez$$_id = $$arez$$_nextId++;
  }

  final long $$arez$$_id() {
    return $$arez$$_id;
  }

  String $$arez$$_name() {
    return "ReadOnlyActionModel." + $$arez$$_id();
  }

  @Override
  public boolean isDisposed() {
    return $$arez$$_disposed;
  }

  @Override
  public void dispose() {
    if ( !isDisposed() ) {
      $$arez$$_disposed = true;
    }
  }

  @Override
  public int queryStuff(final long time) {
    assert !$$arez$$_disposed;
    Throwable $$arez$$_throwable = null;
    boolean $$arez$$_completed = false;
    long $$arez$$_startedAt = 0L;
    try {
      if ( this.$$arez$$_context.areSpiesEnabled() && this.$$arez$$_context.getSpy().willPropagateSpyEvents() ) {
        $$arez$$_startedAt = System.currentTimeMillis();
        this.$$arez$$_context.getSpy().reportSpyEvent( new ActionStartedEvent( $$arez$$_name() + ".queryStuff", false, new Object[]{time} ) );
      }
      final int $$arez$$_result = this.$$arez$$_context.safeFunction(this.$$arez$$_context.areNamesEnabled() ? $$arez$$_name() + ".queryStuff" : null, false, () -> super.queryStuff(time) );
      $$arez$$_completed = true;
      if ( this.$$arez$$_context.areSpiesEnabled() && this.$$arez$$_context.getSpy().willPropagateSpyEvents() ) {
        final long $$arez$$_duration = System.currentTimeMillis() - $$arez$$_startedAt;
        this.$$arez$$_context.getSpy().reportSpyEvent( new ActionCompletedEvent( $$arez$$_name() + ".queryStuff", false, new Object[]{time}, true, $$arez$$_result, $$arez$$_throwable, $$arez$$_duration ) );
      }
      return $$arez$$_result;
    } catch( final RuntimeException $$arez$$_e ) {
      $$arez$$_throwable = $$arez$$_e;
      throw $$arez$$_e;
    } catch( final Exception $$arez$$_e ) {
      $$arez$$_throwable = $$arez$$_e;
      throw new IllegalStateException( $$arez$$_e );
    } catch( final Error $$arez$$_e ) {
      $$arez$$_throwable = $$arez$$_e;
      throw $$arez$$_e;
    } catch( final Throwable $$arez$$_e ) {
      $$arez$$_throwable = $$arez$$_e;
      throw new IllegalStateException( $$arez$$_e );
    } finally {
      if ( !$$arez$$_completed ) {
        final Integer $$arez$$_result = null;
        if ( this.$$arez$$_context.areSpiesEnabled() && this.$$arez$$_context.getSpy().willPropagateSpyEvents() ) {
          final long $$arez$$_duration = System.currentTimeMillis() - $$arez$$_startedAt;
          this.$$arez$$_context.getSpy().reportSpyEvent( new ActionCompletedEvent( $$arez$$_name() + ".queryStuff", false, new Object[]{time}, true, $$arez$$_result, $$arez$$_throwable, $$arez$$_duration ) );
        }
      }
    }
  }
}
