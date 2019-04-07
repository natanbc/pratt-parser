package discord.entity;

public class Emote extends Entity {
    private final String name;
    
    public Emote(String name, long id) {
        super(id);
        this.name = name;
    }
    
    public String name() {
        return name;
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Emote)) {
            return false;
        }
        return id == ((Emote)obj).id;
    }
    
    @Override
    public String toString() {
        return "Emote(" + name + ":" + id + ")";
    }
}
