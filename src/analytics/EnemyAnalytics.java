package analytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Enemy;
import entity.EntityType;

public class EnemyAnalytics {
    private final Map<Integer, EnemyStats> statsById = new HashMap<>();
    private final Map<Integer, List<EnemyStats>> waves = new HashMap<>(); 
    private int nextId = 1;

    public int registerSpawn(Enemy enemy, int waveIndex) {
        int id = nextId++;
        EnemyStats es = new EnemyStats(id, enemy.getType(), System.currentTimeMillis());
        statsById.put(id, es);
        waves.computeIfAbsent(waveIndex, k -> new ArrayList<>()).add(es);
        enemy.setAnalyticsId(id); 
        return id;
    }


    public void registerDamageTaken(int analyticsId, int amount) {
        EnemyStats es = statsById.get(analyticsId);
        if (es != null) es.damageTaken += amount;
    }

    public void registerDamageDealt(int analyticsId, int amount) {
        EnemyStats es = statsById.get(analyticsId);
        if (es != null) es.damageDealt += amount;
    }

    public void registerDeath(int analyticsId) {
        EnemyStats es = statsById.get(analyticsId);
        if (es != null) es.deathTimeMillis = System.currentTimeMillis();
    }

    public void printWaveReport(int waveIndex) {
        List<EnemyStats> list = waves.getOrDefault(waveIndex, Collections.emptyList());
        if (list.isEmpty()) {
            System.out.println("Wave " + waveIndex + " - no data");
            return;
        }
        long sumDur = 0;
        Map<EntityType, Integer> countByType = new HashMap<>();
        Map<EntityType, Long> sumDurByType = new HashMap<>();
        for (EnemyStats es : list) {
            long dur = es.getAliveDurationMillis();
            sumDur += dur;
            countByType.merge(es.type, 1, Integer::sum);
            sumDurByType.merge(es.type, dur, Long::sum);
        }
        System.out.println("Wave " + waveIndex + " report:");
        System.out.printf("  Enemies: %d\n", list.size());
        System.out.printf("  Avg life (s): %.2f\n", sumDur / 1000.0 / list.size());
        for (EntityType t : countByType.keySet()) {
            int c = countByType.get(t);
            double avg = sumDurByType.get(t) / 1000.0 / c;
            System.out.printf("    %s: count=%d avgLife(s)=%.2f\n", t, c, avg);
        }
    }


    public String exportCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("id,type,spawnMillis,deathMillis,aliveMillis,damageTaken,damageDealt\n");
        for (EnemyStats es : statsById.values()) {
            sb.append(String.format("%d,%s,%d,%s,%d,%d,%d\n",
                    es.id,
                    es.type,
                    es.spawnTimeMillis,
                    (es.deathTimeMillis==null ? "" : es.deathTimeMillis.toString()),
                    es.getAliveDurationMillis(),
                    es.damageTaken,
                    es.damageDealt));
        }
        return sb.toString();
    }
    
    public void printAverageLifeByType() {
        if (statsById.isEmpty()) {
            System.out.println("No enemies spawned yet.");
            return;
        }

        Map<EntityType, Integer> countByType = new HashMap<>();
        Map<EntityType, Long> sumLifeByType = new HashMap<>();

        for (EnemyStats es : statsById.values()) {
            long aliveTime = es.getAliveDurationMillis();
            countByType.merge(es.type, 1, Integer::sum);
            sumLifeByType.merge(es.type, aliveTime, Long::sum);
        }

        System.out.println("Average life per enemy type:");
        for (EntityType type : countByType.keySet()) {
            int count = countByType.get(type);
            double avgSeconds = sumLifeByType.get(type) / 1000.0 / count;
            System.out.printf("  %s: count=%d avgLife=%.2f s%n", type, count, avgSeconds);
        }
    }



}

