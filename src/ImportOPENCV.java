
public class ImportOPENCV {
    static {
        // Thay đường dẫn thực tế đến file .dll của bạn vào đây
        System.load("C:/Users/Duy/Downloads/opencv/build/java/x64/opencv_java4120.dll");
    }

    public static void main(String[] args) {
        // Test thử xem chạy chưa
        System.out.println("OpenCV version: " + org.opencv.core.Core.VERSION);
    }
}