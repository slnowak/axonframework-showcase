package knbit.events.bc.interest.viewmodel.members.infrastructure

import com.mongodb.DB
import com.mongodb.DBCollection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration("survey-repository-configuration")
class RepositoryConfig {

    @Bean(name = "survey-events")
    def DBCollection surveyEventsCollection(DB db) {
        db.surveyEventsCollection
    }

}
