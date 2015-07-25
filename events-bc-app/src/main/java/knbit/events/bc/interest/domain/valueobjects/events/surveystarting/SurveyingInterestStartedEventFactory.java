package knbit.events.bc.interest.domain.valueobjects.events.surveystarting;

import knbit.events.bc.common.domain.valueobjects.EventId;
import knbit.events.bc.interest.domain.policies.surveyinginterest.InterestPolicy;

/**
 * Created by novy on 28.05.15.
 */
public interface SurveyingInterestStartedEventFactory {

    SurveyingInterestStartedEvent newSurveyingInterestStartedEvent(EventId eventId, InterestPolicy thresholdPolicy);
}
