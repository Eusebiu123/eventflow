package ro.eusebiu.event_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.eusebiu.event_service.entity.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByActiveTrueOrderByEventDateAsc();

    List<Event> findByCityIgnoreCaseAndActiveTrueOrderByEventDateAsc(String city);
    boolean existsByTitleIgnoreCaseAndVenueIgnoreCaseAndEventDate(String title, String venue, LocalDateTime eventDate);
    List<Event> findByTitleContainingIgnoreCaseAndActiveTrueOrderByEventDateAsc(String title);
}
