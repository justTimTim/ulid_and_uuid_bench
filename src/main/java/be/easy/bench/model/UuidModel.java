package be.easy.bench.model;

import java.sql.Timestamp;
import java.util.UUID;

public class UuidModel {

  private UUID id;
  private String name;
  private Timestamp created;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
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
