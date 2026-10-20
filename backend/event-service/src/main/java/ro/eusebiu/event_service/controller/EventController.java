package ro.eusebiu.event_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ro.eusebiu.event_service.dto.request.CreateEventRequest;
import ro.eusebiu.event_service.dto.request.UpdateEventRequest;
import ro.eusebiu.event_service.dto.response.EventResponse;
import ro.eusebiu.event_service.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> create(@Valid @RequestBody CreateEventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllActive() {
        return ResponseEntity.ok(eventService.getAllActive());
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<EventResponse>> getByCity(@PathVariable String city) {
        return ResponseEntity.ok(eventService.getByCity(city));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateEventRequest request) {
        return ResponseEntity.ok(eventService.update(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        eventService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/search")
    public ResponseEntity<List<EventResponse>> searchByTitle(@RequestParam String title) {
        return ResponseEntity.ok(eventService.searchByTitle(title));
    }
    @PostMapping("/{id}/reserve")
    public ResponseEntity<EventResponse> reserveSeats(
            @PathVariable Long id,
            @RequestParam Integer ticketsCount
    ) {
        return ResponseEntity.ok(eventService.reserveSeats(id, ticketsCount));
    }
}
