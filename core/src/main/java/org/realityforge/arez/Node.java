package org.realityforge.arez;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A node within an Arez dependency graph.
 * The node is a named element within a specific Arez system that forms part of the dependency graph.
 */
public abstract class Node
  implements Disposable
{
  /**
   * Reference to the system to which this node belongs.
   */
  @Nonnull
  private final ArezContext _context;
  /**
   * A human consumable name for node. It should be non-null if {@link ArezConfig#enableNames()} returns
   * true and <tt>null</tt> otherwise.
   */
  @Nullable
  private final String _name;

  Node( @Nonnull final ArezContext context, @Nullable final String name )
  {
    Guards.invariant( () -> ArezConfig.enableNames() || null == name,
                      () -> String.format( "Node passed a name '%s' but ArezConfig.enableNames() is false", name ) );
    _context = Objects.requireNonNull( context );
    _name = ArezConfig.enableNames() ? Objects.requireNonNull( name ) : null;
  }

  /**
   * Return the name of the node.
   * This method should NOT be invoked unless {@link ArezConfig#enableNames()} returns true and will throw an
   * exception if invariant checking is enabled.
   *
   * @return the name of the node.
   */
  @Nonnull
  public final String getName()
  {
    Guards.invariant( ArezConfig::enableNames,
                      () -> "Node.getName() invoked when ArezConfig.enableNames() is false" );
    assert null != _name;
    return _name;
  }

  @Nonnull
  final ArezContext getContext()
  {
    return _context;
  }

  /**
   * {@inheritDoc}
   */
  @Nonnull
  @Override
  public final String toString()
  {
    if ( ArezConfig.enableNames() )
    {
      return getName();
    }
    else
    {
      return super.toString();
    }
  }

  /**
   * Return true if spy events will be propagated.
   * This means spies are enabled and there is at least one spy event handler present.
   *
   * @return true if spy events will be propagated, false otherwise.
   */
  protected final boolean willPropagateSpyEvents()
  {
    return ArezConfig.enableSpy() && getSpy().willPropagateSpyEvents();
  }

  /**
   * Return the spy associated with context.
   * This method should not be invoked unless {@link ArezConfig#enableSpy()} returns true.
   *
   * @return the spy associated with context.
   */
  @Nonnull
  protected final Spy getSpy()
  {
    return getContext().getSpy();
  }

  /**
   * Report a spy event.
   *
   * @param event the event that occurred.
   */
  protected final void reportSpyEvent( @Nonnull final Object event )
  {
    getSpy().reportSpyEvent( event );
  }
}
