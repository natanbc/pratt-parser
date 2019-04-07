package discord.entity;

public class User extends Entity {
    public User(long id) { super(id); }
    
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof User)) {
            return false;
        }
        return id == ((User)obj).id;
    }
    
    @Override
    public String toString() {
        return "User(" + id + ")";
    }
}
