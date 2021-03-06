package knbit.events.bc.choosingterm.domain.entities;

import knbit.events.bc.choosingterm.domain.valuobjects.Capacity;
import knbit.events.bc.choosingterm.domain.valuobjects.EventDuration;
import knbit.events.bc.choosingterm.domain.valuobjects.ReservationId;
import knbit.events.bc.choosingterm.domain.valuobjects.events.ReservationEvents;
import knbit.events.bc.common.domain.IdentifiedDomainEntity;
import knbit.events.bc.common.domain.valueobjects.EventId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;

import java.util.stream.Stream;

/**
 * Created by novy on 19.08.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true)
public class Reservation extends IdentifiedDomainEntity<ReservationId> {

    private EventId eventId;
    private ReservationId reservationId;

    @Getter
    private EventDuration eventDuration;
    @Getter
    private Capacity capacity;
    private ReservationStatus reservationStatus;
    // TODO: move to status
    private String message;

    public Reservation(EventId eventId, ReservationId reservationId, EventDuration eventDuration, Capacity capacity) {
        this.eventId = eventId;
        this.id = reservationId;
        this.eventDuration = eventDuration;
        this.capacity = capacity;
        // todo shouldn't we wait for an external confirmation?
        this.reservationStatus = ReservationStatus.PENDING;
    }

    private boolean concernedWith(ReservationEvents.ReservationEvent event) {
        return id.equals(event.reservationId());
    }

    public void accept() {
        rejectOn(ReservationStatus.ACCEPTED, ReservationStatus.REJECTED, ReservationStatus.CANCELLED, ReservationStatus.FAILED);
        apply(ReservationEvents.ReservationAccepted.of(eventId, id));
    }

    public void reject() {
        rejectOn(ReservationStatus.ACCEPTED, ReservationStatus.REJECTED, ReservationStatus.CANCELLED, ReservationStatus.FAILED);
        apply(ReservationEvents.ReservationRejected.of(eventId, id));
    }

    public void fail(String cause) {
        rejectOn(ReservationStatus.ACCEPTED, ReservationStatus.REJECTED, ReservationStatus.CANCELLED, ReservationStatus.FAILED);
        apply(ReservationEvents.ReservationFailed.of(eventId, id, cause));
    }

    public void cancel() {
        rejectOn(ReservationStatus.ACCEPTED, ReservationStatus.REJECTED, ReservationStatus.CANCELLED, ReservationStatus.FAILED);
        apply(ReservationEvents.ReservationCancelled.of(eventId, id));
    }

    public boolean pending() {
        return reservationStatus == ReservationStatus.PENDING;
    }

    private void rejectOn(ReservationStatus... statuses) {
        Stream.of(statuses)
                .filter(possibleStatus -> possibleStatus == reservationStatus)
                .findAny()
                .ifPresent(invalidStatus -> invalidStatus.rejectOn(id));
    }

    @EventSourcingHandler
    private void on(ReservationEvents.ReservationAccepted event) {
        changeStatusTo(event, ReservationStatus.ACCEPTED);
    }

    @EventSourcingHandler
    private void on(ReservationEvents.ReservationRejected event) {
        changeStatusTo(event, ReservationStatus.REJECTED);
    }

    @EventSourcingHandler
    private void on(ReservationEvents.ReservationCancelled event) {
        changeStatusTo(event, ReservationStatus.CANCELLED);
    }

    @EventSourcingHandler
    private void on(ReservationEvents.ReservationFailed event) {
        changeStatusTo(event, ReservationStatus.FAILED);
        message = event.cause();
    }

    private void changeStatusTo(ReservationEvents.ReservationEvent event, ReservationStatus newStatus) {
        if (concernedWith(event)) {
            reservationStatus = newStatus;
        }
    }
}

