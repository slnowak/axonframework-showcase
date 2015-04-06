package knbit.events.bc.announcement.coordinator;

import com.google.common.collect.Lists;
import knbit.events.bc.announcement.Announcement;
import knbit.events.bc.announcement.Publisher;

import java.util.Collection;

/**
 * Created by novy on 02.04.15.
 */
public class PublisherCoordinator implements Publisher {

    private final Collection<Publisher> publishers = Lists.newLinkedList();

    @Override
    public void publish(final Announcement announcement) {
        if (publishers.isEmpty()) {
            throw new NoPublisherSpecifiedException();
        }

        // todo: consider doing it in async way
        publishers.forEach(publisher -> publisher.publish(announcement));

    }

    public PublisherCoordinator register(Publisher publisher) {
        publishers.add(publisher);
        return this;
    }
}