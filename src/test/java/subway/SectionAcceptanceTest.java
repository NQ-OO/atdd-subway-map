package subway;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import subway.line.LineRequest;


@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
     * When: 지하철 구간을 생성하면
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    Map<String, Object> givenParameterWithCorrectStationInfo() {
        return Map.of("downStationId", stationId3, "upStationId", stationId2, "distance", 10);
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    Map<String, Object> givenParameterWithInconsistentUpStationInfo() {
        return Map.of("downStationId", stationId4, "upStationId", stationId3, "distance", 10);
    }

    /**
     * Given: 새로운 구간의 하행역이 해당 노선에 이미 등록되어 있을 때,
     * When: 지하철 구간을 생성하면
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
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    Map<String, Object> givenParameterWithDuplicateDownStationInfo() {
        return Map.of("downStationId", stationId1, "upStationId", stationId2, "distance", 10);
    }

    /**
     * Given: 지하철 노선에 두 개 이상의 구간이 등록 되어있고, 제거하려는 구간(section)이 노선의 하행 종점역일 때,
     * When: 구간을 제거하면
     * Then: 구간이 정상적으로 제거된다.
     */

    /**
     * Given: 지하철 노선에 한개의 구간 만 갖고 있을 때,
     * When: 구간을 제거하면
     * Then: 오류가 발생한다.
     */

    /**
     * Given: 제거하려는 구간이 노선의 마지막 구간이 아닐 때,
     * When: 구간을 제거하면
     * Then: 오류가 발생한다.
     */









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
