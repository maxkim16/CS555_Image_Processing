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
	
	public static BufferedImage loadImage(String path) {
		BufferedImage img = null;
		try {
			File imgFile = new File(path);
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
		lena = loadImage("/Users/maxkim/Applications/maxfolder/CPP/"
				+ "CS555_Image_Processing_Raheja/Image_Operations/lena_gray.gif");
		
		// get the gray scale version of Lena image
		lenaGray = convertToGrayScale(lena);

		// get the pixels of the image by getting the raster of the image
		lenaArr = imgTo2DArrPixel(lenaGray);

		// decrease the intensity of the picture by 150
		//decreaseIntensity(lenaArr, 150);
		changeGrayResPixel(lenaArr, 5);
		
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
		writeFile(modifiedLena, "changed555.gif");
	}
	
	// this method returns the sampleModel of the bufferedImage
	public static SampleModel getSampleModel(BufferedImage bi) {
		Raster raster = bi.getData();
		SampleModel sampleModel = raster.getSampleModel();
		return sampleModel;
	}
	
	// Use Nearest Neighbors Interpolation algorithm to zoom the image
	public static void zoomNeighbors(BufferedImage bi, int newW, int newH, String filename) {
		BufferedImage zoomedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_GRAY);
		SampleModel sampleModel = null;
		int w1, h1, w2, h2;
		int[][] oriImgPixel, zoomedPixel;
		int[] pixelIn1dArr, zoomedPixelIn1dArr;
		w1 = bi.getWidth();
		h1 = bi.getHeight();
		oriImgPixel = new int[w1][h1];
		
		
		try {
			oriImgPixel = imgTo2DArrPixel(bi);
		} catch (IOException e) {
						e.printStackTrace();
		}
		pixelIn1dArr = gridTo1dArr(oriImgPixel);
		// Use Bilinear Algorithm on the given pixel values.
		zoomedPixelIn1dArr = resizePixelsNN(pixelIn1dArr, w1, h1, newW, newH); 
		zoomedPixel = pixelTo1dToGrid(zoomedPixelIn1dArr, newW, newH);
		
		sampleModel = getSampleModel(zoomedImage);
		zoomedImage = convertPixelToBufImg(zoomedPixel, sampleModel);
		writeFile(zoomedImage, filename);
	}
	
	public static void zoomLinearY(BufferedImage bi, int newW, int newH,String direction, String filename) {
		BufferedImage zoomedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_GRAY);
		SampleModel sampleModel = null;
		int w1, h1, w2, h2;
		int[][] oriImgPixel, zoomedPixel;
		int[] pixelIn1dArr, zoomedPixelIn1dArr;
		w1 = bi.getWidth();
		h1 = bi.getHeight();
		oriImgPixel = new int[w1][h1];
		
		
		try {
			oriImgPixel = imgTo2DArrPixel(bi);
		} catch (IOException e) {
						e.printStackTrace();
		}
		pixelIn1dArr = gridTo1dArr(oriImgPixel);
		// Use Linear Algorithm on the given pixel values (only Y values)
		zoomedPixelIn1dArr = resizeLinearGrayY(pixelIn1dArr, w1, h1, newW, newH, direction); 
		
		// Use Nearest Neighbors to interpolate x values /////////////
		zoomedPixelIn1dArr = useNNxValues(pixelIn1dArr, w1, h1, newW, newH);
		
		zoomedPixel = pixelTo1dToGrid(zoomedPixelIn1dArr, newW, newH);
		
		sampleModel = getSampleModel(zoomedImage);
		zoomedImage = convertPixelToBufImg(zoomedPixel, sampleModel);
		writeFile(zoomedImage, filename);
	}
	
	// Use Lilinear Interpolation to zoom the image only in y-direction
		public static int[] resizeLinearGrayY(int[] pixels, int w, int h, int w2, int h2, String direction) {
			int rowsForLinearInt = h2/h;
			System.out.println("selected rows: " + rowsForLinearInt);
			int[][] temp2 = new int[w2][h2];
		    int[] temp = new int[w2*h2] ;
		    int A, B, C, D, x, y, index, gray ;
		    float x_ratio = ((float)(w-1))/w2 ;
		    float y_ratio = ((float)(h-1))/h2 ;
		    float x_diff, y_diff;
		    int offset = 0 ;
		    for (int i=0;i<h2;i+=rowsForLinearInt) {
		        for (int j=0;j<w2;j++) {
		            x = (int)(x_ratio * j) ;
		            y = (int)(y_ratio * i) ;
		            x_diff = (x_ratio * j) - x ;
		            y_diff = (y_ratio * i) - y ;
		            index = y*w+x ;

		            // range is 0 to 255 thus bitwise AND with 0xff
		            A = pixels[index] & 0xff ;
		            B = pixels[index+1] & 0xff ;
		            // C = pixels[index+w] & 0xff ;
		            // D = pixels[index+w+1] & 0xff ;
		            
		            // Y = A(1-w)(1-h) + B(w)(1-h) + C(h)(1-w) + Dwh
		            gray = (int)(
		                    A*(1-x_diff)*(1-y_diff) +  B*(x_diff)*(1-y_diff) 
		                    ) ;
		            temp2[i][j] = gray;
		            //temp[offset++] = gray ;                                   
		        }
		    }
		    // return temp;
		    return gridTo1dArr(temp2);
		    //return temp;
		}
		
	
	public static void zoomBilinear(BufferedImage bi, int newW, int newH, String filename) {
		BufferedImage zoomedImage = new BufferedImage(newW, newH, BufferedImage.TYPE_BYTE_GRAY);
		SampleModel sampleModel = null;
		int w1, h1, w2, h2;
		int[][] oriImgPixel, zoomedPixel;
		int[] pixelIn1dArr, zoomedPixelIn1dArr;
		w1 = bi.getWidth();
		h1 = bi.getHeight();
		oriImgPixel = new int[w1][h1];
		
		
		try {
			oriImgPixel = imgTo2DArrPixel(bi);
		} catch (IOException e) {
						e.printStackTrace();
		}
		pixelIn1dArr = gridTo1dArr(oriImgPixel);
		// Use Bilinear Algorithm on the given pixel values.
		zoomedPixelIn1dArr = resizeBilinearGray(pixelIn1dArr, w1, h1, newW, newH); 
		zoomedPixel = pixelTo1dToGrid(zoomedPixelIn1dArr, newW, newH);
		
		sampleModel = getSampleModel(zoomedImage);
		zoomedImage = convertPixelToBufImg(zoomedPixel, sampleModel);
		writeFile(zoomedImage, filename);
	}
	
	// Use Bilinear Interpolation to zoom the image
	public static int[] resizeBilinearGray(int[] pixels, int w, int h, int w2, int h2) {
	    int[] temp = new int[w2*h2] ;
	    int A, B, C, D, x, y, index, gray ;
	    float x_ratio = ((float)(w-1))/w2 ;
	    float y_ratio = ((float)(h-1))/h2 ;
	    float x_diff, y_diff, ya, yb ;
	    int offset = 0 ;
	    for (int i=0;i<h2;i++) {
	        for (int j=0;j<w2;j++) {
	            x = (int)(x_ratio * j) ;
	            y = (int)(y_ratio * i) ;
	            x_diff = (x_ratio * j) - x ;
	            y_diff = (y_ratio * i) - y ;
	            index = y*w+x ;

	            // range is 0 to 255 thus bitwise AND with 0xff
	            A = pixels[index] & 0xff ;
	            B = pixels[index+1] & 0xff ;
	            C = pixels[index+w] & 0xff ;
	            D = pixels[index+w+1] & 0xff ;
	            
	            // Y = A(1-w)(1-h) + B(w)(1-h) + C(h)(1-w) + Dwh
	            gray = (int)(
	                    A*(1-x_diff)*(1-y_diff) +  B*(x_diff)*(1-y_diff) +
	                    C*(y_diff)*(1-x_diff)   +  D*(x_diff*y_diff)
	                    ) ;

	            temp[offset++] = gray ;                                   
	        }
	    }
	    return temp ;
	}
	
	
	
	public static int[][] pixelTo1dToGrid(int[] a, int h, int w) {
		int[][] b = new int[h][w];
		for (int i = 0; i < h; i++)
			for (int j = 0; j < w; j++)
				b[i][j] = a[(i * w)+j];
		return b;
	}
	
	// useNNxValues(pixelIn1dArr, w1, h1, newW, newH);
	// Use Nearest Neighbors method to interpolate x values
	public static int[] useNNxValues(int[] pixels, int w1, int h1, int w2, int h2) {
	    int[] temp = new int[w2*h2] ;
	    int rowsForXvalues = h2/w1;
	    double x_ratio = w1/(double)w2 ;
	    double y_ratio = h1/(double)h2 ;
	    double px, py ; 
	    for (int i=0;i<h2;i++) {
	    	if( (i == 0) || ( (i % rowsForXvalues) == 0) ) {
	    		continue;
	    	}
	        for (int j=0;j<w2;j++) {
	            px = Math.floor(j*x_ratio) ;
	            py = Math.floor(i*y_ratio) ;
	            temp[(i*w2)+j] = pixels[(int)((py*w1)+px)];
	        }
	    }
	    return temp ;
	}
	
	public static int[] resizePixelsNN(int[] pixels, int w1, int h1, int w2, int h2) {
	    int[] temp = new int[w2*h2] ;
	    double x_ratio = w1/(double)w2 ;
	    double y_ratio = h1/(double)h2 ;
	    double px, py ; 
	    for (int i=0;i<h2;i++) {
	        for (int j=0;j<w2;j++) {
	            px = Math.floor(j*x_ratio) ;
	            py = Math.floor(i*y_ratio) ;
	            temp[(i*w2)+j] = pixels[(int)((py*w1)+px)] ;
	        }
	    }
	    return temp ;
	}
	
	// returns pixel values stored as a grid into a 1d array
	public static int[] gridTo1dArr(int[][] grid) {
		int h1 = grid.length;
		int w1 = grid[0].length;
		int[] arr = new int[w1* h1];
		for (int i = 0; i < h1; i++) 
			for (int j = 0; j < w1; j++)
				arr[(i*w1) + j] = grid[i][j]; 
		return arr;
	}
	
	//
	public static int[][] changeGrayScaleRes(BufferedImage bi, int numOfBits, String name) throws IOException {
		BufferedImage biGray, modifiedBi = null;
		int[][] biGrayArr = null;
		SampleModel sampleModel;
		Raster raster = null;
		
		// get the gray scale version of Lena image
		biGray = convertToGrayScale(bi);

		// get the pixels of the image by getting the raster of the image
		biGrayArr = imgTo2DArrPixel(biGray);

		// change the number of bits of grayscale resolution
		changeGrayResPixel(biGrayArr, numOfBits);
		
		// create a buffered image that will store the modified image
		// make sure it matches the size and the type of the original image
		// if the two images will be the same size and same type
		modifiedBi = new BufferedImage(biGrayArr.length, biGrayArr[0].length, BufferedImage.TYPE_BYTE_GRAY);
		
		// sampleModel will be stored into a writableRaster, which will need a sample raster
		// SampleModel represents samples of pixels in am image
		// WritableRaster therefore needs it to see what kind of samples an image has
		// For example, the number of samples, etc.
		raster = modifiedBi.getData();
	    sampleModel = raster.getSampleModel();

	    // convert the pixel values into a bufferedImage
		modifiedBi = convertPixelToBufImg(biGrayArr, sampleModel);
		
		// write the bufferedImage into a file and save it
		writeFile(modifiedBi, name);
		return null;
	}
	
	// change the grayscale resolution by modifying all the pixel values
	public static void changeGrayResPixel(int[][] pixel, int numOfBits) {
		// commonly the number of bits used for the byte image is 8 in a grayscale image
		int defaultNumOfBits = 8; // 
		int numOfRow = pixel.length;
		int numOfCol = pixel[0].length;
		// change the grayscale resolution
		for(int i = 0; i < numOfRow; i++)	{
			for(int j = 0; j < numOfCol; j++) {
				pixel[i][j] = (int) Math.floor( (pixel[i][j]) / (Math.pow(2, 8 - numOfBits)) );
			}
		}
	}
	
	// shrink the pixel grid by given the size
	public static int[][] shrinkPixel(int[][] pixel, int numRow, int numCol) {
		int[][] shrinkedPixel = new int[numRow][numCol];
		int numRowOriImg = pixel.length;
		int numColOriImg = pixel[0].length;
		int selectedRow, selectedCol;
		selectedRow = (int) Math.floor((numRowOriImg / numRow));
		selectedCol = (int) Math.floor((numColOriImg / numCol));
		for(int i = 0; i < numRow; i++)
			for(int j = 0; j < numCol; j++)
				shrinkedPixel[i][j] =  pixel[i * selectedRow][j * selectedCol ];
		return shrinkedPixel;
	}
	
	public static void shrinkPixelTest(int newSize, String filename) throws IOException {
		BufferedImage lena, lenaGray, modifiedLena = null;
		int[][] lenaArr = null;
		int[][] shrinkedArr = null;
		SampleModel sampleModel;
		Raster raster = null;
		
		// load the image
		lena = loadImage("/Users/maxkim/Applications/maxfolder/CPP/"
				+ "CS555_Image_Processing_Raheja/Image_Operations/lena_gray.gif");
		
		// get the gray scale version of Lena image
		lenaGray = convertToGrayScale(lena);

		// get the pixels of the image by getting the raster of the image
		lenaArr = imgTo2DArrPixel(lenaGray);

		// decrease the intensity of the picture by 150
		//decreaseIntensity(lenaArr, 150);
		shrinkedArr = shrinkPixel(lenaArr ,newSize, newSize);
		
		// create a buffered image that will store the modified image
		// make sure it matches the size and the type of the original image
		// if the two images will be the same size and same type
		modifiedLena = new BufferedImage(newSize, newSize, BufferedImage.TYPE_BYTE_GRAY);
		
		// sampleModel will be stored into a writableRaster, which will need a sample raster
		// SampleModel represents samples of pixels in am image
		// WritableRaster therefore needs it to see what kind of samples an image has
		// For example, the number of samples, etc.
		raster = modifiedLena.getData();
	    sampleModel = raster.getSampleModel();

	    // convert the pixel values into a bufferedImage
		modifiedLena = convertPixelToBufImg(shrinkedArr, sampleModel);
		
		// write the bufferedImage into a file and save it
		writeFile(modifiedLena, filename);
	}
	
	public static void main(String[] args) throws IOException {
		BufferedImage img, imgGray = null;
		img = loadImage("/Users/maxkim/Applications/maxfolder/CPP/"
				+ "CS555_Image_Processing_Raheja/Image_Operations/shrinked64.gif");
		imgGray = convertToGrayScale(img);
		zoomNeighbors(imgGray, 1024, 1024, "nn1024.gif"); // Use Nearest
															// Neighbors
															// Interpolation to
															// zoom an image
		zoomBilinear(imgGray, 1024, 1024, "bi1024.gif"); // Use Bilinear
															// Interpolation to
															// zoom the image
		zoomLinearY(imgGray, 1024, 1024 ,"x", "linearY1024.gif");
		changeGrayScaleRes(imgGray, 5, "res5.gif");
		shrinkPixelTest(64, "shrinked64.gif");
		zoomLinearY(imgGray, 1024, 1024 ,"x", "linearX1024.gif");

		int[][]a = new int[3][3];
	}
}
