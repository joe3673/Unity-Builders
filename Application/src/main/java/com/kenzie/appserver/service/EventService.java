package com.kenzie.appserver.service;

import com.kenzie.appserver.repositories.EventRepository;
import com.kenzie.appserver.repositories.UserRepository;
import com.kenzie.appserver.repositories.model.EventRecord;
import com.kenzie.appserver.service.model.Event;
import com.kenzie.appserver.service.model.User;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventService {
    private EventRepository eventRepository;

    @Autowired
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> findAllEvents() {
        List<Event> events = new ArrayList<>();

        Iterable<EventRecord> eventRecordIterable = eventRepository.findAll();
        for (EventRecord record : eventRecordIterable) {
            events.add(new Event(
                    record.getEventID(),
                    record.getName(),
                    record.getLocation(),
                    record.getStartTime(),
                    record.getEndTime(),
                    record.getPeopleAttending(),
                    record.getPeopleAttended(),
                    record.getEventSponsor()));
        }
        return events;
    }

    public Event findByEventId(String eventId) {
        Event eventFromBackendService = eventRepository.findById(eventId)
                .map(event -> new Event(
                        event.getEventID(),
                        event.getName(),
                        event.getLocation(),
                        event.getStartTime(),
                        event.getEndTime(),
                        event.getPeopleAttending(),
                        event.getPeopleAttended(),
                        event.getEventSponsor()))
                .orElse(null);

        return eventFromBackendService;
    }

    public Event addNewEvent(Event event) {
        EventRecord eventRecord = new EventRecord(
                event.getEventID(),
                event.getName(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime(),
                event.getPeopleAttending(),
                event.getPeopleAttended(),
                event.getEventSponsor());
        eventRepository.save(eventRecord);
        return event;
    }

    public void updateEvent(Event event) {
        if (eventRepository.existsById(event.getEventID())) {
            EventRecord eventRecord = new EventRecord(
                    event.getEventID(),
                    event.getName(),
                    event.getLocation(),
                    event.getStartTime(),
                    event.getEndTime(),
                    event.getPeopleAttending(),
                    event.getPeopleAttended(),
                    event.getEventSponsor());
            eventRepository.save(eventRecord);
        }
    }

    public void deleteEvent(String eventId) {
        Event eventToDelete = findByEventId(eventId);

        if (eventToDelete != null && hasEventOccurred(eventToDelete.getEndTime())) {
            eventRepository.deleteById(eventId);
        } else {
//            Event not found or has not occurred, throw exception or log message?
        }
    }
    private boolean hasEventOccurred(LocalDateTime eventEndTime) {
        return LocalDateTime.now().isAfter(eventEndTime);
    }

    public List<Event> getEventsAttendedByFriends(String userId) {
        User user = UserRepository.findById(userId).orElse(null);

        if (user == null) {
            return null;
        }

        List<Event> eventsAttendedByFriends = new ArrayList<>();

        for (User friend : user.getFriends()) {
            eventsAttendedByFriends.addAll(friend.getAttendedEvents);
        }
        return eventsAttendedByFriends;
    }
}
