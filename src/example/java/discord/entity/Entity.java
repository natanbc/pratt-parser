package discord.entity;

public abstract class Entity {
    protected final long id;
    
    public Entity(long id) { this.id = id; }
    
    public long id() {
        return id;
    }
}
