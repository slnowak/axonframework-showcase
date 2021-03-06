package knbit.events.bc.readmodel.kanbanboard.columns

import com.mongodb.DBCollection
import knbit.events.bc.auth.Authorized
import knbit.events.bc.auth.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping('/kanbanboard')
class KanbanBoardController {
    def DBCollection collection

    @Autowired
    KanbanBoardController(@Qualifier('kanban-board') DBCollection collection) {
        this.collection = collection
    }

    @RequestMapping
    @Authorized(Role.EVENTS_MANAGEMENT)
    def kanbanBoard() {
        collection.find().toArray()
                .groupBy { event -> event['eventStatus'] }
    }

}
