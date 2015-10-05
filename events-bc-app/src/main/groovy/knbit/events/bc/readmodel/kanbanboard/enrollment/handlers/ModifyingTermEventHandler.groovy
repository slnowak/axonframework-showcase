package knbit.events.bc.readmodel.kanbanboard.enrollment.handlers

import com.mongodb.DBCollection
import knbit.events.bc.choosingterm.domain.valuobjects.TermId
import knbit.events.bc.common.domain.valueobjects.EventId
import knbit.events.bc.enrollment.domain.valueobjects.Lecturer
import knbit.events.bc.enrollment.domain.valueobjects.events.TermModifyingEvents
import org.axonframework.eventhandling.annotation.EventHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * Created by novy on 05.10.15.
 */

@Component
class ModifyingTermEventHandler {

    def DBCollection enrollmentCollection

    @Autowired
    ModifyingTermEventHandler(@Qualifier("enrollment") DBCollection enrollmentCollection) {
        this.enrollmentCollection = enrollmentCollection
    }

    @EventHandler
    def on(TermModifyingEvents.LecturerAssigned event) {
        def eventId = event.eventId()
        def termId = event.termId()
        def lecturer = event.lecturer()

        enrollmentCollection.update(
                query(eventId, termId),
                [$set: ['terms.$.lecturer': lecturerDataFrom(lecturer)]]
        )
    }

    @EventHandler
    def on(TermModifyingEvents.ParticipantLimitSet event) {
        def eventId = event.eventId()
        def termId = event.termId()
        def participantsLimit = event.participantsLimit()

        enrollmentCollection.update(
                query(eventId, termId),
                [$set: ['terms.$.participantsLimit': participantsLimit.value()]]
        )
    }

    private static def query(EventId eventId, TermId termId) {
        [
                domainId      : eventId.value(),
                'terms.termId': termId.value()
        ]
    }

    private static def lecturerDataFrom(Lecturer lecturer) {
        [
                firstName: lecturer.firstName(),
                lastName : lecturer.lastName()
        ]
    }
}
