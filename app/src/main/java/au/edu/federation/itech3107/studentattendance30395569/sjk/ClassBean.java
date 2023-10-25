package au.edu.federation.itech3107.studentattendance30395569.sjk;


import androidx.room.Entity;
import androidx.room.PrimaryKey;
//classroom list
@Entity
public class ClassBean {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

