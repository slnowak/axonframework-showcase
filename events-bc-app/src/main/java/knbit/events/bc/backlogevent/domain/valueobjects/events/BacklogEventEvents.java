package knbit.events.bc.backlogevent.domain.valueobjects.events;

import knbit.events.bc.common.domain.valueobjects.EventCancelled;
import knbit.events.bc.common.domain.valueobjects.EventDetails;
import knbit.events.bc.common.domain.valueobjects.EventId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Created by novy on 22.08.15.
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BacklogEventEvents {

    @Value(staticConstructor = "of")
    @Accessors(fluent = true)
    public static class Created {

        EventId eventId;
        EventDetails eventDetails;
    }

    @Accessors(fluent = true)
    @Value(staticConstructor = "of")
    public static class EventDetailsChanged {

        EventId eventId;
        EventDetails oldDetails;
        EventDetails newDetails;
    }

    @Value(staticConstructor = "of")
    @Accessors(fluent = true)
    public static class Cancelled implements EventCancelled {

        EventId eventId;
    }
}
