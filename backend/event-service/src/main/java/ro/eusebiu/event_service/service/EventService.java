package ro.eusebiu.event_service.service;

import ro.eusebiu.event_service.dto.request.CreateEventRequest;
import ro.eusebiu.event_service.dto.request.UpdateEventRequest;
import ro.eusebiu.event_service.dto.response.EventResponse;

import java.util.List;

public interface EventService {

    EventResponse create(CreateEventRequest request);

    List<EventResponse> getAllActive();

    List<EventResponse> getByCity(String city);

    EventResponse getById(Long id);
    EventResponse update(Long id, UpdateEventRequest request);
    void deactivate(Long id);
    List<EventResponse> searchByTitle(String title);

    void delete(Long id);
    EventResponse reserveSeats(Long id, Integer ticketsCount);
}