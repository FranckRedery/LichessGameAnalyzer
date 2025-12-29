package domain;

import java.util.List;

public class OpeningExplorerResponse {
    public List<MoveStat> moves;

    public static class MoveStat {
        public String uci;
        public int white;
        public int black;
        public int draws;
    }


}
