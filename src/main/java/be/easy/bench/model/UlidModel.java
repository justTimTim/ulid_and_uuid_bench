package be.easy.bench.model;

import com.github.f4b6a3.ulid.Ulid;
import java.sql.Timestamp;

public class UlidModel {

  private Ulid id;
  private String name;
  private Timestamp created;

  public Ulid getId() {
    return id;
  }

  public void setId(Ulid id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Timestamp getCreated() {
    return created;
  }

  public void setCreated(Timestamp created) {
    this.created = created;
  }
}
