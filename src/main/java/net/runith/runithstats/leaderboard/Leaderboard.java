package net.runith.runithstats.leaderboard;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Comparator;
import java.util.List;

public final class Leaderboard {
    private final Object2IntOpenHashMap<String> scores = new Object2IntOpenHashMap<>();
    private List<Object2IntMap.Entry<String>> cachedSorted = new ObjectArrayList<>();
    private boolean dirty = true;

    public void setScore(String player, int score) {
        scores.put(player, score);
        dirty = true;
    }

    public void addScore(String player, int points) {
        if (points != 0) {
            scores.addTo(player, points);
            dirty = true;
        }
    }

    public int getScore(String player) {
        return scores.getOrDefault(player, 0);
    }

    public void remove(String player) {
        if (scores.removeInt(player) != 0) {
            dirty = true;
        }
    }

    public List<Object2IntMap.Entry<String>> getTopPlayers(int limit) {
        if (dirty) {
            cachedSorted = new ObjectArrayList<>(scores.object2IntEntrySet());
            cachedSorted.sort(Comparator
                .comparingInt(Object2IntMap.Entry<String>::getIntValue)
                .reversed()
                .thenComparing(Object2IntMap.Entry::getKey));
            dirty = false;
        }
        return cachedSorted.subList(0, Math.min(limit, cachedSorted.size()));
    }
}
