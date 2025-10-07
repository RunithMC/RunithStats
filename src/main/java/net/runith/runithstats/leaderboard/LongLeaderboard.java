
package net.runith.runithstats.leaderboard;

import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.Comparator;
import java.util.List;

public final class LongLeaderboard {
    private final Object2LongOpenHashMap<String> scores = new Object2LongOpenHashMap<>();
    private List<Object2LongOpenHashMap.Entry<String>> cachedSorted = new ObjectArrayList<>();
    private boolean dirty = true;

    public void setScore(String player, long score) {
        scores.put(player, score);
        dirty = true;
    }

    public void addScore(String player, long score) {
        if (score != 0) {
            scores.addTo(player, score);
            dirty = true;
        }
    }

    public long getScore(String player) {
        return scores.getOrDefault(player, 0L);
    }

    public void remove(String player) {
        if (scores.removeLong(player) != 0) {
            dirty = true;
        }
    }

    public List<Object2LongOpenHashMap.Entry<String>> getTopPlayers(int limit) {
        if (dirty) {
            cachedSorted = new ObjectArrayList<>(scores.object2LongEntrySet());
            cachedSorted.sort(Comparator
                .comparingLong(Object2LongOpenHashMap.Entry<String>::getLongValue)
                .reversed()
                .thenComparing(Object2LongOpenHashMap.Entry::getKey));
            dirty = false;
        }
        return cachedSorted.subList(0, Math.min(limit, cachedSorted.size()));
    }
}
