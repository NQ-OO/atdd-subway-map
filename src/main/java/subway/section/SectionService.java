package subway.section;

import org.springframework.stereotype.Service;

import subway.line.Line;
import subway.line.LineRepository;
import subway.station.Station;
import subway.station.StationRepository;

@Service
public class SectionService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public void addSection(Long lineId, SectionRequest sectionRequest) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        Station upStation = stationRepository.findById(sectionRequest.getUpStationId()).orElseThrow(IllegalArgumentException::new);
        Station downStation = stationRepository.findById(sectionRequest.getDownStationId()).orElseThrow(IllegalArgumentException::new);
        Section section = new Section(upStation, downStation, Long.valueOf(sectionRequest.getDistance()));
        line.addSection(section);
        sectionRepository.save(section);
        lineRepository.save(line);
    }

    public void deleteSection(Long lineId, Long stationId) {
        Line line = lineRepository.findById(lineId).orElseThrow(IllegalArgumentException::new);
        Station stationToDelete = stationRepository.findById(stationId).orElseThrow(IllegalArgumentException::new);
        Section section = line.deleteSection(stationToDelete);
        sectionRepository.delete(section);
        lineRepository.save(line);
    }

}
