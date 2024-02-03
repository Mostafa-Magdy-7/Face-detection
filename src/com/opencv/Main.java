package com.opencv;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.highgui.HighGui;
import org.opencv.objdetect.Objdetect;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.highgui.HighGui;
import org.opencv.objdetect.Objdetect;

import java.io.*;
public class Main {
    public static void main(String[] args) {
        // Loading the core library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Create a file chooser dialog
        JFileChooser fileChooser = new JFileChooser();

        // Show the file chooser dialog
        int result = fileChooser.showOpenDialog(null);

        // Check if a file was selected
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();

            Mat image = Imgcodecs.imread(filePath);
            // Resize the image to a smaller size
            int newWidth = 800; // Adjust the desired width
            int newHeight = (int) ((double) newWidth / image.width() * image.height());
            Imgproc.resize(image, image, new Size(newWidth, newHeight));
            detectAndSave(image);
        }


    }

    private static void detectAndSave(Mat image) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(image, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Imgproc.equalizeHist(grayFrame, grayFrame);

        int height = grayFrame.height();
        int absoluteFaceSize = Math.round(height * 0.0005f); // Adjust the fraction as needed

        CascadeClassifier faceCascade = new CascadeClassifier();
        String xmlPath = "src/haarcascade_frontalface_alt2.xml";
        faceCascade.load(xmlPath);

        faceCascade.detectMultiScale(grayFrame, faces, 1.1, 2, 0 | Objdetect.CASCADE_SCALE_IMAGE, new Size(absoluteFaceSize, absoluteFaceSize), new Size());

        Rect[] faceArray = faces.toArray();
        for (int i = 0; i < faceArray.length; i++) {
            Rect face = faceArray[i];

            // Draw red square around the face
            Imgproc.rectangle(image, face.tl(), face.br(), new Scalar(0, 0, 255), 3);

            // Add index label
            String label = String.valueOf(i);
            Point labelPosition = new Point(face.tl().x, face.tl().y - 10);
            Imgproc.putText(image, label, labelPosition, Imgproc.FONT_HERSHEY_SIMPLEX, 0.9, new Scalar(0, 0, 255), 2);

            // Save the detected face as a separate image
            Mat detectedFace = new Mat(image, face);
            String outputDirectory = "cropped_faces"; // Replace with the absolute directory path
            String outputFileName = outputDirectory + "/face_" + i + ".jpg";
            Imgcodecs.imwrite(outputFileName, detectedFace);
        }

        Imgcodecs.imwrite("output.jpg", image);
        System.out.println("Success! Detected " + faceArray.length + " faces.");
        HighGui.imshow("output.jpg", image);
        HighGui.waitKey();
    }
}
