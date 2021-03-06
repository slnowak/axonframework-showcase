package knbit.events.bc.eventproposal.domain;

import knbit.events.bc.eventproposal.domain.aggregates.EventProposal;
import knbit.events.bc.eventproposal.domain.aggregates.EventProposalFactory;
import knbit.events.bc.eventproposal.domain.valueobjects.commands.EventProposalCommands;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by novy on 05.05.15.
 */

@Component
public class EventProposalCommandHandler {

    private final Repository<EventProposal> repository;

    @Autowired
    public EventProposalCommandHandler(@Qualifier("eventProposalRepository") Repository<EventProposal> repository) {
        this.repository = repository;
    }

    @CommandHandler
    public void handle(EventProposalCommands.ProposeEvent command) {
        repository.add(
                EventProposalFactory.newEventProposal(
                        command.eventProposalId(), command.name(), command.description(),
                        command.eventType(), command.imageUrl()
                )
        );
    }

    @CommandHandler
    public void handle(EventProposalCommands.AcceptProposal command) {
        final EventProposal eventProposal = repository.load(command.eventProposalId());
        eventProposal.accept();
    }

    @CommandHandler
    public void handle(EventProposalCommands.RejectProposal command) {
        final EventProposal eventProposal = repository.load(command.eventProposalId());
        eventProposal.reject();
    }
}
