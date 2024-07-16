package subway.line;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import subway.station.Station;
import subway.station.StationRepository;

@Service
public class LineService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse createLine(LineRequest lineRequest) {
        Station upStation = stationRepository.findById(lineRequest.getUpStationId()).orElseThrow(IllegalArgumentException::new);
        Station downStation = stationRepository.findById(lineRequest.getDownStationId()).orElseThrow(IllegalArgumentException::new);
        Line line = lineRepository.save(new Line(lineRequest.getName(), lineRequest.getColor(), upStation, downStation, lineRequest.getDistance()));
        return new LineResponse(line);
    }

    public List<LineResponse> getAllLines() {
        List<Line> allLines = lineRepository.findAll();
        return allLines.stream()
                       .map(LineResponse::new)
                       .collect(Collectors.toList());
    }

    public LineResponse getLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return new LineResponse(line);
    }

    public void updateLine(LineRequest lineRequest, Long id) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        line.updateLine(lineRequest);
        lineRepository.save(line);
    }

    public void deleteLine(Long id) {
        Line line = lineRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        lineRepository.delete(line);
    }
}
