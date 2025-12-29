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
        html.append("            <h1>♟️ Chess Analysis Report</h1>\n");
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
        return """
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            body {
                font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: #333;
                padding: 20px;
                line-height: 1.6;
            }
            
            .container {
                max-width: 1400px;
                margin: 0 auto;
                background: white;
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0,0,0,0.3);
                overflow: hidden;
            }
            
            header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 40px;
                text-align: center;
            }
            
            header h1 {
                font-size: 2.5em;
                margin-bottom: 10px;
                font-weight: 700;
            }
            
            .subtitle {
                font-size: 1.2em;
                opacity: 0.95;
            }
            
            .summary {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 20px;
                padding: 40px;
                background: #f8f9fa;
            }
            
            .summary-card {
                background: white;
                padding: 25px;
                border-radius: 15px;
                box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                text-align: center;
                transition: transform 0.3s ease, box-shadow 0.3s ease;
            }
            
            .summary-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 8px 25px rgba(0,0,0,0.15);
            }
            
            .summary-card .number {
                font-size: 3em;
                font-weight: 700;
                color: #667eea;
                margin: 10px 0;
            }
            
            .summary-card .label {
                font-size: 1em;
                color: #666;
                text-transform: uppercase;
                letter-spacing: 1px;
            }
            
            .charts-section {
                padding: 40px;
            }
            
            .charts-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(450px, 1fr));
                gap: 30px;
                margin-top: 20px;
            }
            
            .chart-container {
                background: white;
                padding: 30px;
                border-radius: 15px;
                box-shadow: 0 4px 15px rgba(0,0,0,0.1);
            }
            
            .chart-container h3 {
                color: #667eea;
                margin-bottom: 20px;
                font-size: 1.3em;
                text-align: center;
            }
            
            .chart-wrapper {
                position: relative;
                height: 300px;
            }
            
            .stats-section {
                padding: 40px;
                background: #f8f9fa;
            }
            
            .stats-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                gap: 20px;
                margin-top: 20px;
            }
            
            .stat-card {
                background: white;
                padding: 20px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }
            
            .stat-card h4 {
                color: #667eea;
                margin-bottom: 15px;
                font-size: 1.1em;
            }
            
            .stat-item {
                display: flex;
                justify-content: space-between;
                padding: 10px 0;
                border-bottom: 1px solid #eee;
            }
            
            .stat-item:last-child {
                border-bottom: none;
            }
            
            .stat-label {
                font-weight: 500;
                color: #555;
            }
            
            .stat-value {
                font-weight: 700;
                color: #667eea;
            }
            
            .errors-table-section {
                padding: 40px;
            }
            
            .errors-table-section h2 {
                color: #667eea;
                margin-bottom: 20px;
                font-size: 1.8em;
            }
            
            .error-card {
                background: white;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin-bottom: 20px;
                overflow: hidden;
                transition: transform 0.2s ease;
            }
            
            .error-card:hover {
                transform: translateX(5px);
                box-shadow: 0 4px 20px rgba(0,0,0,0.15);
            }
            
            .error-header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 15px 20px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 10px;
            }
            
            .error-body {
                padding: 20px;
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 15px;
            }
            
            .error-info {
                display: flex;
                flex-direction: column;
            }
            
            .error-info-label {
                font-size: 0.85em;
                color: #666;
                text-transform: uppercase;
                letter-spacing: 0.5px;
                margin-bottom: 5px;
            }
            
            .error-info-value {
                font-weight: 600;
                color: #333;
            }
            
            .severity-badge {
                padding: 5px 15px;
                border-radius: 20px;
                font-weight: 600;
                font-size: 0.9em;
            }
            
            .severity-BLUNDER {
                background: #ff4444;
                color: white;
            }
            
            .severity-MISTAKE {
                background: #ff9800;
                color: white;
            }
            
            .severity-INACCURACY {
                background: #ffc107;
                color: #333;
            }
            
            section h2 {
                color: #667eea;
                margin-bottom: 20px;
                font-size: 1.8em;
            }
            
            @media (max-width: 768px) {
                .charts-grid {
                    grid-template-columns: 1fr;
                }
                
                .summary {
                    grid-template-columns: 1fr;
                }
                
                header h1 {
                    font-size: 2em;
                }
            }
        """;
    }

    private String generateSummarySection() {
        int totalErrors = errors.size();
        int blunders = (int) errors.stream().filter(e -> e.getSeverity() == ErrorSeverity.BLUNDER).count();
        int mistakes = (int) errors.stream().filter(e -> e.getSeverity() == ErrorSeverity.MISTAKE).count();
        int inaccuracies = (int) errors.stream().filter(e -> e.getSeverity() == ErrorSeverity.INACCURACY).count();

        double avgCentipawnLoss = errors.stream()
                .mapToDouble(GameError::getCentipawnLoss)
                .average()
                .orElse(0.0);

        long uniqueGames = errors.stream()
                .map(GameError::getGameId)
                .distinct()
                .count();

        StringBuilder html = new StringBuilder();
        html.append("        <section class='summary'>\n");
        html.append("            <div class='summary-card'>\n");
        html.append("                <div class='number'>").append(totalErrors).append("</div>\n");
        html.append("                <div class='label'>Errori Totali</div>\n");
        html.append("            </div>\n");
        html.append("            <div class='summary-card'>\n");
        html.append("                <div class='number'>").append(blunders).append("</div>\n");
        html.append("                <div class='label'>Blunder</div>\n");
        html.append("            </div>\n");
        html.append("            <div class='summary-card'>\n");
        html.append("                <div class='number'>").append(mistakes).append("</div>\n");
        html.append("                <div class='label'>Mistakes</div>\n");
        html.append("            </div>\n");
        html.append("            <div class='summary-card'>\n");
        html.append("                <div class='number'>").append(inaccuracies).append("</div>\n");
        html.append("                <div class='label'>Inaccuracies</div>\n");
        html.append("            </div>\n");
        html.append("            <div class='summary-card'>\n");
        html.append("                <div class='number'>").append(String.format("%.0f", avgCentipawnLoss)).append("</div>\n");
        html.append("                <div class='label'>CP Loss Medio</div>\n");
        html.append("            </div>\n");
        html.append("            <div class='summary-card'>\n");
        html.append("                <div class='number'>").append(uniqueGames).append("</div>\n");
        html.append("                <div class='label'>Partite Analizzate</div>\n");
        html.append("            </div>\n");
        html.append("        </section>\n");

        return html.toString();
    }

    private String generateChartsSection() {
        StringBuilder html = new StringBuilder();
        html.append("        <section class='charts-section'>\n");
        html.append("            <h2>📊 Analisi Visuale</h2>\n");
        html.append("            <div class='charts-grid'>\n");

        html.append("                <div class='chart-container'>\n");
        html.append("                    <h3>Errori per Fase di Gioco</h3>\n");
        html.append("                    <div class='chart-wrapper'>\n");
        html.append("                        <canvas id='phaseChart'></canvas>\n");
        html.append("                    </div>\n");
        html.append("                </div>\n");

        html.append("                <div class='chart-container'>\n");
        html.append("                    <h3>Errori per Categoria</h3>\n");
        html.append("                    <div class='chart-wrapper'>\n");
        html.append("                        <canvas id='categoryChart'></canvas>\n");
        html.append("                    </div>\n");
        html.append("                </div>\n");

        html.append("                <div class='chart-container'>\n");
        html.append("                    <h3>Distribuzione Gravità</h3>\n");
        html.append("                    <div class='chart-wrapper'>\n");
        html.append("                        <canvas id='severityChart'></canvas>\n");
        html.append("                    </div>\n");
        html.append("                </div>\n");

        html.append("                <div class='chart-container'>\n");
        html.append("                    <h3>CP Loss per Gravità</h3>\n");
        html.append("                    <div class='chart-wrapper'>\n");
        html.append("                        <canvas id='cpLossChart'></canvas>\n");
        html.append("                    </div>\n");
        html.append("                </div>\n");

        html.append("            </div>\n");
        html.append("        </section>\n");

        return html.toString();
    }

    private String generateStatisticsSection() {
        StringBuilder html = new StringBuilder();
        html.append("        <section class='stats-section'>\n");
        html.append("            <h2>📈 Statistiche Dettagliate</h2>\n");
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
                .collect(Collectors.groupingBy(GameError::getPhase, Collectors.counting()));

        StringBuilder html = new StringBuilder();
        html.append("                <div class='stat-card'>\n");
        html.append("                    <h4>🎯 Errori per Fase</h4>\n");

        for (GamePhase phase : GamePhase.values()) {
            long count = phaseCount.getOrDefault(phase, 0L);
            html.append("                    <div class='stat-item'>\n");
            html.append("                        <span class='stat-label'>").append(phase.name()).append("</span>\n");
            html.append("                        <span class='stat-value'>").append(count).append("</span>\n");
            html.append("                    </div>\n");
        }

        html.append("                </div>\n");
        return html.toString();
    }

    private String generateCategoryStatistics() {
        Map<ErrorCategory, Long> categoryCount = errors.stream()
                .collect(Collectors.groupingBy(GameError::getCategory, Collectors.counting()));

        StringBuilder html = new StringBuilder();
        html.append("                <div class='stat-card'>\n");
        html.append("                    <h4>🔍 Errori per Categoria</h4>\n");

        for (ErrorCategory category : ErrorCategory.values()) {
            long count = categoryCount.getOrDefault(category, 0L);
            if (count > 0) {
                html.append("                    <div class='stat-item'>\n");
                html.append("                        <span class='stat-label'>").append(category.name().replace("_", " ")).append("</span>\n");
                html.append("                        <span class='stat-value'>").append(count).append("</span>\n");
                html.append("                    </div>\n");
            }
        }

        html.append("                </div>\n");
        return html.toString();
    }

    private String generateColorStatistics() {
        Map<Side, Long> colorCount = errors.stream()
                .collect(Collectors.groupingBy(GameError::getPlayerColor, Collectors.counting()));

        Map<Side, Double> avgCpLoss = errors.stream()
                .collect(Collectors.groupingBy(
                        GameError::getPlayerColor,
                        Collectors.averagingDouble(GameError::getCentipawnLoss)
                ));

        StringBuilder html = new StringBuilder();
        html.append("                <div class='stat-card'>\n");
        html.append("                    <h4>⚫⚪ Statistiche per Colore</h4>\n");

        for (Side side : Side.values()) {
            if (side == Side.WHITE || side == Side.BLACK) {
                long count = colorCount.getOrDefault(side, 0L);
                double avgLoss = avgCpLoss.getOrDefault(side, 0.0);

                html.append("                    <div class='stat-item'>\n");
                html.append("                        <span class='stat-label'>").append(side.name()).append(" - Errori</span>\n");
                html.append("                        <span class='stat-value'>").append(count).append("</span>\n");
                html.append("                    </div>\n");
                html.append("                    <div class='stat-item'>\n");
                html.append("                        <span class='stat-label'>").append(side.name()).append(" - CP Loss Medio</span>\n");
                html.append("                        <span class='stat-value'>").append(String.format("%.1f", avgLoss)).append("</span>\n");
                html.append("                    </div>\n");
            }
        }

        html.append("                </div>\n");
        return html.toString();
    }

    private String generateErrorsTableSection() {
        StringBuilder html = new StringBuilder();
        html.append("        <section class='errors-table-section'>\n");
        html.append("            <h2>🔍 Dettaglio Errori</h2>\n");

        List<GameError> sortedErrors = errors.stream()
                .sorted(Comparator.comparingDouble(GameError::getCentipawnLoss).reversed())
                .collect(Collectors.toList());

        for (GameError error : sortedErrors) {
            html.append("            <div class='error-card'>\n");
            html.append("                <div class='error-header'>\n");
            html.append("                    <div>\n");
            html.append("                        <strong>Mossa ").append(error.getMoveNumber()).append("</strong> - ");
            html.append(error.getPlayerColor().name()).append(" | ");
            html.append(error.getOpeningName() != null ? error.getOpeningName() : "Unknown Opening");
            html.append("                    </div>\n");
            html.append("                    <span class='severity-badge severity-").append(error.getSeverity().name()).append("'>");
            html.append(error.getSeverity().name()).append("</span>\n");
            html.append("                </div>\n");
            html.append("                <div class='error-body'>\n");

            html.append("                    <div class='error-info'>\n");
            html.append("                        <span class='error-info-label'>Fase</span>\n");
            html.append("                        <span class='error-info-value'>").append(error.getPhase().name()).append("</span>\n");
            html.append("                    </div>\n");

            html.append("                    <div class='error-info'>\n");
            html.append("                        <span class='error-info-label'>Categoria</span>\n");
            html.append("                        <span class='error-info-value'>").append(error.getCategory().name().replace("_", " ")).append("</span>\n");
            html.append("                    </div>\n");

            html.append("                    <div class='error-info'>\n");
            html.append("                        <span class='error-info-label'>CP Loss</span>\n");
            html.append("                        <span class='error-info-value'>").append(String.format("%.0f", error.getCentipawnLoss())).append("</span>\n");
            html.append("                    </div>\n");

            html.append("                    <div class='error-info'>\n");
            html.append("                        <span class='error-info-label'>Mossa Giocata</span>\n");
            html.append("                        <span class='error-info-value'>").append(error.getPlayedMoveSan()).append("</span>\n");
            html.append("                    </div>\n");

            html.append("                    <div class='error-info'>\n");
            html.append("                        <span class='error-info-label'>Mossa Migliore</span>\n");
            html.append("                        <span class='error-info-value'>").append(error.getBestMoveUci()).append("</span>\n");
            html.append("                    </div>\n");

            html.append("                    <div class='error-info'>\n");
            html.append("                        <span class='error-info-label'>Eval Prima</span>\n");
            html.append("                        <span class='error-info-value'>").append(String.format("%.2f", error.getEvalBefore())).append("</span>\n");
            html.append("                    </div>\n");

            html.append("                    <div class='error-info'>\n");
            html.append("                        <span class='error-info-label'>Eval Dopo</span>\n");
            html.append("                        <span class='error-info-value'>").append(String.format("%.2f", error.getEvalAfter())).append("</span>\n");
            html.append("                    </div>\n");

            html.append("                    <div class='error-info'>\n");
            html.append("                        <span class='error-info-label'>Game ID</span>\n");
            html.append("                        <span class='error-info-value'>").append(error.getGameId()).append("</span>\n");
            html.append("                    </div>\n");

            html.append("                </div>\n");
            html.append("            </div>\n");
        }

        html.append("        </section>\n");
        return html.toString();
    }

    private String getChartScripts() {
        Map<GamePhase, Long> phaseData = errors.stream()
                .collect(Collectors.groupingBy(GameError::getPhase, Collectors.counting()));

        Map<ErrorCategory, Long> categoryData = errors.stream()
                .collect(Collectors.groupingBy(GameError::getCategory, Collectors.counting()));

        Map<ErrorSeverity, Long> severityData = errors.stream()
                .collect(Collectors.groupingBy(GameError::getSeverity, Collectors.counting()));

        Map<ErrorSeverity, Double> cpLossData = errors.stream()
                .collect(Collectors.groupingBy(
                        GameError::getSeverity,
                        Collectors.averagingDouble(GameError::getCentipawnLoss)
                ));

        return String.format("""
            Chart.defaults.font.family = '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif';
            Chart.defaults.plugins.legend.display = true;
            Chart.defaults.plugins.legend.position = 'bottom';
            
            const phaseCtx = document.getElementById('phaseChart').getContext('2d');
            new Chart(phaseCtx, {
                type: 'pie',
                data: {
                    labels: %s,
                    datasets: [{
                        data: %s,
                        backgroundColor: [
                            'rgba(102, 126, 234, 0.8)',
                            'rgba(118, 75, 162, 0.8)',
                            'rgba(255, 152, 0, 0.8)'
                        ],
                        borderWidth: 2,
                        borderColor: '#fff'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'bottom',
                            labels: { padding: 15, font: { size: 12 } }
                        }
                    }
                }
            });
            
            const categoryCtx = document.getElementById('categoryChart').getContext('2d');
            new Chart(categoryCtx, {
                type: 'doughnut',
                data: {
                    labels: %s,
                    datasets: [{
                        data: %s,
                        backgroundColor: [
                            'rgba(255, 99, 132, 0.8)',
                            'rgba(54, 162, 235, 0.8)',
                            'rgba(255, 206, 86, 0.8)',
                            'rgba(75, 192, 192, 0.8)',
                            'rgba(153, 102, 255, 0.8)',
                            'rgba(255, 159, 64, 0.8)',
                            'rgba(199, 199, 199, 0.8)'
                        ],
                        borderWidth: 2,
                        borderColor: '#fff'
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'bottom',
                            labels: { padding: 10, font: { size: 11 } }
                        }
                    }
                }
            });
            
            const severityCtx = document.getElementById('severityChart').getContext('2d');
            new Chart(severityCtx, {
                type: 'bar',
                data: {
                    labels: %s,
                    datasets: [{
                        label: 'Numero di Errori',
                        data: %s,
                        backgroundColor: [
                            'rgba(255, 68, 68, 0.8)',
                            'rgba(255, 152, 0, 0.8)',
                            'rgba(255, 193, 7, 0.8)'
                        ],
                        borderWidth: 2,
                        borderColor: [
                            'rgb(255, 68, 68)',
                            'rgb(255, 152, 0)',
                            'rgb(255, 193, 7)'
                        ]
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { stepSize: 1 }
                        }
                    },
                    plugins: {
                        legend: { display: false }
                    }
                }
            });
            
            const cpLossCtx = document.getElementById('cpLossChart').getContext('2d');
            new Chart(cpLossCtx, {
                type: 'bar',
                data: {
                    labels: %s,
                    datasets: [{
                        label: 'CP Loss Medio',
                        data: %s,
                        backgroundColor: 'rgba(102, 126, 234, 0.8)',
                        borderColor: 'rgb(102, 126, 234)',
                        borderWidth: 2
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        y: {
                            beginAtZero: true,
                            title: {
                                display: true,
                                text: 'Centipawn Loss'
                            }
                        }
                    },
                    plugins: {
                        legend: { display: false }
                    }
                }
            });
        """,
                toJsonArray(phaseData.keySet().stream().map(Enum::name).collect(Collectors.toList())),
                toJsonArray(phaseData.values()),
                toJsonArray(categoryData.keySet().stream().map(c -> c.name().replace("_", " ")).collect(Collectors.toList())),
                toJsonArray(categoryData.values()),
                toJsonArray(severityData.keySet().stream().map(Enum::name).collect(Collectors.toList())),
                toJsonArray(severityData.values()),
                toJsonArray(cpLossData.keySet().stream().map(Enum::name).collect(Collectors.toList())),
                toJsonArray(cpLossData.values())
        );
    }

    private String toJsonArray(Collection<?> collection) {
        return "[" + collection.stream()
                .map(item -> {
                    if (item instanceof String) {
                        return "'" + item + "'";
                    }
                    return String.valueOf(item);
                })
    .collect(Collectors.joining(", ")) + "]";}

}
