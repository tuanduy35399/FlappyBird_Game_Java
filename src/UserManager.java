import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;

public class UserManager {
    private MongoCollection<Document> usersCollection;

    public UserManager() {
        MongoDatabase database = ConnectDB.getDatabase();
        if (database != null) {
            usersCollection = database.getCollection("users");
        }
    }

    // Xử lý đăng ký
    public boolean register(String username, String password) {
        // Kiểm tra xem user đã tồn tại chưa
        if (usersCollection.find(eq("username", username)).first() != null) {
            return false; // User đã tồn tại
        }
        Document newUser = new Document("username", username)
                .append("password", password) // Lưu ý: Thực tế nên mã hóa mật khẩu
                .append("highScore", 0.0);
        usersCollection.insertOne(newUser);
        return true;
    }

    // Xử lý đăng nhập
    public boolean login(String username, String password) {
        Document user = usersCollection.find(eq("username", username)).first();
        return user != null && user.getString("password").equals(password);
    }

    // Cập nhật điểm cao nhất
    public void updateHighScore(String username, double currentScore) {
        Document user = usersCollection.find(eq("username", username)).first();
        if (user != null) {
            double savedHighScore = user.getDouble("highScore");
            if (currentScore > savedHighScore) {
                usersCollection.updateOne(eq("username", username), set("highScore", currentScore));
            }
        }
    }

    // Lấy điểm cao nhất để hiển thị
    public double getHighScore(String username) {
        Document user = usersCollection.find(eq("username", username)).first();
        return user != null ? user.getDouble("highScore") : 0.0;
    }
}