package knbit.events.bc.interest.survey.domain.valueobjects.events;

import knbit.events.bc.interest.survey.domain.valueobjects.SurveyId;
import knbit.events.bc.interest.questionnaire.domain.valueobjects.Attendee;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Created by novy on 28.05.15.
 */

@Accessors(fluent = true)
@Value
public class SurveyVotedDownEvent {

    private final SurveyId surveyId;
    private final Attendee attendee;
}