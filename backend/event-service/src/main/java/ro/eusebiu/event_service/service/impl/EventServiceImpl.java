package ro.eusebiu.event_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ro.eusebiu.event_service.dto.request.CreateEventRequest;
import ro.eusebiu.event_service.dto.request.UpdateEventRequest;
import ro.eusebiu.event_service.dto.response.EventResponse;
import ro.eusebiu.event_service.entity.Event;
import ro.eusebiu.event_service.exception.ResourceNotFoundException;
import ro.eusebiu.event_service.repository.EventRepository;
import ro.eusebiu.event_service.service.EventService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public EventResponse create(CreateEventRequest request) {
        if (eventRepository.existsByTitleIgnoreCaseAndVenueIgnoreCaseAndEventDate(
                request.title(),
                request.venue(),
                request.eventDate()
        )) {
            throw new IllegalArgumentException("Event already exists for the same title, venue and date");
        }

        Event event = Event.builder()
                .title(request.title())
                .description(request.description())
                .city(request.city())
                .venue(request.venue())
                .eventDate(request.eventDate())
                .price(request.price())
                .availableSeats(request.availableSeats())
                .active(true)
                .build();

        Event savedEvent = eventRepository.save(event);
        return toResponse(savedEvent);
    }

    @Override
    public List<EventResponse> getAllActive() {
        return eventRepository.findByActiveTrueOrderByEventDateAsc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<EventResponse> getByCity(String city) {
        return eventRepository.findByCityIgnoreCaseAndActiveTrueOrderByEventDateAsc(city)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public EventResponse getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return toResponse(event);
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCity(),
                event.getVenue(),
                event.getEventDate(),
                event.getPrice(),
                event.getAvailableSeats(),
                event.isActive()
        );
    }
    @Override
    public EventResponse update(Long id, UpdateEventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        boolean duplicateExists = eventRepository.existsByTitleIgnoreCaseAndVenueIgnoreCaseAndEventDate(
                request.title(),
                request.venue(),
                request.eventDate()
        );

        if (duplicateExists &&
                (!event.getTitle().equalsIgnoreCase(request.title())
                        || !event.getVenue().equalsIgnoreCase(request.venue())
                        || !event.getEventDate().equals(request.eventDate()))) {
            throw new IllegalArgumentException("Event already exists for the same title, venue and date");
        }

        event.setTitle(request.title());
        event.setDescription(request.description());
        event.setCity(request.city());
        event.setVenue(request.venue());
        event.setEventDate(request.eventDate());
        event.setPrice(request.price());
        event.setAvailableSeats(request.availableSeats());

        return toResponse(eventRepository.save(event));
    }

    @Override
    public void deactivate(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: "+ id));
        event.setActive(false);
        eventRepository.save(event);
    }
    @Override
    public List<EventResponse> searchByTitle(String title) {
        return eventRepository.findByTitleContainingIgnoreCaseAndActiveTrueOrderByEventDateAsc(title)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: "+ id));
        eventRepository.delete(event);
    }

    @Override
    public EventResponse reserveSeats(Long id, Integer ticketsCount) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));

        if (!event.isActive()) {
            throw new IllegalArgumentException("Event is not active");
        }

        if (event.getAvailableSeats() < ticketsCount) {
            throw new IllegalArgumentException("Not enough available seats");
        }

        event.setAvailableSeats(event.getAvailableSeats() - ticketsCount);
        Event savedEvent = eventRepository.save(event);
        return toResponse(savedEvent);
    }
}
