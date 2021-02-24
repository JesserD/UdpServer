package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Tank {
    private int health;
    private boolean placed;
    private List<Projectile> projectiles;


    public Tank() {
    }

    public Tank(int health) {
        this.health = health;
        this.placed = false;
        this.projectiles = new ArrayList<>();
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isPlaced() {
        return this.placed;
    }

    public boolean getPlaced() {
        return this.placed;
    }

    public void setPlaced(boolean placed) {
        this.placed = placed;
    }

    public List<Projectile> getProjectiles() {
        return this.projectiles;
    }

    public void setProjectiles(List<Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    public Tank health(int health) {
        setHealth(health);
        return this;
    }

    public Tank placed(boolean placed) {
        setPlaced(placed);
        return this;
    }

    public Tank projectiles(List<Projectile> projectiles) {
        setProjectiles(projectiles);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Tank)) {
            return false;
        }
        Tank tank = (Tank) o;
        return health == tank.health && placed == tank.placed && Objects.equals(projectiles, tank.projectiles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(health, placed, projectiles);
    }

    @Override
    public String toString() {
        return "{" +
            " health='" + getHealth() + "'" +
            ", placed='" + isPlaced() + "'" +
            ", projectiles='" + getProjectiles() + "'" +
            "}";
    }
    


}