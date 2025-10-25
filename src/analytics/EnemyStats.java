package analytics;

import java.util.ArrayList;
import java.util.List;

import player.EntityType;

public class EnemyStats {
	public final int id;
    public final EntityType type;
    public final long spawnTimeMillis;
    public Long deathTimeMillis = null;      
    public int damageTaken = 0;
    public int damageDealt = 0;
    public List<Long> positionTimestamps = new ArrayList<>(); 
    
    public EnemyStats(int id, EntityType type, long spawnTimeMillis) {
        this.id = id;
        this.type = type;
        this.spawnTimeMillis = spawnTimeMillis;
    }

    public long getAliveDurationMillis() {
        return (deathTimeMillis == null) ? (System.currentTimeMillis() - spawnTimeMillis)
                                         : (deathTimeMillis - spawnTimeMillis);
    }
}
