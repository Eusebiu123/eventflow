package ro.eusebiu.notification_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.eusebiu.notification_service.kafka.service.DltReplayService;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/dlt")
@RequiredArgsConstructor
public class DltReplayController {

    private final DltReplayService dltReplayService;

    @PostMapping("/replay")
    public ResponseEntity<Map<String, Object>> replay(
            @RequestParam long offset
    ) {

        if (offset < 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Offset must be non-negative")
            );
        }

        dltReplayService.replay(offset);

        return ResponseEntity.ok(
                Map.of(
                        "message", "DLT message replayed successfully",
                        "offset", offset
                )
        );
    }
}