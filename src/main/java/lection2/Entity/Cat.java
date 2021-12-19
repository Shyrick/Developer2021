package lection2.Entity;

public class Cat {
    // Создаем класс, с полями как в БД
    // Поля не обязательно называть строго как в БД, но как-то близко, чтоб было интуитивно понятно
    private long id;
    private String catName;
    private float weight;
    private boolean sex;
    private long ownerId;

    public long getId() {
        return id;
    }

    public String getCatName() {
        return catName;
    }

    public float getWeight() {
        return weight;
    }

    public boolean getSex() {
        return sex;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "Cat{" +
                "id=" + id +
                ", catName='" + catName + '\'' +
                ", weight=" + weight +
                ", sex=" + sex +
                ", ownerId=" + ownerId +
                '}';
    }
}
