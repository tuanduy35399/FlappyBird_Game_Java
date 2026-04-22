

import org.opencv.core.Core;
import org.opencv.core.Mat; //gọi Matrix
import org.opencv.videoio.VideoCapture;
 //Đây là file test
public class ImportOPENCV {
   public static void main(String[] args) {
       OpenCVLoader.load();  //load file dll 
       //gọi hàm load trong class OpenCVLoader

       VideoCapture camera = new VideoCapture(0); //mở camera mặc định là số 0
       if (!camera.isOpened()) {
           System.out.println("Khong mo duoc webcam");
           return;
       }

       Mat frame = new Mat(); //tạo đối tượng frame có kiểu dữ liệu Matrix để lưu 1 khung ảnh trên camera
       if (camera.read(frame) && !frame.empty()) { //đọc 1 frame từ camera và lưu vô biến frame và frame đó phải khác rỗng
           System.out.println("OpenCV version: " + Core.VERSION); 
           System.out.println("Webcam OK: " + frame.width() + "x" + frame.height());
       } else {
           System.out.println("Doc frame that bai"); 
       }

       camera.release(); //đóng cam
   }
}
