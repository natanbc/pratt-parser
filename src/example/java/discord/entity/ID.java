package discord.entity;

public class ID extends Entity {
    public ID(long id) {
        super(id);
    }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof ID)) {
            return false;
        }
        return id == ((ID)obj).id;
    }
    
    @Override
    public String toString() {
        return "ID(" + id + ")";
    }
}
