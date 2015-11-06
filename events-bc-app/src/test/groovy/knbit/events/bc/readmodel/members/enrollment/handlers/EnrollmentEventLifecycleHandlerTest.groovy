package knbit.events.bc.readmodel.members.enrollment.handlers

import com.mongodb.DBCollection
import knbit.events.bc.choosingterm.domain.builders.TermBuilder
import knbit.events.bc.choosingterm.domain.valuobjects.IdentifiedTerm
import knbit.events.bc.choosingterm.domain.valuobjects.TermId
import knbit.events.bc.common.domain.valueobjects.EventDetails
import knbit.events.bc.common.domain.valueobjects.EventId
import knbit.events.bc.enrollment.domain.valueobjects.events.EventUnderEnrollmentEvents
import knbit.events.bc.interest.builders.EventDetailsBuilder
import knbit.events.bc.readmodel.DBCollectionAware
import knbit.events.bc.readmodel.EventDetailsWrapper
import spock.lang.Specification

import static knbit.events.bc.readmodel.EventDetailsWrapper.sectionOrNull
import static knbit.events.bc.readmodel.EventDetailsWrapper.urlOrNull

/**
 * Created by novy on 11.10.15.
 */
class EnrollmentEventLifecycleHandlerTest extends Specification implements DBCollectionAware {

    def EnrollmentEventLifecycleHandler objectUnderTest
    def DBCollection collection

    def EventId eventId
    def EventDetails eventDetails

    void setup() {
        collection = testCollection()
        objectUnderTest = new EnrollmentEventLifecycleHandler(collection)
        eventId = EventId.of("eventId")
        eventDetails = EventDetailsBuilder.defaultEventDetails()
    }

    def "should create new database entry containing event details and terms with zero participant count"() {
        given:
        def firstTerm = IdentifiedTerm.of(
                TermId.of("id1"), TermBuilder.defaultTerm()
        )
        def secondTerm = IdentifiedTerm.of(
                TermId.of("id2"), TermBuilder.defaultTerm()
        )

        when:
        objectUnderTest.on EventUnderEnrollmentEvents.Created.of(
                eventId, eventDetails, [firstTerm, secondTerm]
        )

        then:
        def enrollmentPreview = collection.findOne([eventId: eventId.value()])

        stripMongoIdFrom(enrollmentPreview) == [
                eventId       : eventId.value(),
                name          : eventDetails.name().value(),
                description   : eventDetails.description().value(),
                eventType     : eventDetails.type(),
                imageUrl      : urlOrNull(eventDetails.imageUrl()),
                section       : sectionOrNull(eventDetails.section()),
                terms         : [
                        [
                                termId              : firstTerm.termId().value(),
                                date                : firstTerm.duration().start(),
                                duration            : firstTerm.duration().duration().getStandardMinutes(),
                                limit               : firstTerm.capacity().value(),
                                location            : firstTerm.location().value(),
                                participantsEnrolled: 0
                        ],
                        [
                                termId              : secondTerm.termId().value(),
                                date                : secondTerm.duration().start(),
                                duration            : secondTerm.duration().duration().getStandardMinutes(),
                                limit               : secondTerm.capacity().value(),
                                location            : secondTerm.location().value(),
                                participantsEnrolled: 0
                        ]
                ]
        ]
    }

    def "should remove db entry on event transition"() {
        given:
        collection << [
                [eventId: eventId.value()],
                [eventId: 'anotherId']
        ]

        when:
        objectUnderTest.on EventUnderEnrollmentEvents.TransitedToReady.of(eventId, eventDetails, [])

        then:
        collection.find([eventId: eventId.value()]).toArray() == []
    }
}
