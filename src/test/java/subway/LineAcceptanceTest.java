package subway;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
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
import subway.line.Line;
import subway.line.LineRequest;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class LineAcceptanceTest {

    @BeforeEach
    void init() {
        createStation("station1");
        createStation("station2");
        createStation("station3");
        createStation("station4");
    }

    /**
     * Given: 새로운 지하철 노선 정보를 입력하고,
     * When: 관리자가 노선을 생성하면,
     * Then: 해당 노선이 생성되고 노선 목록에 포함된다.
     */
    @DisplayName("지하철 노선을 생성한다")
    @Test
    void testCreateLine() {
        // Given
        String newLineName = "Line1";
        // When
        ExtractableResponse<Response> response = createLine(new LineRequest(newLineName, "red", 1L, 2L, 10L));
        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> lineInfo = getAllLines();
        List<String> nameList = lineInfo.body().jsonPath().getList("name", String.class);
        assertThat(nameList).contains(newLineName);
    }

    /**
     * Given: 여러 개의 지하철 노선이 등록되어 있고,
     * When: 관리자가 지하철 노선 목록을 조회하면,
     * Then: 모든 지하철 노선 목록이 반환된다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void testGetAllLine() {
        // Given
        String lineName1 = "Line1";
        String lineName2 = "Line2";
        createLine(new LineRequest(lineName1, "red", 1L, 2L, 10L));
        createLine(new LineRequest(lineName2, "blue", 3L, 4L, 10L));
        // When
        ExtractableResponse<Response> lineInfo = getAllLines();
        // Then
        List<String> lineNames = lineInfo.body().jsonPath().getList("name", String.class);
        assertThat(lineNames).containsExactly(lineName1, lineName2);
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 조회하면,
     * Then: 해당 노선의 정보가 반환된다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void testGetLine() {
        // Given
        String lineName1 = "Line1";
        ExtractableResponse<Response> response = createLine(new LineRequest(lineName1, "red", 1L, 2L, 10L));
        // When
        String url = response.response().getHeader("Location");
        ExtractableResponse<Response> lineInfo = getLine(url);
        // Then
        String lineName = lineInfo.body().jsonPath().get("name");
        assertThat(lineName).isEqualTo(lineName1);
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 수정하면,
     * Then: 해당 노선의 정보가 수정된다.
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void testUpdateLine() {
        // Given
        String previousLineName = "Line1";
        String newLineName = "Line2";
        ExtractableResponse<Response> createLineResponse = createLine(new LineRequest(previousLineName, "red", 1L, 2L, 10L));
        // When
        Map<String, String> updateLineInfoParam = Map.of("name", newLineName, "color", "red");
        updateLine(updateLineInfoParam, 1L);
        // Then
        ExtractableResponse<Response> lineInfo = getLine(createLineResponse.response().getHeader("Location"));
        String lineName = lineInfo.body().jsonPath().get("name");
        assertThat(lineName).isEqualTo(newLineName);
    }

    /**
     * Given: 특정 지하철 노선이 등록되어 있고,
     * When: 관리자가 해당 노선을 삭제하면,
     * Then: 해당 노선이 삭제되고 노선 목록에서 제외된다.
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void testRemoveLine() {
        // Given
        String lineName1 = "Line1";
        ExtractableResponse<Response> createLineResponse = createLine(new LineRequest(lineName1, "red", 1L, 2L, 10L));
        // When
        String url = createLineResponse.response().getHeader("Location");
        deleteStation(url);
        // Then
        ExtractableResponse<Response> allLines = getAllLines();
        List<String> allLineNames = allLines.body().jsonPath().getList("name", String.class);
        assertThat(allLineNames).doesNotContain(lineName1);
    }

    void createStation(String stationName) {
        Map<String, String> station = Map.of("name", stationName);
        RestAssured.given().log().all()
                   .body(station)
                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                   .when().post("/stations")
                   .then().log().all()
                   .extract();
    }

    ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                          .body(lineRequest)
                          .contentType(MediaType.APPLICATION_JSON_VALUE)
                          .when().post("/lines")
                          .then().log().all()
                          .extract();
    }

    ExtractableResponse<Response> getAllLines() {
        return RestAssured.given().log().all()
                          .when().get("/lines")
                          .then().log().all()
                          .extract();
    }

    ExtractableResponse<Response> getLine(String url) {
        return RestAssured.given().log().all()
                          .when().get(url)
                          .then().log().all()
                          .extract();
    }

    void updateLine(Map<String, String> lineRequest, Long lineIdToUpdate) {
        RestAssured.given().log().all()
                   .body(lineRequest)
                   .contentType(MediaType.APPLICATION_JSON_VALUE)
                   .when().put("/lines/" + lineIdToUpdate.toString())
                   .then().log().all()
                   .extract();
    }

    void deleteStation(String url) {
        RestAssured.given().log().all()
                   .when().delete(url)
                   .then().log().all()
                   .extract();
    }
}
