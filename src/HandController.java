import javax.swing.SwingUtilities;

import org.opencv.core.Core; //xử lý cơ bản
import org.opencv.core.Mat; //ma trận
import org.opencv.core.Rect; ///định nghĩa vùng hình chữ nhật
import org.opencv.core.Scalar; ///định nghĩa màu sắc 
import org.opencv.highgui.HighGui; //Hiển thị cửa sổ ảnh
import org.opencv.imgproc.Imgproc;// Các hàm xử lý ảnh
import org.opencv.imgproc.Moments;//tính trọng tâm của vùng ảnh
import org.opencv.videoio.VideoCapture;//kết nối cam

public class HandController implements Runnable {
    private static final String CAMERA_WINDOW = "Flappy Bird Camera"; //tên của sổ
    private static final int MOTION_THRESHOLD = 1800; //ngưỡng pixel xác định có chuyển động tay
    private static final double STABLE_BLEND = 0.35; // giảm giật, làm mượt 

    private final FlappyBird game; //tạo đối tượng tham chiếu tới class Flappy Bird
    private volatile boolean running = true; 
    private double lastTrackedNormalizedY = 0.5; // vị trí tay trước đó (mặc định là con chim ở giữa)

    public HandController(FlappyBird game) {
        this.game = game;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            OpenCVLoader.load(); //load file 
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Khong nap duoc OpenCV: " + e.getMessage());
            return;
        }

        VideoCapture camera = new VideoCapture(0); //mở cam
        if (!camera.isOpened()) {
            System.err.println("Khong mo duoc webcam.");
            return;
        }
        // Này là gọi hàm Matrix viết tắt là Mat()
        Mat frame = new Mat(); //ảnh gốc từ cam
        Mat grayFrame = new Mat(); //ảnh xám
        Mat previousGrayFrame = new Mat();//ảnh xám cũ
        Mat difference = new Mat();//sai khác giữa 2 grayFrame và previousGrayFrame
        Mat thresholded = new Mat();// Ảnh nhị phân (đen trắng)

        while (running) {
            if (!camera.read(frame) || frame.empty()) {
                continue;
            }

            Core.flip(frame, frame, 1); //lật ảnh lại như gương cho dễ nhìn
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY); // Chuyển ảnh sang grayscale (giảm dữ liệu)
            Imgproc.GaussianBlur(grayFrame, grayFrame, new org.opencv.core.Size(21, 21), 0); // Làm mờ ảnh để giảm nhiễu

            if (previousGrayFrame.empty()) {
                grayFrame.copyTo(previousGrayFrame);
                continue;
            }

            int roiX = frame.cols() / 2;
            int roiY = frame.rows() / 5;
            int roiWidth = frame.cols() / 3;
            int roiHeight = frame.rows() / 2;
            Rect waveRegion = new Rect(roiX, roiY, roiWidth, roiHeight); // Tạo hình chữ nhật vùng detect

            Core.absdiff(previousGrayFrame, grayFrame, difference); //so sánh frame cũ vs mới để biết sự khác biệt
            Imgproc.threshold(difference, thresholded, 25, 255, Imgproc.THRESH_BINARY); //nhị phân hóa
            //Nếu trắng thì có chuyển động

            Mat regionMask = thresholded.submat(waveRegion); // Cắt vùng ROI từ ảnh
            int motionPixels = Core.countNonZero(regionMask); // Đếm số pixel trắng (mức độ chuyển động)
            boolean handDetected = motionPixels > MOTION_THRESHOLD; // Nếu vượt ngưỡng thì coi như có tay
            double centerY = -1;// vị trí Y của tay
            double normalizedY = lastTrackedNormalizedY;// vị trí đã làm mượt

            if (handDetected) {
                Moments moments = Imgproc.moments(regionMask, true); // Tính trọng tâm vùng chuyển động
                if (moments.get_m00() > 0) {
                    centerY = moments.get_m01() / moments.get_m00(); //tính vị trí Y trung tâm
                    double rawNormalizedY = centerY / roiHeight; // Chuẩn hóa về [0,1]
                    normalizedY = (lastTrackedNormalizedY * (1.0 - STABLE_BLEND)) + (rawNormalizedY * STABLE_BLEND); // Làm mượt (blend giữa cũ và mới)
                    lastTrackedNormalizedY = normalizedY;// Cập nhật lại vị trí
                }
            }

            Scalar regionColor = handDetected ? new Scalar(0, 255, 0) : new Scalar(0, 165, 255);
            //khung thành màu xanh nếu phát hiện chuyển động, còn không thì màu cam
            Imgproc.rectangle(frame, waveRegion, regionColor, 2); //vẽ khung ROI

            HighGui.imshow(CAMERA_WINDOW, frame); //hiển thị cửa sổ cam
            HighGui.waitKey(1); //delay 1 giây để cập nhật frame

            if (handDetected) {
                final double controlY = normalizedY;
                SwingUtilities.invokeLater(() -> game.setBirdPositionFromControl(controlY)); //gửi vị trí sang game
            }

            grayFrame.copyTo(previousGrayFrame);// Lưu frame hiện tại để so sánh vòng sau

            regionMask.release();//giải phóng bộ nhớ
        }

        camera.release();//đóng cam 
        HighGui.destroyWindow(CAMERA_WINDOW);
    }
}
