package subway.fixture;

import subway.line.SectionRequest;

public class SectionRequestFixture {
	public static SectionRequest.Builder builder() {
		return SectionRequest.builder()
			.downStationId(1L)
			.upStationId(2L)
			.distance(10);
	}
}
