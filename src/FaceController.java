import javax.swing.SwingUtilities;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.videoio.VideoCapture;

public class FaceController implements Runnable {
    private static final String CAMERA_WINDOW = "Flappy Bird Camera";
    private static final int MOTION_THRESHOLD = 1800;
    private static final double STABLE_BLEND = 0.35;

    private final FlappyBird game;
    private volatile boolean running = true;
    private double lastTrackedNormalizedY = 0.5;

    public FaceController(FlappyBird game) {
        this.game = game;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            OpenCVLoader.load();
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Khong nap duoc OpenCV: " + e.getMessage());
            return;
        }

        VideoCapture camera = new VideoCapture(0);
        if (!camera.isOpened()) {
            System.err.println("Khong mo duoc webcam.");
            return;
        }

        Mat frame = new Mat();
        Mat grayFrame = new Mat();
        Mat previousGrayFrame = new Mat();
        Mat difference = new Mat();
        Mat thresholded = new Mat();

        while (running) {
            if (!camera.read(frame) || frame.empty()) {
                continue;
            }

            Core.flip(frame, frame, 1);
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
            Imgproc.GaussianBlur(grayFrame, grayFrame, new org.opencv.core.Size(21, 21), 0);

            if (previousGrayFrame.empty()) {
                grayFrame.copyTo(previousGrayFrame);
                continue;
            }

            int roiX = frame.cols() / 2;
            int roiY = frame.rows() / 5;
            int roiWidth = frame.cols() / 3;
            int roiHeight = frame.rows() / 2;
            Rect waveRegion = new Rect(roiX, roiY, roiWidth, roiHeight);

            Core.absdiff(previousGrayFrame, grayFrame, difference);
            Imgproc.threshold(difference, thresholded, 25, 255, Imgproc.THRESH_BINARY);

            Mat regionMask = thresholded.submat(waveRegion);
            int motionPixels = Core.countNonZero(regionMask);
            boolean handDetected = motionPixels > MOTION_THRESHOLD;
            double centerY = -1;
            double normalizedY = lastTrackedNormalizedY;

            if (handDetected) {
                Moments moments = Imgproc.moments(regionMask, true);
                if (moments.get_m00() > 0) {
                    centerY = moments.get_m01() / moments.get_m00();
                    double rawNormalizedY = centerY / roiHeight;
                    normalizedY = (lastTrackedNormalizedY * (1.0 - STABLE_BLEND)) + (rawNormalizedY * STABLE_BLEND);
                    lastTrackedNormalizedY = normalizedY;
                }
            }

            Scalar regionColor = handDetected ? new Scalar(0, 255, 0) : new Scalar(0, 165, 255);
            Imgproc.rectangle(frame, waveRegion, regionColor, 2);
            Imgproc.putText(frame, "Move hand inside box", new org.opencv.core.Point(roiX, roiY - 10),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.7, regionColor, 2);
            Imgproc.line(frame, new org.opencv.core.Point(roiX, roiY + (roiHeight / 2)),
                    new org.opencv.core.Point(roiX + roiWidth, roiY + (roiHeight / 2)),
                    new Scalar(255, 255, 0), 2);
            Imgproc.putText(frame, "Motion: " + motionPixels, new org.opencv.core.Point(20, 35),
                    Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, new Scalar(255, 255, 255), 2);
            String statusText = "No hand movement";
            if (handDetected) {
                statusText = "Bird follows hand position";
            }
            Imgproc.putText(frame, statusText,
                    new org.opencv.core.Point(20, 70), Imgproc.FONT_HERSHEY_SIMPLEX, 0.8, regionColor, 2);

            if (centerY >= 0) {
                int markerX = roiX + (roiWidth / 2);
                int markerY = roiY + (int) centerY;
                Imgproc.circle(frame, new org.opencv.core.Point(markerX, markerY), 10, new Scalar(255, 0, 255), -1);
            }

            int mappedBirdY = (int) Math.round(normalizedY * 100);
            Imgproc.putText(frame, "Mapped Y: " + mappedBirdY + "%",
                    new org.opencv.core.Point(20, 105), Imgproc.FONT_HERSHEY_SIMPLEX, 0.8,
                    new Scalar(255, 255, 255), 2);

            HighGui.imshow(CAMERA_WINDOW, frame);
            HighGui.waitKey(1);

            if (handDetected) {
                final double controlY = normalizedY;
                SwingUtilities.invokeLater(() -> game.setBirdPositionFromControl(controlY));
            }

            grayFrame.copyTo(previousGrayFrame);
            regionMask.release();
        }

        camera.release();
        HighGui.destroyWindow(CAMERA_WINDOW);
    }
}
