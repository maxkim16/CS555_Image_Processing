package image_Operations;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Image_Operations {
	
	public static void getImgInfo(BufferedImage bi) {

		// print the height and width of the image
		System.out.println("width:" + bi.getWidth());
		System.out.println("height:" + bi.getHeight());
		System.out.println("type: " + bi.getType());
		System.out.println("cm: " + bi.getColorModel());

	}
	
	// convert the BufferedImage into a 2D array 
	public static int[][] imgTo2DArrPixel(BufferedImage img) throws IOException {
	    int width = img.getWidth();
	    int height = img.getHeight();
	    int[][] imgArr = new int[width][height];
	    
	    // get the pixels of the image
	    Raster raster = img.getData();
	    
	    // sampleModel will be used when converting the modified pixel values back into the modified raster and BuffedImage
	    //sampleModel = raster.getSampleModel();
	    
	    // copy the pixels into the 2d array
	    for (int i = 0; i < width; i++) {
	        for (int j = 0; j < height; j++) {
	            imgArr[i][j] = raster.getSample(i, j, 0);
	        }
	    }
	    return imgArr;
	}
	
	public static void printPixels(int[][] arr) {
		int row = arr.length;
		int col = arr[0].length;
		for (int i = 0; i < row; i++ ) {
			for (int j = 0; j < col; j++) {
					System.out.print(arr[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("row: " + row);
		System.out.println("col: " + col);

	}
	
	public static BufferedImage loadImage() {
		BufferedImage img = null;
		try {
			File imgFile = new File("/Users/maxkim/Applications/maxfolder/CPP/"
					+ "CS555_Image_Processing_Raheja/Image_Operations/lena_gray.gif");
			img = ImageIO.read(imgFile);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return img;
	}
	
	public static void writeFile(BufferedImage bi, String name) {
		try {
		    File outputfile = new File(name);
		    ImageIO.write(bi, "gif", outputfile);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	// convert the type of BufferedImage to TYPE_BYTE_GRAY and return it
	public static BufferedImage convertToGrayScale(BufferedImage originalImg) {
	    BufferedImage grayScaleImg = new BufferedImage(originalImg.getWidth(), originalImg.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
	    grayScaleImg.getGraphics().drawImage(originalImg, 0, 0, null);
	    return grayScaleImg;
	}

	public static void decreaseIntensity(int[][] arr, int decVal) {
		int row = arr.length;
		int col = arr[0].length;
		for (int i = 0; i < row; i++ ) {
			for (int j = 0; j < col; j++) {
				if(arr[i][j] < decVal) {
					arr[i][j] = 0;
				}
				else {
					arr[i][j] -= decVal;
				}
			}
		}
	}
	
	public static BufferedImage convertPixelToBufImg(int pixels[][], SampleModel sampleModel) {
		int w = pixels.length;
		int h = pixels[0].length;
		// sampleModel is needed instead of image.getData() function
	    // WritableRaster raster=(WritableRaster)image.getData();  
		WritableRaster raster = Raster.createWritableRaster(sampleModel, new Point(0, 0));
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				raster.setSample(i, j, 0, pixels[i][j]);
			}
		}
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
		image.setData(raster);
		return image;
	}
	
	public static void shrinkPixels(int[][] arr, int newW, int newH) {
		int row = arr.length;
		int col = arr[0].length;
		
		/*
		for (int i = 0; i < row; i++) {
			if(i % 2 == 0) {
				for (int j = 0; j < col; j+=2) {
					arr[i][j] = 0;
				}
			}
			else {
				for (int j = 1; j < col; j+=2) {
					arr[i][j] = 0;
				}
			}	
		}
		*/
		
		for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j+=2) {
					arr[i][j] = 0;
				}	
		}
	}
	
	public static void createSample() throws IOException {
		BufferedImage lena, lenaGray, modifiedLena = null;
		int[][] lenaArr = null;
		SampleModel sampleModel;
		Raster raster = null;
		
		// load the image
		lena = loadImage();
		
		// get the gray scale version of Lena image
		lenaGray = convertToGrayScale(lena);

		// get the pixels of the image by getting the raster of the image
		lenaArr = imgTo2DArrPixel(lenaGray);

		// decrease the intensity of the picture by 150
		decreaseIntensity(lenaArr, 150);

		// create a buffered image that will store the modified image
		// make sure it matches the size and the type of the original image
		// if the two images will be the same size and same type
		modifiedLena = new BufferedImage(lenaArr.length, lenaArr[0].length, BufferedImage.TYPE_BYTE_GRAY);
		
		// sampleModel will be stored into a writableRaster, which will need a sample raster
		// SampleModel represents samples of pixels in am image
		// WritableRaster therefore needs it to see what kind of samples an image has
		// For example, the number of samples, etc.
		raster = modifiedLena.getData();
	    sampleModel = raster.getSampleModel();

	    // convert the pixel values into a bufferedImage
		modifiedLena = convertPixelToBufImg(lenaArr, sampleModel);
		
		// write the bufferedImage into a file and save it
		writeFile(modifiedLena, "ggg.gif");
	}
	
	// this method returns the sampleModel of the bufferedImage
	public static SampleModel getSampleModel(BufferedImage bi) {
		Raster raster = bi.getData();
		SampleModel sampleModel = raster.getSampleModel();
		return sampleModel;
	}
	
	public static void main(String[] args) throws IOException {
		int[][] imgGrayArr, imgArr = null;
		BufferedImage img, imgGray, modifiedImg = null;
		SampleModel sampleModel;

		img = loadImage();
		imgGray = convertToGrayScale(img);
		imgGrayArr = imgTo2DArrPixel(imgGray);
		modifiedImg = new BufferedImage(imgGray.getWidth(), imgGray.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		sampleModel = getSampleModel(modifiedImg);
		decreaseIntensity(imgGrayArr, 150);
		modifiedImg = convertPixelToBufImg(imgGrayArr, sampleModel);
		//writeFile(modifiedImg, "m.gif");
		createSample();
	}
}
