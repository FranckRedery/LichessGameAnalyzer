package app;

import com.github.bhlangonijr.chesslib.Side;
import domain.GameError;
import domain.enums.ErrorCategory;
import domain.enums.ErrorSeverity;
import domain.enums.GamePhase;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class GameErrorReportGenerator {

    private final List<GameError> errors;

    public GameErrorReportGenerator(List<GameError> errors) {
        this.errors = errors;
    }

    public void generateAndOpenHtmlReport() {
        try {
            String html = generateHtmlReport();
            File htmlFile = new File("chess_analysis_report.html");

            try (FileWriter writer = new FileWriter(htmlFile)) {
                writer.write(html);
            }

            System.out.println("Report generato: " + htmlFile.getAbsolutePath());

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(htmlFile.toURI());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateHtmlReport() {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>\n");
        html.append("<html lang='it'>\n");
        html.append("<head>\n");
        html.append("    <meta charset='UTF-8'>\n");
        html.append("    <meta name='viewport' content='width=device-width, initial-scale=1.0'>\n");
        html.append("    <title>Chess Analysis Report</title>\n");
        html.append("    <script src='https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.0/chart.umd.min.js'></script>\n");
        html.append("    <style>\n");
        html.append(getStyles());
        html.append("    </style>\n");
        html.append("</head>\n");
        html.append("<body>\n");

        html.append("    <div class='container'>\n");
        html.append("        <header>\n");
        html.append("            <h1>&#9823; Chess Analysis Report</h1>\n");
        html.append("            <p class='subtitle'>Analisi dettagliata degli errori nelle partite</p>\n");
        html.append("        </header>\n");

        html.append(generateSummarySection());
        html.append(generateChartsSection());
        html.append(generateStatisticsSection());
        html.append(generateErrorsTableSection());

        html.append("    </div>\n");
        html.append("    <script>\n");
        html.append(getChartScripts());
        html.append("    </script>\n");
        html.append("</body>\n");
        html.append("</html>\n");

        return html.toString();
    }

    private String getStyles() {
        StringBuilder css = new StringBuilder();
        css.append("* { margin: 0; padding: 0; box-sizing: border-box; }\n");
        css.append("body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif; ");
        css.append("background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #333; padding: 20px; line-height: 1.6; }\n");
        css.append(".container { max-width: 1400px; margin: 0 auto; background: white; border-radius: 20px; ");
        css.append("box-shadow: 0 20px 60px rgba(0,0,0,0.3); overflow: hidden; }\n");
        css.append("header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 40px; text-align: center; }\n");
        css.append("header h1 { font-size: 2.5em; margin-bottom: 10px; font-weight: 700; }\n");
        css.append(".subtitle { font-size: 1.2em; opacity: 0.95; }\n");
        css.append(".summary { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; padding: 40px; background: #f8f9fa; }\n");
        css.append(".summary-card { background: white; padding: 25px; border-radius: 15px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); text-align: center; ");
        css.append("transition: transform 0.3s ease, box-shadow 0.3s ease; }\n");
        css.append(".summary-card:hover { transform: translateY(-5px); box-shadow: 0 8px 25px rgba(0,0,0,0.15); }\n");
        css.append(".summary-card .number { font-size: 3em; font-weight: 700; color: #667eea; margin: 10px 0; }\n");
        css.append(".summary-card .label { font-size: 1em; color: #666; text-transform: uppercase; letter-spacing: 1px; }\n");
        css.append(".charts-section { padding: 40px; }\n");
        css.append(".charts-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(450px, 1fr)); gap: 30px; margin-top: 20px; }\n");
        css.append(".chart-container { background: white; padding: 30px; border-radius: 15px; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }\n");
        css.append(".chart-container h3 { color: #667eea; margin-bottom: 20px; font-size: 1.3em; text-align: center; }\n");
        css.append(".chart-wrapper { position: relative; height: 300px; }\n");
        css.append(".stats-section { padding: 40px; background: #f8f9fa; }\n");
        css.append(".stats-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-top: 20px; }\n");
        css.append(".stat-card { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
        css.append(".stat-card h4 { color: #667eea; margin-bottom: 15px; font-size: 1.1em; }\n");
        css.append(".stat-item { display: flex; justify-content: space-between; padding: 10px 0; border-bottom: 1px solid #eee; }\n");
        css.append(".stat-item:last-child { border-bottom: none; }\n");
        css.append(".stat-label { font-weight: 500; color: #555; }\n");
        css.append(".stat-value { font-weight: 700; color: #667eea; }\n");
        css.append(".errors-table-section { padding: 40px; }\n");
        css.append(".errors-table-section h2 { color: #667eea; margin-bottom: 20px; font-size: 1.8em; }\n");
        css.append(".error-card { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; ");
        css.append("overflow: hidden; transition: transform 0.2s ease; }\n");
        css.append(".error-card:hover { transform: translateX(5px); box-shadow: 0 4px 20px rgba(0,0,0,0.15); }\n");
        css.append(".error-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px 20px; ");
        css.append("display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 10px; }\n");
        css.append(".error-body { padding: 20px; display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 15px; }\n");
        css.append(".error-info { display: flex; flex-direction: column; }\n");
        css.append(".error-info-label { font-size: 0.85em; color: #666; text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 5px; }\n");
        css.append(".error-info-value { font-weight: 600; color: #333; }\n");
        css.append(".chess-animations { grid-column: 1 / -1; display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-top: 20px; ");
        css.append("padding-top: 20px; border-top: 2px solid #eee; }\n");
        css.append(".animation-container { display: flex; flex-direction: column; align-items: center; background: #f8f9fa; ");
        css.append("padding: 15px; border-radius: 10px; }\n");
        css.append(".animation-title { font-weight: 700; color: #667eea; margin-bottom: 10px; font-size: 1.1em; text-align: center; }\n");
        css.append(".chess-board-gif { width: 100%; max-width: 400px; height: auto; border-radius: 8px; ");
        css.append("box-shadow: 0 4px 10px rgba(0,0,0,0.2); background: #fff; }\n");
        css.append(".move-notation { margin-top: 10px; font-family: 'Courier New', monospace; font-size: 0.95em; color: #555; text-align: center; }\n");
        css.append(".severity-badge { padding: 5px 15px; border-radius: 20px; font-weight: 600; font-size: 0.9em; }\n");
        css.append(".severity-BLUNDER { background: #ff4444; color: white; }\n");
        css.append(".severity-MISTAKE { background: #ff9800; color: white; }\n");
        css.append(".severity-INACCURACY { background: #ffc107; color: #333; }\n");
        css.append("section h2 { color: #667eea; margin-bottom: 20px; font-size: 1.8em; }\n");
        css.append("@media (max-width: 768px) { .charts-grid { grid-template-columns: 1fr; } ");
        css.append(".summary { grid-template-columns: 1fr; } header h1 { font-size: 2em; } ");
        css.append(".chess-animations { grid-template-columns: 1fr; } }\n");

        return css.toString();
    }

    private String generateSummarySection() {
        int totalErrors = errors.size();
        int blunders = (int) errors.stream().filter(e -> e.severity() == ErrorSeverity.BLUNDER).count();
        int mistakes = (int) errors.stream().filter(e -> e.severity() == ErrorSeverity.MISTAKE).count();
        int inaccuracies = (int) errors.stream().filter(e -> e.severity() == ErrorSeverity.INACCURACY).count();

        double avgCentipawnLoss = errors.stream()
                .mapToDouble(GameError::centipawnLoss)
                .average()
                .orElse(0.0);

        long uniqueGames = errors.stream()
                .map(GameError::gameId)
                .distinct()
                .count();

        StringBuilder html = new StringBuilder();
        html.append("        <section class='summary'>\n");
        html.append("            <div class='summary-card'><div class='number'>").append(totalErrors).append("</div><div class='label'>Errori Totali</div></div>\n");
        html.append("            <div class='summary-card'><div class='number'>").append(blunders).append("</div><div class='label'>Blunder</div></div>\n");
        html.append("            <div class='summary-card'><div class='number'>").append(mistakes).append("</div><div class='label'>Mistakes</div></div>\n");
        html.append("            <div class='summary-card'><div class='number'>").append(inaccuracies).append("</div><div class='label'>Inaccuracies</div></div>\n");
        html.append("            <div class='summary-card'><div class='number'>").append(String.format("%.0f", avgCentipawnLoss)).append("</div><div class='label'>CP Loss Medio</div></div>\n");
        html.append("            <div class='summary-card'><div class='number'>").append(uniqueGames).append("</div><div class='label'>Partite Analizzate</div></div>\n");
        html.append("        </section>\n");

        return html.toString();
    }

    private String generateChartsSection() {
        StringBuilder html = new StringBuilder();
        html.append("        <section class='charts-section'>\n");
        html.append("            <h2>&#128202; Analisi Visuale</h2>\n");
        html.append("            <div class='charts-grid'>\n");
        html.append("                <div class='chart-container'><h3>Errori per Fase di Gioco</h3><div class='chart-wrapper'><canvas id='phaseChart'></canvas></div></div>\n");
        html.append("                <div class='chart-container'><h3>Errori per Categoria</h3><div class='chart-wrapper'><canvas id='categoryChart'></canvas></div></div>\n");
        html.append("                <div class='chart-container'><h3>Distribuzione Gravita</h3><div class='chart-wrapper'><canvas id='severityChart'></canvas></div></div>\n");
        html.append("                <div class='chart-container'><h3>CP Loss per Gravita</h3><div class='chart-wrapper'><canvas id='cpLossChart'></canvas></div></div>\n");
        html.append("            </div>\n");
        html.append("        </section>\n");

        return html.toString();
    }

    private String generateStatisticsSection() {
        StringBuilder html = new StringBuilder();
        html.append("        <section class='stats-section'>\n");
        html.append("            <h2>&#128200; Statistiche Dettagliate</h2>\n");
        html.append("            <div class='stats-grid'>\n");
        html.append(generatePhaseStatistics());
        html.append(generateCategoryStatistics());
        html.append(generateColorStatistics());
        html.append("            </div>\n");
        html.append("        </section>\n");

        return html.toString();
    }

    private String generatePhaseStatistics() {
        Map<GamePhase, Long> phaseCount = errors.stream()
                .collect(Collectors.groupingBy(GameError::phase, Collectors.counting()));

        StringBuilder html = new StringBuilder();
        html.append("                <div class='stat-card'>\n");
        html.append("                    <h4>&#127919; Errori per Fase</h4>\n");

        for (GamePhase phase : GamePhase.values()) {
            long count = phaseCount.getOrDefault(phase, 0L);
            html.append("                    <div class='stat-item'>");
            html.append("<span class='stat-label'>").append(phase.name()).append("</span>");
            html.append("<span class='stat-value'>").append(count).append("</span>");
            html.append("</div>\n");
        }

        html.append("                </div>\n");
        return html.toString();
    }

    private String generateCategoryStatistics() {
        Map<ErrorCategory, Long> categoryCount = errors.stream()
                .collect(Collectors.groupingBy(GameError::category, Collectors.counting()));

        StringBuilder html = new StringBuilder();
        html.append("                <div class='stat-card'>\n");
        html.append("                    <h4>&#128269; Errori per Categoria</h4>\n");

        for (ErrorCategory category : ErrorCategory.values()) {
            long count = categoryCount.getOrDefault(category, 0L);
            if (count > 0) {
                html.append("                    <div class='stat-item'>");
                html.append("<span class='stat-label'>").append(category.name().replace("_", " ")).append("</span>");
                html.append("<span class='stat-value'>").append(count).append("</span>");
                html.append("</div>\n");
            }
        }

        html.append("                </div>\n");
        return html.toString();
    }

    private String generateColorStatistics() {
        Map<Side, Long> colorCount = errors.stream()
                .collect(Collectors.groupingBy(GameError::playerColor, Collectors.counting()));

        Map<Side, Double> avgCpLoss = errors.stream()
                .collect(Collectors.groupingBy(
                        GameError::playerColor,
                        Collectors.averagingDouble(GameError::centipawnLoss)
                ));

        StringBuilder html = new StringBuilder();
        html.append("                <div class='stat-card'>\n");
        html.append("                    <h4>&#9899;&#9898; Statistiche per Colore</h4>\n");

        for (Side side : Side.values()) {
            if (side == Side.WHITE || side == Side.BLACK) {
                long count = colorCount.getOrDefault(side, 0L);
                double avgLoss = avgCpLoss.getOrDefault(side, 0.0);

                html.append("                    <div class='stat-item'>");
                html.append("<span class='stat-label'>").append(side.name()).append(" - Errori</span>");
                html.append("<span class='stat-value'>").append(count).append("</span>");
                html.append("</div>\n");
                html.append("                    <div class='stat-item'>");
                html.append("<span class='stat-label'>").append(side.name()).append(" - CP Loss Medio</span>");
                html.append("<span class='stat-value'>").append(String.format("%.1f", avgLoss)).append("</span>");
                html.append("</div>\n");
            }
        }

        html.append("                </div>\n");
        return html.toString();
    }

    private String generateErrorsTableSection() {
        StringBuilder html = new StringBuilder();
        html.append("        <section class='errors-table-section'>\n");
        html.append("            <h2>&#128269; Dettaglio Errori</h2>\n");

        List<GameError> sortedErrors = errors.stream()
                .sorted(Comparator.comparingDouble(GameError::centipawnLoss).reversed())
                .collect(Collectors.toList());

        for (GameError error : sortedErrors) {
            html.append("            <div class='error-card'>\n");
            html.append("                <div class='error-header'>\n");
            html.append("                    <div><strong>Mossa ").append(error.moveNumber()).append("</strong> - ");
            html.append(error.playerColor().name()).append(" | ");
            html.append(error.openingName() != null ? escapeHtml(error.openingName()) : "Unknown Opening");
            html.append("</div>\n");
            html.append("                    <span class='severity-badge severity-").append(error.severity().name()).append("'>");
            html.append(error.severity().name()).append("</span>\n");
            html.append("                </div>\n");
            html.append("                <div class='error-body'>\n");

            html.append("                    <div class='error-info'><span class='error-info-label'>Fase</span>");
            html.append("<span class='error-info-value'>").append(error.phase().name()).append("</span></div>\n");

            html.append("                    <div class='error-info'><span class='error-info-label'>Categoria</span>");
            html.append("<span class='error-info-value'>").append(error.category().name().replace("_", " ")).append("</span></div>\n");

            html.append("                    <div class='error-info'><span class='error-info-label'>CP Loss</span>");
            html.append("<span class='error-info-value'>").append(String.format("%.0f", error.centipawnLoss())).append("</span></div>\n");

            html.append("                    <div class='error-info'><span class='error-info-label'>Mossa Giocata</span>");
            html.append("<span class='error-info-value'>").append(escapeHtml(error.playedMoveSan())).append("</span></div>\n");

            html.append("                    <div class='error-info'><span class='error-info-label'>Mossa Migliore</span>");
            html.append("<span class='error-info-value'>").append(escapeHtml(error.bestMoveUci())).append("</span></div>\n");

            html.append("                    <div class='error-info'><span class='error-info-label'>Eval Prima</span>");
            html.append("<span class='error-info-value'>").append(String.format("%.2f", error.evalBefore())).append("</span></div>\n");

            html.append("                    <div class='error-info'><span class='error-info-label'>Eval Dopo</span>");
            html.append("<span class='error-info-value'>").append(String.format("%.2f", error.evalAfter())).append("</span></div>\n");

            html.append("                    <div class='error-info'><span class='error-info-label'>Game ID</span>");
            html.append("<span class='error-info-value'>").append(escapeHtml(error.gameId())).append("</span></div>\n");

            html.append(generateChessAnimations(error));

            html.append("                </div>\n");
            html.append("            </div>\n");
        }

        html.append("        </section>\n");
        return html.toString();
    }

    private String generateChessAnimations(GameError error) {
        StringBuilder html = new StringBuilder();

        html.append("                    <div class='chess-animations'>\n");

        String playedMoveGif = generateLichessGifUrl(
                error.fenBefore(),
                error.playedMoveUci(),
                error.playerColor().name().toLowerCase()
        );

        html.append("                        <div class='animation-container'>\n");
        html.append("                            <div class='animation-title'>&#10060; Mossa Giocata (Errore)</div>\n");
        html.append("                            <img src='").append(playedMoveGif).append("' ");
        html.append("alt='Mossa giocata' class='chess-board-gif' loading='lazy'>\n");
        html.append("                            <div class='move-notation'>Mossa: <strong>");
        html.append(escapeHtml(error.playedMoveSan())).append("</strong> (");
        html.append(escapeHtml(error.playedMoveUci())).append(")</div>\n");
        html.append("                        </div>\n");

        String bestMoveGif = generateLichessGifUrl(
                error.fenBefore(),
                error.bestMoveUci(),
                error.playerColor().name().toLowerCase()
        );

        html.append("                        <div class='animation-container'>\n");
        html.append("                            <div class='animation-title'>&#9989; Mossa Migliore</div>\n");
        html.append("                            <img src='").append(bestMoveGif).append("' ");
        html.append("alt='Mossa migliore' class='chess-board-gif' loading='lazy'>\n");
        html.append("                            <div class='move-notation'>Mossa: <strong>");
        html.append(escapeHtml(error.bestMoveUci())).append("</strong> (CP Loss evitata: ");
        html.append(String.format("%.0f", error.centipawnLoss())).append(")</div>\n");
        html.append("                        </div>\n");

        html.append("                    </div>\n");

        return html.toString();
    }

    private String generateLichessGifUrl(String fen, String move, String orientation) {
        try {
            String encodedFen = URLEncoder.encode(fen, "UTF-8");
            return String.format(
                    "https://lichess1.org/export/fen.gif?fen=%s&lastMove=%s&orientation=%s&theme=brown&piece=cburnett",
                    encodedFen,
                    move,
                    orientation
            );
        } catch (Exception e) {
            e.printStackTrace();
            return "https://via.placeholder.com/400x400.png?text=Errore+caricamento+GIF";
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String getChartScripts() {
        Map<GamePhase, Long> phaseData = errors.stream()
                .collect(Collectors.groupingBy(GameError::phase, Collectors.counting()));

        Map<ErrorCategory, Long> categoryData = errors.stream()
                .collect(Collectors.groupingBy(GameError::category, Collectors.counting()));

        Map<ErrorSeverity, Long> severityData = errors.stream()
                .collect(Collectors.groupingBy(GameError::severity, Collectors.counting()));

        Map<ErrorSeverity, Double> cpLossData = errors.stream()
                .collect(Collectors.groupingBy(
                        GameError::severity,
                        Collectors.averagingDouble(GameError::centipawnLoss)
                ));

        StringBuilder js = new StringBuilder();
        js.append("Chart.defaults.font.family = '-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif';\n");
        js.append("Chart.defaults.plugins.legend.display = true;\n");
        js.append("Chart.defaults.plugins.legend.position = 'bottom';\n\n");

        js.append("const phaseCtx = document.getElementById('phaseChart').getContext('2d');\n");
        js.append("new Chart(phaseCtx, {\n");
        js.append("    type: 'pie',\n");
        js.append("    data: {\n");
        js.append("        labels: ").append(toJsonArray(phaseData.keySet().stream().map(Enum::name).collect(Collectors.toList()))).append(",\n");
        js.append("        datasets: [{ data: ").append(toJsonArray(phaseData.values())).append(",\n");
        js.append("            backgroundColor: ['rgba(102,126,234,0.8)','rgba(118,75,162,0.8)','rgba(255,152,0,0.8)'],\n");
        js.append("            borderWidth: 2, borderColor: '#fff' }]\n");
        js.append("    },\n");
        js.append("    options: { responsive: true, maintainAspectRatio: false,\n");
        js.append("        plugins: { legend: { position: 'bottom', labels: { padding: 15, font: { size: 12 } } } } }\n");
        js.append("});\n\n");

        js.append("const categoryCtx = document.getElementById('categoryChart').getContext('2d');\n");
        js.append("new Chart(categoryCtx, {\n");
        js.append("    type: 'doughnut',\n");
        js.append("    data: {\n");
        js.append("        labels: ").append(toJsonArray(categoryData.keySet().stream().map(c -> c.name().replace("_"," ")).collect(Collectors.toList()))).append(",\n");
        js.append("        datasets: [{ data: ").append(toJsonArray(categoryData.values())).append(",\n");
        js.append("            backgroundColor: ['rgba(255,99,132,0.8)','rgba(54,162,235,0.8)','rgba(255,206,86,0.8)',\n");
        js.append("                'rgba(75,192,192,0.8)','rgba(153,102,255,0.8)','rgba(255,159,64,0.8)','rgba(199,199,199,0.8)'],\n");
        js.append("            borderWidth: 2, borderColor: '#fff' }]\n");
        js.append("    },\n");
        js.append("    options: { responsive: true, maintainAspectRatio: false,\n");
        js.append("        plugins: { legend: { position: 'bottom', labels: { padding: 10, font: { size: 11 } } } } }\n");
        js.append("});\n\n");

        js.append("const severityCtx = document.getElementById('severityChart').getContext('2d');\n");
        js.append("new Chart(severityCtx, {\n");
        js.append("    type: 'bar',\n");
        js.append("    data: {\n");
        js.append("        labels: ").append(toJsonArray(severityData.keySet().stream().map(Enum::name).collect(Collectors.toList()))).append(",\n");
        js.append("        datasets: [{ label: 'Numero di Errori', data: ").append(toJsonArray(severityData.values())).append(",\n");
        js.append("            backgroundColor: ['rgba(255,68,68,0.8)','rgba(255,152,0,0.8)','rgba(255,193,7,0.8)'],\n");
        js.append("            borderWidth: 2,\n");
        js.append("            borderColor: ['rgb(255,68,68)','rgb(255,152,0)','rgb(255,193,7)'] }]\n");
        js.append("    },\n");
        js.append("    options: { responsive: true, maintainAspectRatio: false,\n");
        js.append("        scales: { y: { beginAtZero: true, ticks: { stepSize: 1 } } },\n");
        js.append("        plugins: { legend: { display: false } } }\n");
        js.append("});\n\n");

        js.append("const cpLossCtx = document.getElementById('cpLossChart').getContext('2d');\n");
        js.append("new Chart(cpLossCtx, {\n");
        js.append("    type: 'bar',\n");
        js.append("    data: {\n");
        js.append("        labels: ").append(toJsonArray(cpLossData.keySet().stream().map(Enum::name).collect(Collectors.toList()))).append(",\n");
        js.append("        datasets: [{ label: 'CP Loss Medio', data: ").append(toJsonArray(cpLossData.values())).append(",\n");
        js.append("            backgroundColor: 'rgba(102,126,234,0.8)',\n");
        js.append("            borderColor: 'rgb(102,126,234)', borderWidth: 2 }]\n");
        js.append("    },\n");
        js.append("    options: { responsive: true, maintainAspectRatio: false,\n");
        js.append("        scales: { y: { beginAtZero: true, title: { display: true, text: 'Centipawn Loss' } } },\n");
        js.append("        plugins: { legend: { display: false } } }\n");
        js.append("});\n");

        return js.toString();
    }

    private String toJsonArray(Collection<?> collection) {
        return "[" + collection.stream()
                .map(item -> {
                    if (item instanceof String) {
                        return "'" + item + "'";
                    }
                    return String.valueOf(item);
                })
                .collect(Collectors.joining(",")) + "]";
    }
}