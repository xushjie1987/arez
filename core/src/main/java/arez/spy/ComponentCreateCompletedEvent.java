package arez.spy;

import arez.Component;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * Notification when a Component is has finished being created.
 */
public final class ComponentCreateCompletedEvent
  implements SerializableEvent
{
  public static final String TYPE_NAME = EventUtil.getName( ComponentCreateCompletedEvent.class );
  @Nonnull
  private final Component _component;

  public ComponentCreateCompletedEvent( @Nonnull final Component component )
  {
    _component = Objects.requireNonNull( component );
  }

  @Nonnull
  public Component getComponent()
  {
    return _component;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void toMap( @Nonnull final Map<String, Object> map )
  {
    map.put( "type", TYPE_NAME );
    map.put( "component", getComponent().getName() );
  }
}
