package subway.line;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import subway.section.Section;
import subway.section.Sections;
import subway.station.Station;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Embedded
    private Sections sections;

    @ManyToOne
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    public Line() {}

    public Line(String name, String color, Sections sections, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.sections = sections;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Sections getSections() {
        return sections;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    private void setDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void updateLine(LineRequest lineRequest) {
        name = lineRequest.getName();
        color = lineRequest.getColor();
    }

    public void addSection(Section section) {
        sections.addSection(section, downStation);
        setDownStation(section.getDownStation());
    }

}
