package be.easy.bench;

import be.easy.bench.model.UlidModel;
import com.github.f4b6a3.ulid.Ulid;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
public class DataRepositoryUlid {

  private static final String QUERY = "SELECT * FROM test.speed_ulid where id = ? ";
  private static final String QUERY_RANDOM =
      "SELECT * FROM test.speed_ulid ORDER BY random() LIMIT 100";

  @Benchmark
  public List<UlidModel> test(DbResourceUlid resource) {
    List<UlidModel> res = new ArrayList<>(100);

    for (String id : resource.ids) {
      try (PreparedStatement preparedStatement =
          resource.connection.prepareStatement(QUERY)) {

        preparedStatement.setObject(1, id.getBytes()); //bytea
//        preparedStatement.setObject(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {

          UlidModel ulidModel = new UlidModel();
          ulidModel.setId(Ulid.from(new String(resultSet.getBytes("id")))); //bytea
//          ulidModel.setId(Ulid.from(resultSet.getString("id")));
          ulidModel.setName(resultSet.getString("name"));
          ulidModel.setCreated(resultSet.getTimestamp("created"));

          res.add(ulidModel);
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
  public static class DbResourceUlid {

    private Connection connection;
    private final List<String> ids = new ArrayList<>(100);

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
          ids.add(new String(resultSet.getBytes("id"))); // bytea
//          ids.add(resultSet.getString("id"));
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
