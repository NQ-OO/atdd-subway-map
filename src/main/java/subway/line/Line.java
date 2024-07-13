package subway.line;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Column(nullable = false)
    private Long upStationId;

    @Column(nullable = false)
    private Long downStationId;

    @Column(nullable = false)
    private Long distance;

    public Line() {}

    public Line(String name, String color, Long upStationId, Long downStationId, Long distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
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

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public Long getDistance() {
        return distance;
    }

    public void updateLine(LineRequest lineRequest) {
        if (Objects.nonNull(lineRequest.getName())) {
            setName(lineRequest.getName());
        }
        if (Objects.nonNull(lineRequest.getColor())) {
            setColor(lineRequest.getColor());
        }
        if (Objects.nonNull(lineRequest.getUpStationId())) {
            setUpStationId(lineRequest.getUpStationId());
        }
        if (Objects.nonNull(lineRequest.getDownStationId())) {
            setDownStationId(lineRequest.getDownStationId());
        }
        if (Objects.nonNull(lineRequest.getDistance())) {
            setDistance(lineRequest.getDistance());
        }
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setColor(String color) {
        this.color = color;
    }

    private void setUpStationId(Long upStationId) {
        this.upStationId = upStationId;
    }

    private void setDownStationId(Long downStationId) {
        this.downStationId = downStationId;
    }

    private void setDistance(Long distance) {
        this.distance = distance;
    }
}
