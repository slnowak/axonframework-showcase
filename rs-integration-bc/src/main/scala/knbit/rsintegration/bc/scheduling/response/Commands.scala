package knbit.rsintegration.bc.scheduling.response

import knbit.rsintegration.bc.common.Reservation

case class InitializeResponseCommand(requestId: String,
                                     reservation: Reservation,
                                     responseStrategy: ResponseStrategy,
                                     schedulingStrategy: SchedulingStrategy)

case object SendResponseCommand