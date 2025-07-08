// LeaderboardEntry.java
import java.util.Objects; // Import Objects for better equals and hashCode

public class LeaderboardEntry implements Comparable<LeaderboardEntry> {
    private String name;
    private int score;

    public LeaderboardEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(LeaderboardEntry other) {
        // Urutkan dalam urutan menurun berdasarkan skor
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return name + ": " + score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaderboardEntry that = (LeaderboardEntry) o;
        return score == that.score && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }
}