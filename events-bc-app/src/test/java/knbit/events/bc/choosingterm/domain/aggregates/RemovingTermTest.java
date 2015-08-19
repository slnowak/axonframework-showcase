package knbit.events.bc.choosingterm.domain.aggregates;

import knbit.events.bc.FixtureFactory;
import knbit.events.bc.choosingterm.domain.exceptions.CannotRemoveNotExistingTermException;
import knbit.events.bc.choosingterm.domain.valuobjects.Capacity;
import knbit.events.bc.choosingterm.domain.valuobjects.EventDuration;
import knbit.events.bc.choosingterm.domain.valuobjects.Location;
import knbit.events.bc.choosingterm.domain.valuobjects.Term;
import knbit.events.bc.choosingterm.domain.valuobjects.commands.RemoveTermCommand;
import knbit.events.bc.choosingterm.domain.valuobjects.events.TermAddedEvent;
import knbit.events.bc.choosingterm.domain.valuobjects.events.TermRemovedEvent;
import knbit.events.bc.choosingterm.domain.valuobjects.events.UnderChoosingTermEventCreated;
import knbit.events.bc.common.domain.valueobjects.EventDetails;
import knbit.events.bc.common.domain.valueobjects.EventId;
import knbit.events.bc.interest.builders.EventDetailsBuilder;
import org.axonframework.test.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by novy on 19.08.15.
 */
public class RemovingTermTest {

    private FixtureConfiguration<UnderChoosingTermEvent> fixture;
    private EventId eventId;
    private EventDetails eventDetails;
    private Term termToRemove;

    @Before
    public void setUp() throws Exception {
        fixture = FixtureFactory.underChoosingTermEventFixtureConfiguration();
        eventId = EventId.of("eventId");
        eventDetails = EventDetailsBuilder
                .instance()
                .build();
        termToRemove = Term.of(
                EventDuration.of(
                        LocalDateTime.of(2015, 1, 1, 18, 30),
                        Duration.ofMinutes(90)
                ),
                Capacity.of(666),
                Location.of("3.28c")
        );
    }

    @Test
    public void shouldNotBeAbleToRemoveNotExistingTerm() throws Exception {
        fixture
                .given(
                        UnderChoosingTermEventCreated.of(eventId, eventDetails)
                )
                .when(
                        RemoveTermCommand.of(
                                eventId,
                                termToRemove.duration().start(),
                                termToRemove.duration().duration(),
                                termToRemove.capacity().value(),
                                termToRemove.location().getValue()
                        )
                )
                .expectException(CannotRemoveNotExistingTermException.class);
    }

    @Test
    public void shouldProduceTermRemovedEventOnSuccessfulRemoval() throws Exception {
        fixture
                .given(
                        UnderChoosingTermEventCreated.of(eventId, eventDetails),
                        TermAddedEvent.of(eventId, termToRemove)
                )
                .when(
                        RemoveTermCommand.of(
                                eventId,
                                termToRemove.duration().start(),
                                termToRemove.duration().duration(),
                                termToRemove.capacity().value(),
                                termToRemove.location().getValue()
                        )
                )
                .expectEvents(
                        TermRemovedEvent.of(eventId, termToRemove)
                );
    }
}
