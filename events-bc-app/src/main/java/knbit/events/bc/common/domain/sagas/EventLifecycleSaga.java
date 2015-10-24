package knbit.events.bc.common.domain.sagas;

import knbit.events.bc.backlogevent.domain.valueobjects.events.BacklogEventEvents;
import knbit.events.bc.backlogevent.domain.valueobjects.events.BacklogEventTransitionEvents;
import knbit.events.bc.choosingterm.domain.valuobjects.commands.UnderChoosingTermEventCommands;
import knbit.events.bc.choosingterm.domain.valuobjects.events.UnderChoosingTermEventEvents;
import knbit.events.bc.enrollment.domain.valueobjects.commands.EventUnderEnrollmentCommands;
import knbit.events.bc.enrollment.domain.valueobjects.events.EventUnderEnrollmentEvents;
import knbit.events.bc.eventready.domain.valueobjects.ReadyCommands;
import knbit.events.bc.interest.domain.valueobjects.commands.InterestAwareEventCommands;
import knbit.events.bc.interest.domain.valueobjects.events.InterestAwareEvents;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.springframework.beans.factory.annotation.Autowired;

public class EventLifecycleSaga extends AbstractAnnotatedSaga {
    private static final String EVENT_ID_PROPERTY = "eventId";
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = EVENT_ID_PROPERTY)
    private void on(BacklogEventEvents.Created event) {
    }


    @SagaEventHandler(associationProperty = EVENT_ID_PROPERTY)
    private void on(BacklogEventTransitionEvents.TransitedToInterestAware event) {
        commandGateway.send(
                InterestAwareEventCommands.Create.of(event.eventId(), event.eventDetails())
        );
    }

    @SagaEventHandler(associationProperty = EVENT_ID_PROPERTY)
    private void on(BacklogEventTransitionEvents.TransitedToUnderChoosingTerm event) {
        commandGateway.send(
                UnderChoosingTermEventCommands.Create.of(event.eventId(), event.eventDetails())
        );
    }

    @SagaEventHandler(associationProperty = EVENT_ID_PROPERTY)
    private void on(InterestAwareEvents.TransitedToUnderChoosingTerm event) {
        commandGateway.send(
                UnderChoosingTermEventCommands.Create.of(event.eventId(), event.eventDetails())
        );
    }

    @SagaEventHandler(associationProperty = EVENT_ID_PROPERTY)
    private void on(UnderChoosingTermEventEvents.TransitedToEnrollment event) {
        commandGateway.send(
                EventUnderEnrollmentCommands.Create.of(event.eventId(), event.eventDetails(), event.terms())
        );
    }

    @SagaEventHandler(associationProperty = EVENT_ID_PROPERTY)
    private void on(EventUnderEnrollmentEvents.TransitedToReady event) {
        commandGateway.send(
                ReadyCommands.Create.of(event.eventId(), event.eventDetails(), event.terms())
        );
    }

    @Autowired
    private void setCommandGateway(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

}
