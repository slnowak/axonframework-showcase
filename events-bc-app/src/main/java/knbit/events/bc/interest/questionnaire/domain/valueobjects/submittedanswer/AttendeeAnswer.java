package knbit.events.bc.interest.questionnaire.domain.valueobjects.submittedanswer;

import knbit.events.bc.interest.questionnaire.domain.valueobjects.Attendee;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Created by novy on 25.05.15.
 */

@Accessors(fluent = true)
@Value(staticConstructor = "of")
public class AttendeeAnswer {

    private final Attendee attendee;
    private final List<SubmittedAnswer> submittedAnswers;

}
