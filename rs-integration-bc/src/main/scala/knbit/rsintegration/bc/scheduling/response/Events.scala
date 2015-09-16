package knbit.rsintegration.bc.scheduling.response

import knbit.rsintegration.bc.common.{Term, Reservation}

case class ResponseInitializedEvent(requestId: String,
                                    reservation: Reservation,
                                    responseStrategy: ResponseStrategy,
                                    schedulingStrategy: ResponseSchedulingStrategy)

case object ResponseFinishedEvent

case object ResponseTerminatedEvent

case object UnresolvedResponseEvent

case object FailureReservationEvent

case object RejectedResponseEvent

case class SuccessReservationEvent(eventId: String, reservationId: String, term: Term)

case class FailureReservationEvent(eventId: String, reservationId: String)

case class ResponseExceedMaxAttemptAmountEvent(requestId: String, reservation: Reservation)