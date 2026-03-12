import java.io.File;

import org.opencv.core.Core;

public final class OpenCVLoader {
    private static boolean loaded = false;

    private OpenCVLoader() {
    }

    public static synchronized void load() {
        if (loaded) {
            return;
        }

        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            loaded = true;
            return;
        } catch (UnsatisfiedLinkError ignored) {
        }

        String[] candidates = new String[] {
            "C:/Users/Duy/Downloads/opencv/build/java/x64/opencv_java4120.dll",
            "C:/Users/Duy/Downloads/opencv/build/java/x64/" + Core.NATIVE_LIBRARY_NAME + ".dll"
        };

        for (String candidate : candidates) {
            File dll = new File(candidate);
            if (dll.exists()) {
                System.load(dll.getAbsolutePath());
                loaded = true;
                return;
            }
        }

        throw new UnsatisfiedLinkError("Khong tim thay OpenCV native library (.dll).");
    }
}
