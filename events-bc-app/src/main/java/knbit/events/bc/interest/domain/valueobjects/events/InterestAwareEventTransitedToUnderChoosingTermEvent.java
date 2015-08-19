package knbit.events.bc.interest.domain.valueobjects.events;

import knbit.events.bc.common.domain.valueobjects.EventDetails;
import knbit.events.bc.common.domain.valueobjects.EventId;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Created by novy on 16.08.15.
 */

@Accessors(fluent = true)
@Value(staticConstructor = "of")
public class InterestAwareEventTransitedToUnderChoosingTermEvent {

    private final EventId eventId;
    private final EventDetails eventDetails;
}
