package subway.section;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import subway.line.LineResponse;

@RestController
public class SectionController {

    private SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/lines/{lineId}/sections")
    public ResponseEntity<LineResponse> addSection(@PathVariable Long lineId, @RequestBody SectionRequest sectionRequest) {
        try {
            sectionService.addSection(lineId, sectionRequest);
            return ResponseEntity.created(URI.create("/lines/" + lineId.toString() + "/sections")).build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
