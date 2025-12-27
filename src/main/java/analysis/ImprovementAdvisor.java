package analysis;

import domain.AnalysisResult;
import domain.enums.ErrorCategory;

public class ImprovementAdvisor {

    public static String suggest(AnalysisResult result) {
        return result.weakestArea()
                .map(ImprovementAdvisor::messageFor)
                .orElse("Ottima partita, nessuna area critica individuata.");
    }

    private static String messageFor(ErrorCategory category) {
        return switch (category) {
            case TACTICAL ->
                    "Lavora sul calcolo tattico: puzzle giornalieri e analisi delle combinazioni.";
            case OPENING_KNOWLEDGE ->
                    "Rivedi le aperture: probabilmente esci presto dalla teoria.";
            case ENDGAME_TECHNIQUE ->
                    "Studia finali base (re e pedoni, torri).";
            case POSITIONAL ->
                    "Approfondisci concetti strategici come case deboli e colonne aperte.";
            case TIME_MANAGEMENT ->
                    "Gestisci meglio il tempo: evita lunghe riflessioni su mosse forzate.";
            case STRATEGIC ->
                    "Migliora la pianificazione a medio-lungo termine.";
        };
    }
}