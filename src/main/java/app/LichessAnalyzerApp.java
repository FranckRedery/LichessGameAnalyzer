package app;

import fetch.LichessFetcher;

public class LichessAnalyzerApp {

    static void main() {
        LichessFetcher fetcher = new LichessFetcher();
        try {
            String games = fetcher.fetchGamesByUser("FranckReda96");
            System.out.println(games);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}