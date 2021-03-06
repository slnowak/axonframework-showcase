package knbit.events.bc.choosingterm.domain.aggregates;

import com.google.common.collect.Maps;
import knbit.events.bc.choosingterm.domain.entities.Reservation;
import knbit.events.bc.choosingterm.domain.enums.UnderChoosingTermEventState;
import knbit.events.bc.choosingterm.domain.exceptions.CannotAddOverlappingTermException;
import knbit.events.bc.choosingterm.domain.exceptions.CannotRemoveNotExistingTermException;
import knbit.events.bc.choosingterm.domain.exceptions.ReservationExceptions.ReservationDoesNotExist;
import knbit.events.bc.choosingterm.domain.exceptions.TransitionToEnrollmentExceptions;
import knbit.events.bc.choosingterm.domain.exceptions.UnderChoosingTermEventExceptions;
import knbit.events.bc.choosingterm.domain.valuobjects.*;
import knbit.events.bc.choosingterm.domain.valuobjects.events.ReservationEvents;
import knbit.events.bc.choosingterm.domain.valuobjects.events.TermEvents;
import knbit.events.bc.choosingterm.domain.valuobjects.events.UnderChoosingTermEventEvents;
import knbit.events.bc.common.domain.IdFactory;
import knbit.events.bc.common.domain.IdentifiedDomainAggregateRoot;
import knbit.events.bc.common.domain.valueobjects.EventDetails;
import knbit.events.bc.common.domain.valueobjects.EventId;
import knbit.events.bc.enrollment.domain.exceptions.EventUnderEnrollmentExceptions;
import knbit.events.bc.enrollment.domain.valueobjects.TermClosure;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.axonframework.eventsourcing.annotation.EventSourcedMember;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static knbit.events.bc.choosingterm.domain.enums.UnderChoosingTermEventState.*;


/**
 * Created by novy on 16.08.15.
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UnderChoosingTermEvent extends IdentifiedDomainAggregateRoot<EventId> {

    private EventDetails eventDetails;

    @EventSourcedMember
    private Map<ReservationId, Reservation> reservations = Maps.newHashMap();

    private Map<TermId, Term> terms = Maps.newHashMap();

    private UnderChoosingTermEventState state;

    public UnderChoosingTermEvent(EventId eventId, EventDetails eventDetails) {
        apply(UnderChoosingTermEventEvents.Created.of(eventId, eventDetails));
    }

    @EventSourcingHandler
    private void on(UnderChoosingTermEventEvents.Created event) {
        this.id = event.eventId();
        this.eventDetails = event.eventDetails();

        this.state = CREATED;
    }

    public void addTerm(Term newTerm) {
        rejectOnCancelledOrTransited();
        if (newTermOverlaps(newTerm)) {
            throw new CannotAddOverlappingTermException(id, newTerm);
        }

        apply(TermEvents.TermAdded.of(id, IdFactory.termId(), newTerm));
    }

    private boolean newTermOverlaps(Term newTerm) {
        return terms.values()
                .stream()
                .anyMatch(term -> term.overlaps(newTerm));
    }

    @EventSourcingHandler
    private void on(TermEvents.TermAdded event) {
        terms.put(event.termId(), event.term());
    }

    public void removeTerm(TermId termId) {
        rejectOnCancelledOrTransited();
        if (!terms.containsKey(termId)) {
            throw new CannotRemoveNotExistingTermException(id, termId);
        }

        apply(TermEvents.TermRemoved.of(id, termId));
    }

    @EventSourcingHandler
    private void on(TermEvents.TermRemoved event) {
        terms.remove(event.termId());
    }

    public void bookRoomFor(EventDuration eventDuration, Capacity capacity) {
        rejectOnCancelledOrTransited();

        apply(ReservationEvents.RoomRequested.of(id, IdFactory.reservationId(), eventDuration, capacity));
    }

    @EventSourcingHandler
    private void on(ReservationEvents.RoomRequested event) {
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
        apply(TermEvents.TermAdded.of(id, IdFactory.termId(), termFromReservation));
    }

    public void rejectReservation(ReservationId reservationId) {
        rejectOnNotExistingReservation(reservationId);

        final Reservation reservation = reservations.get(reservationId);
        reservation.reject();
    }

    public void cancelReservation(ReservationId reservationId) {
        rejectOnNotExistingReservation(reservationId);

        final Reservation reservation = reservations.get(reservationId);
        reservation.cancel();
    }

    public void transitToEnrollment(Collection<TermClosure> termClosures) {
        rejectOnCancelledOrTransited();
        rejectIfThereArePendingReservations();
        rejectOnNoTerms();

        apply(
                UnderChoosingTermEventEvents.TransitedToEnrollment.of(
                        id, eventDetails, identifiedTerms(termClosures)
                )
        );
    }

    private Collection<EnrollmentIdentifiedTerm> identifiedTerms(Collection<TermClosure> termClosures) {
        return terms.entrySet()
                .stream()
                .map(termIdAndTerm -> enrollmentIdentifiedTermOf(
                        termIdAndTerm.getKey(), termIdAndTerm.getValue(), termClosures)
                )
                .collect(Collectors.toList());
    }

    private EnrollmentIdentifiedTerm enrollmentIdentifiedTermOf(TermId id, Term term, Collection<TermClosure> termClosures) {
        final TermClosure termClosure = findTermClosureOrThrowException(id, termClosures);
        return EnrollmentIdentifiedTerm.of(id, term, termClosure.lecturers(), termClosure.participantsLimit());
    }

    private TermClosure findTermClosureOrThrowException(TermId termId, Collection<TermClosure> termClosures) {
        return termClosures
                .stream()
                .filter(term -> term.termId().equals(termId))
                .findFirst()
                .orElseThrow(() -> new EventUnderEnrollmentExceptions.NoSuchTermException(id, termId));
    }

    @EventSourcingHandler
    private void on(UnderChoosingTermEventEvents.TransitedToEnrollment event) {
        state = TRANSITED;
    }

    public void failReservation(ReservationId reservationId, String reason) {
        rejectOnNotExistingReservation(reservationId);

        final Reservation reservation = reservations.get(reservationId);
        reservation.fail(reason);
    }

    public void cancel() {
        rejectOnCancelledOrTransited();

        cancelAllPendingReservations();
        apply(UnderChoosingTermEventEvents.Cancelled.of(id));
    }

    private void cancelAllPendingReservations() {
        reservations.values().stream()
                .filter(Reservation::pending)
                .forEach(Reservation::cancel);
    }

    @EventSourcingHandler
    private void on(UnderChoosingTermEventEvents.Cancelled event) {
        this.state = CANCELLED;
    }

    private void rejectOnNotExistingReservation(ReservationId reservationId) {
        if (!reservations.containsKey(reservationId)) {
            throw new ReservationDoesNotExist(reservationId);
        }
    }

    private void rejectIfThereArePendingReservations() {
        final boolean hasAnyPendingReservation = reservations.values()
                .stream()
                .anyMatch(Reservation::pending);

        if (hasAnyPendingReservation) {
            throw new TransitionToEnrollmentExceptions.HasPendingReservations(id);
        }
    }

    private void rejectOnNoTerms() {
        if (terms.isEmpty()) {
            throw new TransitionToEnrollmentExceptions.DoesNotHaveAnyTerms(id);
        }
    }

    private void rejectOnCancelledOrTransited() {
        rejectOnCancelled();
        rejectOnTransited();
    }

    private void rejectOnCancelled() {
        if (state == CANCELLED) {
            throw new UnderChoosingTermEventExceptions.AlreadyCancelled(id);
        }
    }

    private void rejectOnTransited() {
        if (state == TRANSITED) {
            throw new UnderChoosingTermEventExceptions.AlreadyTransitedToEnrollment(id);
        }
    }
}
