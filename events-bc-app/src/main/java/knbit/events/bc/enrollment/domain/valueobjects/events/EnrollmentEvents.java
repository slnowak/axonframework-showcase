package knbit.events.bc.enrollment.domain.valueobjects.events;

import knbit.events.bc.common.domain.valueobjects.EventId;
import knbit.events.bc.enrollment.domain.valueobjects.MemberId;
import knbit.events.bc.choosingterm.domain.valuobjects.TermId;
import lombok.Value;
import lombok.experimental.Accessors;

/**
 * Created by novy on 03.10.15.
 */

public interface EnrollmentEvents {

    @Accessors(fluent = true)
    @Value(staticConstructor = "of")
    class ParticipantEnrolledForTerm implements TermEvent {

        EventId eventId;
        TermId termId;
        MemberId memberId;
    }

    @Accessors(fluent = true)
    @Value(staticConstructor = "of")
    class ParticipantDisenrolledFromTerm implements TermEvent {

        EventId eventId;
        TermId termId;
        MemberId memberId;
    }
}
