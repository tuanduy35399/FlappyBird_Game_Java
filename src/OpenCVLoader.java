import java.io.File;

import org.opencv.core.Core;

//Load file dll
///file dll rất quan trọng vì nó đảm bảo OpenCV có thể nạp vào JVM
/// dll sẽ chứa code C++ để xử lý ảnh, tính toán vị trí và truy cập vô phần cứng
/// Java sẽ thông qua file dll để gọi OS và truy cập vào camera
public final class OpenCVLoader {
    private static boolean loaded = false; // cờ đánh dấu đã load thư viện hay chưa

    private OpenCVLoader() {
    }

    public static synchronized void load() { // dùng đồng bộ để đồng nhất 1 luồng xử lý
        // Nếu không dùng nó thì camera với game sẽ chạy 2 luồng riêng
        if (loaded) {
            return;
        }

        try {
            // thử load thư viện theo cách mặc định
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            loaded = true;
            return;
        } catch (UnsatisfiedLinkError ignored) {
            // Có lỗi thì qua load thủ công ở phía dưới
        }

        String[] dsDuongDan = new String[] {
                "C:/Users/Duy/Downloads/JavaLibrary/opencv/build/java/x64/opencv_java4120.dll",
                "C:/Users/Duy/Downloads/JavaLibrary/opencv/build/java/x64/" + Core.NATIVE_LIBRARY_NAME + ".dll"
        };
        // Duyệt thử từng đường dẫn
        for (String duongDan : dsDuongDan) {
            File dll = new File(duongDan); // mở file
            if (dll.exists()) { // nếu có file
                System.load(dll.getAbsolutePath()); // load trực tiếp file .dll
                loaded = true;
                return;
            }
        }
        // không được thì báo lỗi
        throw new UnsatisfiedLinkError("Khong tim thay OpenCV native library (.dll).");
    }
}
