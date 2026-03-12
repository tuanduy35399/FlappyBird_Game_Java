import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConnectDB {
    private static final String ENV_PATH = ".env";
    private static final Map<String, String> env = new HashMap<>();

    static {
        //đọc file .env 
        try (BufferedReader reader = new BufferedReader(new FileReader(ENV_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;  // bỏ qua dòng trống và comment
                }
                int eq = line.indexOf('=');
                if (eq > 0) {
                    String key = line.substring(0, eq).trim();
                    String value = line.substring(eq + 1).trim();
                    env.put(key, value);
                }
            }
        } catch (IOException e) {
            System.err.println("Unable to read .env file: " + e.getMessage());
        }
    }


    public static MongoDatabase getDatabase() {
        String uri = env.get("MONGODB_URI");
        String dbName = env.get("MONGODB_DB");
        if (uri == null || dbName == null) {
            throw new IllegalStateException("MONGODB_URI and MONGODB_DB must be set in .env");
        }
        MongoClient client = MongoClients.create(uri);
        MongoDatabase database = client.getDatabase(dbName);

        // kiểm tra kết nối bằng cách ping database
        try {
            database.runCommand(new Document("ping", 1));
            System.out.println("Kết nối cơ sở dữ liệu thành công");
        } catch (Exception e) {
            System.err.println("Không thể kết nối đến cơ sở dữ liệu: " + e.getMessage());
        }

        return database;
    }
}
