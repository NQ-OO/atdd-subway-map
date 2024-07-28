package subway;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.line.LineRequest;

class SectionAcceptanceTest {

    private Long stationId1;
    private Long stationId2;
    private Long stationId3;
    private Long stationId4;
    private Long lineId;

    @BeforeEach
    void init() {
        stationId1 = getStationId(createStation("station1"));
        stationId2 = getStationId(createStation("station2"));
        stationId3 = getStationId(createStation("station3"));
        stationId4 = getStationId(createStation("station4"));
        lineId = getLineId(createLine(new LineRequest("사당", "red", stationId1, stationId2, 10L)));
    }

    /**
     * Given: 새로운 지하철 구간 정보를 입력하고,
     * When: 지하철 구간을 생셩하면
     * Then: 해당 구간이 노선에 추가된다.
     */
    @DisplayName("지하철 구간 추가 성공")
    @Test
    void createSectionSuccess() {
        // Given
        Map<String, Object> createSectionInfoParam = givenParameterWithCorrectStationInfo();
        // When
        ExtractableResponse<Response> response = createSection(createSectionInfoParam);
        // Then
        assertThat(response.statusCode()).isNotEqualTo(HttpStatus.CREATED.value());
    }

    Map<String, Object> givenParameterWithCorrectStationInfo() {
        return Map.of("downStationId", stationId1, "upStationId", stationId2, "distance", 10);
    }

    /**
     * Given: 새로운 구간의 상행역이 해당 노선에 등록되어 있는 하행 종점역이 아닐 때,
     * When: 지하철 구간을 생성하면
     * Then: 예외가 발생한다.
     */
    @DisplayName("지하철 구간 추가 실패 - 상행역 불일치")
    @Test
    void createSectionFail1() {
        // Given
        Map<String, Object> createSectionInfoParam = givenParameterWithInconsistentUpStationInfo();
        // When
        ExtractableResponse<Response> response = createSection(createSectionInfoParam);
        // Then
        assertThat(response.statusCode()).isNotEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    Map<String, Object> givenParameterWithInconsistentUpStationInfo() {
        return Map.of("downStationId", stationId4, "upStationId", stationId3, "distance", 10);
    }

    /**
     * Given: 새로운 구간의 하행역이 해당 노선에 이미 등록되어 있을 때,
     * When: 지하철 구간을 생셩하면
     * Then: 예외가 발생한다.
     */

    @DisplayName("지하철 구간 추가 실패 - 중복되는 하행역")
    @Test
    void createSectionFail2() {
        // Given
        Map<String, Object> createSectionInfoParam = givenParameterWithDuplicateDownStationInfo();
        // When
        ExtractableResponse<Response> response = createSection(createSectionInfoParam);
        // Then
        assertThat(response.statusCode()).isNotEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    Map<String, Object> givenParameterWithDuplicateDownStationInfo() {
        return Map.of("downStationId", stationId1, "upStationId", stationId2, "distance", 10);
    }

    ExtractableResponse<Response> createStation(String stationName) {
        Map<String, String> station = Map.of("name", stationName);
        return RestAssured.given().log().all()
                          .body(station)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/stations")
                          .then().log().all()
                          .extract();
    }

    Long getStationId(ExtractableResponse<Response> stationCreationResponse) {
        Integer stationId = stationCreationResponse.body().jsonPath().get("id");
        return stationId.longValue();
    }

    ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                          .body(lineRequest)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines")
                          .then().log().all()
                          .extract();
    }

    Long getLineId(ExtractableResponse<Response> lineCreationResponse) {
        Integer lineId = lineCreationResponse.body().jsonPath().get("id");
        return lineId.longValue();
    }

    ExtractableResponse<Response> createSection(Map<String, Object> createSectionInfoParam) {
        return RestAssured.given().log().all()
                          .body(createSectionInfoParam)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines/" + lineId.toString() + "/sections")
                          .then().log().all()
                          .extract();
    }
}
