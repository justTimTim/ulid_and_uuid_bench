package be.easy.bench;

import be.easy.bench.model.UuidModel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;


@State(Scope.Benchmark)
@Warmup(iterations = 2, time = 10)
@Measurement(iterations = 10, time = 10)
@Fork(value = 5)
@Threads(value = -1)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class DataRepositoryUuid {

  private static final String QUERY = "SELECT * FROM test.speed_uuid where id = ? ";
  private static final String QUERY_RANDOM =
      "SELECT * FROM test.speed_uuid ORDER BY random() LIMIT 100";

  @Benchmark
  public List<UuidModel> test(DbResourceUuid resource) {
    List<UuidModel> res = new ArrayList<>(100);
    for (UUID id : resource.ids) {
      try (PreparedStatement preparedStatement = resource.connection.prepareStatement(QUERY)) {

//        preparedStatement.setObject(1, id.toString().getBytes());
        preparedStatement.setObject(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
          UuidModel uuidModel = new UuidModel();
//          uuidModel.setId(UUID.fromString(new String(resultSet.getBytes("id"))));//bytea
          uuidModel.setId(UUID.fromString(resultSet.getString("id")));//uuid
          uuidModel.setName(resultSet.getString("name"));
          uuidModel.setCreated(resultSet.getTimestamp("created"));

          res.add(uuidModel);
        }

      } catch (SQLException e) {
        System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return res;
  }


  @State(Scope.Thread)
  public static class DbResourceUuid {

    private Connection connection;
    private final List<UUID> ids = new ArrayList<>(100);

    @Setup(Level.Trial)
    public void createConnection() throws SQLException {
      connection = DriverManager.getConnection(
          "jdbc:postgresql://localhost:5432/changers", "postgres", "qwerty");
      fillList();
    }

    public void fillList() {
      try (PreparedStatement preparedStatement = connection.prepareStatement(QUERY_RANDOM)) {

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
//          String id = new String(resultSet.getBytes("id")); //bytea
          String id = resultSet.getString("id");
          ids.add(UUID.fromString(id));
        }
      } catch (SQLException e) {
        System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
      }
    }

    @TearDown(Level.Trial)
    public void doTearDown() throws SQLException {
      connection.close();
    }
  }

}
