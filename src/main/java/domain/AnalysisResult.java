package domain;

import domain.enums.ErrorCategory;
import domain.enums.ErrorSeverity;
import domain.enums.GamePhase;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnalysisResult {

    private final List<GameError> errors;

    public AnalysisResult(List<GameError> errors) {
        this.errors = List.copyOf(errors);
    }

    public List<GameError> getErrors() {
        return errors;
    }

    // ------------------
    // DERIVED INSIGHTS
    // ------------------

    public long countBySeverity(ErrorSeverity severity) {
        return errors.stream()
                .filter(e -> e.getSeverity() == severity)
                .count();
    }

    public Map<ErrorCategory, Long> errorsByCategory() {
        return errors.stream()
                .collect(Collectors.groupingBy(
                        GameError::getCategory,
                        Collectors.counting()
                ));
    }

    public Map<GamePhase, Long> errorsByPhase() {
        return errors.stream()
                .collect(Collectors.groupingBy(
                        GameError::getPhase,
                        Collectors.counting()
                ));
    }

    public Optional<ErrorCategory> weakestArea() {
        return errorsByCategory().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey);
    }
}
