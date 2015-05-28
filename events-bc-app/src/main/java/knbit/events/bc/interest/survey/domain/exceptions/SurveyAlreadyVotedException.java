package knbit.events.bc.interest.survey.domain.exceptions;

import knbit.events.bc.common.domain.exceptions.DomainException;
import knbit.events.bc.interest.survey.domain.valueobjects.SurveyId;
import knbit.events.bc.interest.questionnaire.domain.valueobjects.Attendee;

/**
 * Created by novy on 28.05.15.
 */
public class SurveyAlreadyVotedException extends DomainException {

    private static final String ERROR_MESSAGE_TEMPLATE =
            "Survey with id %s already voted by attendee %s";

    public SurveyAlreadyVotedException(SurveyId surveyId, Attendee attendee) {
        super(String.format(ERROR_MESSAGE_TEMPLATE, surveyId, attendee));
    }
}