package com.project.test.util;

import com.project.test.pageobjects.base.BaseInformations;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import nu.pattern.OpenCV;

public class ImageUtils {
	
	private BaseInformations baseInformations;

    public ImageUtils(BaseInformations baseInformations) {
        this.baseInformations = baseInformations;
        OpenCV.loadLocally();
    }

    public boolean isImagePresentOnTheScreenshot(String subImageName, double matchingCoefficient) {
        if (matchingCoefficient < 0.0 & matchingCoefficient > 1.0)
            throw new InvalidArgumentException("Matching Coefficient must be between 0 and 1");

        String pathOfScreenshoot = ((TakesScreenshot) baseInformations.getDriver()).getScreenshotAs(OutputType.FILE).getAbsolutePath();
        String absoluteSubImagePath = FilePathUtils.getInstance().getImagesFilePath(subImageName);

        Mat subImage = Imgcodecs.imread(pathOfScreenshoot);
        Mat image = Imgcodecs.imread(absoluteSubImagePath);
        Mat result = new Mat();

        Imgproc.matchTemplate(subImage, image, result, Imgproc.TM_CCOEFF_NORMED);
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);

        return mmr.maxVal >= matchingCoefficient;
    }

}
