package knbit.events.bc.choosingterm.domain.aggregates;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import knbit.events.bc.choosingterm.domain.entities.Reservation;
import knbit.events.bc.choosingterm.domain.enums.UnderChoosingTermEventState;
import knbit.events.bc.choosingterm.domain.exceptions.CannotAddOverlappingTermException;
import knbit.events.bc.choosingterm.domain.exceptions.CannotRemoveNotExistingTermException;
import knbit.events.bc.choosingterm.domain.exceptions.ReservationExceptions.ReservationDoesNotExist;
import knbit.events.bc.choosingterm.domain.valuobjects.*;
import knbit.events.bc.choosingterm.domain.valuobjects.events.RoomRequestedEvent;
import knbit.events.bc.choosingterm.domain.valuobjects.events.TermAddedEvent;
import knbit.events.bc.choosingterm.domain.valuobjects.events.TermRemovedEvent;
import knbit.events.bc.choosingterm.domain.valuobjects.events.UnderChoosingTermEventCreated;
import knbit.events.bc.common.domain.IdentifiedDomainAggregateRoot;
import knbit.events.bc.common.domain.valueobjects.EventDetails;
import knbit.events.bc.common.domain.valueobjects.EventId;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.axonframework.eventsourcing.annotation.EventSourcedMember;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import java.util.Collection;
import java.util.Map;

import static knbit.events.bc.choosingterm.domain.enums.UnderChoosingTermEventState.CREATED;


/**
 * Created by novy on 16.08.15.
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UnderChoosingTermEvent extends IdentifiedDomainAggregateRoot<EventId> {

    private EventDetails eventDetails;

    @EventSourcedMember
    private Map<ReservationId, Reservation> reservations = Maps.newHashMap();

    private Collection<Term> terms = Sets.newHashSet();
    private UnderChoosingTermEventState state;

    public UnderChoosingTermEvent(EventId eventId, EventDetails eventDetails) {
        apply(UnderChoosingTermEventCreated.of(eventId, eventDetails));
    }

    @EventSourcingHandler
    private void on(UnderChoosingTermEventCreated event) {
        this.id = event.eventId();
        this.eventDetails = event.eventDetails();

        this.state = CREATED;
    }

    public void addTerm(Term newTerm) {
        if (newTermOverlaps(newTerm)) {
            throw new CannotAddOverlappingTermException(id, newTerm);
        }

        apply(TermAddedEvent.of(id, newTerm));
    }

    private boolean newTermOverlaps(Term newTerm) {
        return terms
                .stream()
                .anyMatch(term -> term.overlaps(newTerm));
    }

    @EventSourcingHandler
    private void on(TermAddedEvent event) {
        terms.add(event.term());
    }

    public void removeTerm(Term termToRemove) {
        if (!terms.contains(termToRemove)) {
            throw new CannotRemoveNotExistingTermException(id, termToRemove);
        }

        apply(TermRemovedEvent.of(id, termToRemove));
    }

    @EventSourcingHandler
    private void on(TermRemovedEvent event) {
        terms.remove(event.term());
    }

    // todo: maybe propose term or somethin' like that?
    public void bookRoomFor(EventDuration eventDuration, Capacity capacity) {
        final ReservationId reservationId = new ReservationId();
        apply(RoomRequestedEvent.of(id, reservationId, eventDuration, capacity));
    }

    @EventSourcingHandler
    private void on(RoomRequestedEvent event) {
        final ReservationId reservationId = event.reservationId();
        final Reservation reservation = new Reservation(
                id, reservationId, event.eventDuration(), event.capacity()
        );
        reservations.put(reservationId, reservation);
    }

    public void acceptReservationWithLocation(ReservationId reservationId, Location location) {
        rejectOnNotExistingReservation(reservationId);

        final Reservation reservation = reservations.get(reservationId);
        reservation.accept();

        final Term termFromReservation = Term.of(reservation.eventDuration(), reservation.capacity(), location);
        apply(TermAddedEvent.of(id, termFromReservation));
    }

    private void rejectOnNotExistingReservation(ReservationId reservationId) {
        if (!reservations.containsKey(reservationId)) {
            throw new ReservationDoesNotExist(reservationId);
        }
    }
}
