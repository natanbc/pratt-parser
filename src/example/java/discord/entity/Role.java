package discord.entity;

public class Role extends Entity {
    public Role(long id) { super(id); }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Role)) {
            return false;
        }
        return id == ((Role)obj).id;
    }
    
    @Override
    public String toString() {
        return "Role(" + id + ")";
    }
}
