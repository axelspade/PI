package sample;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;

public class SpacialFilters {
    private ArrayList<Integer> pixelsRed;
    private ArrayList<Integer> pixelsGreen;
    private ArrayList<Integer> pixelsBlue;
    private Integer width;
    private Integer height;

    public BufferedImage medianFilter(Integer matrixSize,BufferedImage bufferedImage) {
        initializeValues(bufferedImage);
        Integer borderSize = (matrixSize - 1)/2;
        Integer middlePixel = matrixSize * matrixSize / 2;
        for(int i = borderSize; i < height - borderSize ; i++ )
            for(int j = borderSize; j < width - borderSize ; j++ ) {
                for(int row = i - borderSize; row <= i + borderSize; row++)
                    for(int column = j - borderSize;column <= j + borderSize; column++ ) {
                        addPixelComponentsInArrays(bufferedImage, row, column);
                    }

                pixelsRed.sort(Comparator.comparingInt(a -> a));
                pixelsGreen.sort(Comparator.comparingInt(a -> a));
                pixelsBlue.sort(Comparator.comparingInt(a -> a));

                int newPixelRed = pixelsRed.get(middlePixel);
                int newPixelGreen = pixelsGreen.get(middlePixel);
                int newPixelBlue = pixelsBlue.get(middlePixel);

                emptyArrays();

                bufferedImage.setRGB(i,j,(newPixelRed<<16) | (newPixelGreen<<8) | newPixelBlue);
            }

        return bufferedImage;
    }

    private void emptyArrays() {
        pixelsRed.clear();
        pixelsGreen.clear();
        pixelsBlue.clear();
    }

    private void addPixelComponentsInArrays(BufferedImage bufferedImage, int row, int column) {
        int pixel = bufferedImage.getRGB(row, column);
        pixelsRed.add((pixel >> 16) & 0xff);
        pixelsGreen.add((pixel >> 8) & 0xff);
        pixelsBlue.add(pixel & 0xff);
    }

    public BufferedImage biomedicalImprovement(ArrayList<Integer> mask,BufferedImage bufferedImage) {
        if(mask.size() == 9) {
            initializeValues(bufferedImage);
            for(int i = 1; i < height - 1 ;i++)
                for(int j = 1; j < width - 1; j++)
                {
                    int maskRow = 0;
                    int maskColumn = 0;
                    for(int row = i - 1; row <= i + 1; row++) {
                        for (int column = j - 1; column <= j + 1; column++) {
                            int pixel = bufferedImage.getRGB(row, column);
                            int pixelRed = (pixel >> 16) & 0xff;
                            int pixelGreen = (pixel >> 8) & 0xff;
                            int pixelBlue = pixel & 0xff;

                            int maskValue = mask.get(maskRow * 3 + maskColumn);
                            pixelRed = pixelRed + maskValue;
                            pixelGreen = pixelGreen + maskValue;
                            pixelBlue = pixelBlue + maskValue;

                            bufferedImage.setRGB(row, column, (pixelRed << 16) | (pixelGreen << 8) | pixelBlue);
                            maskColumn++;
                        }
                        maskRow++;
                        maskColumn = 0;
                    }
                }
        }
        return bufferedImage;
    }

    private void initializeValues(BufferedImage bufferedImage) {
        width = bufferedImage.getWidth();
        height = bufferedImage.getHeight();
        pixelsRed = new ArrayList<>();
        pixelsGreen = new ArrayList<>();
        pixelsBlue = new ArrayList<>();
    }
}
