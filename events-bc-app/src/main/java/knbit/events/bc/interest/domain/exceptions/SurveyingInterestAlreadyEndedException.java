package knbit.events.bc.interest.domain.exceptions;

import knbit.events.bc.common.domain.exceptions.DomainException;
import knbit.events.bc.common.domain.valueobjects.EventId;

/**
 * Created by novy on 28.05.15.
 */
public class SurveyingInterestAlreadyEndedException extends DomainException {

    private static final String ERROR_MESSAGE_TEMPLATE = "Surveying interest for event with id %s already ended!";

    public SurveyingInterestAlreadyEndedException(EventId eventId) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, eventId));
    }
}
