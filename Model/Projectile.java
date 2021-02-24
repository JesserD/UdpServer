package Model;

import java.util.Objects;

public class Projectile {
    private String id;
    private float x;
    private float y;
    private float z;
    private float directionAngel;

    public Projectile(String id) {
        this.id = id;
    }

    public Projectile(float directionAngel, float x, float y, float z, String id) {
        this.directionAngel = directionAngel;
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return this.z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getDirectionAngel() {
        return this.directionAngel;
    }

    public void setDirectionAngel(float directionAngel) {
        this.directionAngel = directionAngel;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Projectile)) {
            return false;
        }
        Projectile projectile = (Projectile) o;
        return Objects.equals(id, projectile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, x, y, z);
    }


    @Override
    public String toString() {
        return "{" +
            " id='" + getId() + "'" +
            ", x='" + getX() + "'" +
            ", y='" + getY() + "'" +
            ", z='" + getZ() + "'" +
            ", directionAngel='" + getDirectionAngel() + "'" +
            "}";
    }



}
