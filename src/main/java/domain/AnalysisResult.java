package domain;

import java.util.List;

public record AnalysisResult(List<GameError> errors) {

    public AnalysisResult(List<GameError> errors) {
        this.errors = List.copyOf(errors);
    }

}
