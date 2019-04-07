package discord.entity;

public class Channel extends Entity {
    public Channel(long id) { super(id); }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Channel)) {
            return false;
        }
        return id == ((Channel)obj).id;
    }
    
    @Override
    public String toString() {
        return "Channel(" + id + ")";
    }
}
