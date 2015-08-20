package knbit.events.bc.choosingterm.domain.aggregates;

import knbit.events.bc.FixtureFactory;
import knbit.events.bc.choosingterm.domain.exceptions.ReservationExceptions.ReservationAcceptedException;
import knbit.events.bc.choosingterm.domain.exceptions.ReservationExceptions.ReservationCancelledException;
import knbit.events.bc.choosingterm.domain.exceptions.ReservationExceptions.ReservationDoesNotExist;
import knbit.events.bc.choosingterm.domain.exceptions.ReservationExceptions.ReservationRejectedException;
import knbit.events.bc.choosingterm.domain.valuobjects.*;
import knbit.events.bc.choosingterm.domain.valuobjects.commands.AcceptReservationCommand;
import knbit.events.bc.choosingterm.domain.valuobjects.events.*;
import knbit.events.bc.common.domain.valueobjects.EventDetails;
import knbit.events.bc.common.domain.valueobjects.EventId;
import knbit.events.bc.interest.builders.EventDetailsBuilder;
import org.axonframework.test.FixtureConfiguration;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by novy on 19.08.15.
 */
public class AcceptingReservationTest {

    private FixtureConfiguration<UnderChoosingTermEvent> fixture;
    private EventId eventId;
    private EventDetails eventDetails;

    private ReservationId reservationId;
    private EventDuration eventDuration;
    private Capacity capacity;

    @Before
    public void setUp() throws Exception {
        fixture = FixtureFactory.underChoosingTermEventFixtureConfiguration();
        eventId = EventId.of("eventId");
        eventDetails = EventDetailsBuilder
                .instance()
                .build();

        reservationId = ReservationId.of("reservationId");
        eventDuration = EventDuration.of(DateTime.now(), Duration.standardHours(2));
        capacity = Capacity.of(20);
    }

    @Test
    public void shouldNotBeAbleToAcceptNotExistingReservation() throws Exception {
        fixture
                .given(
                        UnderChoosingTermEventCreated.of(eventId, eventDetails)
                )
                .when(
                        AcceptReservationCommand.of(eventId, ReservationId.of("fakeId"), "3.21c")
                )
                .expectException(ReservationDoesNotExist.class);
    }

    @Test
    public void shouldNotBeAbleToAcceptAlreadyAcceptedReservation() throws Exception {
        fixture
                .given(
                        UnderChoosingTermEventCreated.of(eventId, eventDetails),
                        RoomRequestedEvent.of(eventId, reservationId, eventDuration, capacity),
                        ReservationAcceptedEvent.of(eventId, reservationId)
                )
                .when(
                        AcceptReservationCommand.of(eventId, reservationId, "3.21c")
                )
                .expectException(ReservationAcceptedException.class);
    }

    @Test
    public void shouldNotBeAbleToAcceptAlreadyRejectedReservation() throws Exception {
        fixture
                .given(
                        UnderChoosingTermEventCreated.of(eventId, eventDetails),
                        RoomRequestedEvent.of(eventId, reservationId, eventDuration, capacity),
                        ReservationRejectedEvent.of(eventId, reservationId)
                )
                .when(
                        AcceptReservationCommand.of(eventId, reservationId, "3.21c")
                )
                .expectException(ReservationRejectedException.class);
    }

    @Test
    public void shouldNotBeAbleToAcceptCancelledReservation() throws Exception {
        fixture
                .given(
                        UnderChoosingTermEventCreated.of(eventId, eventDetails),
                        RoomRequestedEvent.of(eventId, reservationId, eventDuration, capacity),
                        ReservationCancelledEvent.of(eventId, reservationId)
                )
                .when(
                        AcceptReservationCommand.of(eventId, reservationId, "3.21c")
                )
                .expectException(ReservationCancelledException.class);
    }

    @Test
    public void otherwiseShouldGenerateReservationAcceptedEventAndAddNewTerm() throws Exception {
        fixture
                .given(
                        UnderChoosingTermEventCreated.of(eventId, eventDetails),
                        RoomRequestedEvent.of(eventId, reservationId, eventDuration, capacity)
                )
                .when(
                        AcceptReservationCommand.of(eventId, reservationId, "3.21c")
                )
                .expectEvents(
                        ReservationAcceptedEvent.of(eventId, reservationId),
                        TermAddedEvent.of(eventId, Term.of(eventDuration, capacity, Location.of("3.21c")))
                );
    }
}
