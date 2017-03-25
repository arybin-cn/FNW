package info.arybin.fearnotwords.model.orm;

import org.litepal.annotation.Column;

//A Translation belongs to an Entity
public class Translation extends LocalizedORM {
    private long id;
    @Column(nullable = false)
    private Entity entity;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
