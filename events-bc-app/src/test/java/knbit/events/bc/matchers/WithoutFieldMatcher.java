package knbit.events.bc.matchers;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.axonframework.domain.Message;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Created by novy on 07.05.15.
 */

public class WithoutFieldMatcher {

    public static <GenericObject extends Message<ObjectToMatch>, ObjectToMatch> Matcher<Collection<GenericObject>> matchExactlyIgnoring(String fieldToIgnore, Collection<ObjectToMatch> thoseObjects) {
        return new BaseMatcher<Collection<GenericObject>>() {
            @Override
            public boolean matches(Object item) {
                @SuppressWarnings("unchecked")
                Collection<ObjectToMatch> theseObjects =
                        ((Collection<GenericObject>) item)
                                .stream()
                                .map(GenericObject::getPayload)
                                .collect(Collectors.toList());

                boolean bothHaveSameSize = theseObjects.size() == thoseObjects.size();
                return bothHaveSameSize && containsSameObjectsOmittingField(theseObjects, thoseObjects);
            }

            private boolean containsSameObjectsOmittingField(Collection<ObjectToMatch> theseObjects,
                                                             Collection<ObjectToMatch> thoseObjects) {

                final Iterator<ObjectToMatch> thisIterator = theseObjects.iterator();
                final Iterator<ObjectToMatch> thatIterator = thoseObjects.iterator();

                while (thisIterator.hasNext() && thatIterator.hasNext()) {
                    final ObjectToMatch thisObject = thisIterator.next();
                    final ObjectToMatch thatObject = thatIterator.next();

                    if (!EqualsBuilder.reflectionEquals(thisObject, thatObject, fieldToIgnore)) {
                        return false;
                    }
                }

                return true;

            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(thoseObjects);
            }
        };
    }

}
