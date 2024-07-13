package subway.line;

import java.util.List;

import subway.station.Station;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<Station> stations;

    public LineResponse(Line line, List<Station> stationList) {
        id = line.getId();
        name = line.getName();
        color = line.getColor();
        stations = stationList;
    }

    public Long getId() {
        return id;
    }
}
