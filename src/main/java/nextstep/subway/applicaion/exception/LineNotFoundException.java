package nextstep.subway.applicaion.exception;

public class LineNotFoundException extends NotFoundException {

    private static final String TYPE = "LINE";

    public LineNotFoundException(Long id) {
        super(TYPE, id);
    }
}
