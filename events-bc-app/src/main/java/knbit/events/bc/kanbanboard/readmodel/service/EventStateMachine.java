package knbit.events.bc.kanbanboard.readmodel.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import knbit.events.bc.common.readmodel.EventStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static knbit.events.bc.common.readmodel.EventStatus.*;

public class EventStateMachine {
    private static final Map<EventStatus, List<EventStatus>> reachableStates = Maps.newHashMap();

    static {
        reachableStates.put(BACKLOG, Arrays.asList(BACKLOG, SURVEY_INTEREST, CHOOSING_TERM));
        reachableStates.put(SURVEY_INTEREST, Arrays.asList(SURVEY_INTEREST, CHOOSING_TERM));
        reachableStates.put(CHOOSING_TERM, Arrays.asList(CHOOSING_TERM, ENROLLMENT));
        reachableStates.put(ENROLLMENT, Arrays.asList(ENROLLMENT, READY));
    }

    public static List<EventStatus> match(EventStatus status) {
        return reachableStates.getOrDefault(status, Lists.newArrayList());
    }

}
