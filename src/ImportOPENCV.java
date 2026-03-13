

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
 
public class ImportOPENCV {
   public static void main(String[] args) {
       OpenCVLoader.load();

       VideoCapture camera = new VideoCapture(0);
       if (!camera.isOpened()) {
           System.out.println("Khong mo duoc webcam");
           return;
       }

       Mat frame = new Mat();
       if (camera.read(frame) && !frame.empty()) {
           System.out.println("OpenCV version: " + Core.VERSION);
           System.out.println("Webcam OK: " + frame.width() + "x" + frame.height());
       } else {
           System.out.println("Doc frame that bai");
       }

       camera.release();
   }
}
