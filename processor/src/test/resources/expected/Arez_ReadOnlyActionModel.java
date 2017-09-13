import java.lang.reflect.UndeclaredThrowableException;
import javax.annotation.Generated;
import javax.annotation.Nonnull;
import org.realityforge.arez.ArezContext;
import org.realityforge.arez.spy.ActionCompletedEvent;
import org.realityforge.arez.spy.ActionStartedEvent;

@Generated("org.realityforge.arez.processor.ArezProcessor")
public final class Arez_ReadOnlyActionModel extends ReadOnlyActionModel {
  private static volatile long $$arez$$_nextId;

  private final long $$arez$$_id;

  @Nonnull
  private final ArezContext $$arez$$_context;

  public Arez_ReadOnlyActionModel(@Nonnull final ArezContext $$arez$$_context) {
    super();
    this.$$arez$$_id = $$arez$$_nextId++;
    this.$$arez$$_context = $$arez$$_context;
  }

  private String $$arez$$_id() {
    return "ReadOnlyActionModel." + $$arez$$_id + ".";
  }

  @Override
  public int queryStuff(final long time) {
    boolean $$arez$$_completed = false;
    long $$arez$$_startedAt = 0L;
    try {
      if ( this.$$arez$$_context.areSpiesEnabled() && this.$$arez$$_context.getSpy().willPropagateSpyEvents() ) {
        $$arez$$_startedAt = System.currentTimeMillis();
        this.$$arez$$_context.getSpy().reportSpyEvent( new ActionStartedEvent( $$arez$$_id() + "queryStuff", new Object[]{time} ) );
      }
      final int $$arez$$_result = this.$$arez$$_context.safeFunction(this.$$arez$$_context.areNamesEnabled() ? $$arez$$_id() + "queryStuff" : null, false, () -> super.queryStuff(time) );
      $$arez$$_completed = true;
      if ( this.$$arez$$_context.areSpiesEnabled() && this.$$arez$$_context.getSpy().willPropagateSpyEvents() ) {
        final long $$arez$$_duration = System.currentTimeMillis() - $$arez$$_startedAt;
        this.$$arez$$_context.getSpy().reportSpyEvent( new ActionCompletedEvent( $$arez$$_id() + "queryStuff", new Object[]{time}, $$arez$$_result, $$arez$$_duration ) );
      }
      return $$arez$$_result;
    } catch( final RuntimeException e ) {
      throw e;
    } catch( final Exception e ) {
      throw new UndeclaredThrowableException( e );
    } catch( final Error e ) {
      throw e;
    } catch( final Throwable e ) {
      throw new UndeclaredThrowableException( e );
    } finally {
      if ( !$$arez$$_completed ) {
        final Integer $$arez$$_result = null;
        if ( this.$$arez$$_context.areSpiesEnabled() && this.$$arez$$_context.getSpy().willPropagateSpyEvents() ) {
          final long $$arez$$_duration = System.currentTimeMillis() - $$arez$$_startedAt;
          this.$$arez$$_context.getSpy().reportSpyEvent( new ActionCompletedEvent( $$arez$$_id() + "queryStuff", new Object[]{time}, $$arez$$_result, $$arez$$_duration ) );
        }
      }
    }
  }
}
