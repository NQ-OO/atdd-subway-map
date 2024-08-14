package subway.section;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import subway.station.Station;

@Embeddable
public class Sections {
    @OneToMany
    private List<Section> sections;

    public Sections(Section section) {
        sections = List.of(section);
    }

    public Sections() {
        sections = new ArrayList<Section>();
    }

    public List<Station> toStationList() {
        Set<Station> result = new HashSet<>();
        for (Section section : sections) {
            result.addAll(Set.copyOf(section.toStationList()));
        }
        return result.stream().toList();
    }

    public boolean checkDuplicateStation(Station stationToCheck) {
        return toStationList().stream().anyMatch(station -> station.equals(stationToCheck));
    }

    public void addSection(Section section, Station lineDownStation) {
        validationCheck(section, lineDownStation);
        sections.add(section);
    }

    private void validationCheck(Section section, Station lineDownStation) {
        checkConnectivity(section.getUpStation(), lineDownStation);
        checkIfThereAreNoDuplicateStation(section.getDownStation());
    }

    private void checkConnectivity(Station sectionUpStation, Station lineDownStation) {
        if (!sectionUpStation.equals(lineDownStation)) {
            throw new IllegalStateException("새로운 구간의 상행역은 해당 노선에 등록되어있는 하행 종점역이어야 합니디");
        }
    }

    private void checkIfThereAreNoDuplicateStation(Station downStation) {
        if (checkDuplicateStation(downStation)) {
            throw new IllegalStateException("이미 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없습니다.");
        }
    }
}
