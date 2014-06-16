package NetCDF;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PNG {

    private BufferedImage img;

    public PNG(int width, int height){

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    }

    public BufferedImage getImage(){
        return this.img;
    }

    public void save(String path) throws IOException{
        File file = new File(path);
        ImageIO.write(this.img, "PNG", file);
    }

    public static void netCDFtoPNG(Wrapper netcdf, String path) throws IOException{

        int width = netcdf.getGridWidth();
        int height = netcdf.getGridHeight();

        PNG png = new PNG(width, height);

        //float maxValue = netcdf.getMaxValue();

        //float multiplier = 255/maxValue;
        float[] grid = netcdf.getGrid();
        BufferedImage img = png.getImage();

        int gridIndex = 0;

        for (int y = 0; y < height; y ++){
            for (int x = 0; x < width; x++){


                float gridValue = grid[gridIndex++];
                //gridValue = gridValue > 2 ? 2: gridValue;

                //int color = (int) Math.floor(gridValue * multiplier);
                int color = gridValue > 0 ? 255 : 0;

                //App.write(Integer.toString(color));

                int r = color;
                int g = color;
                int b = color;
                int col = (r << 16) | (g << 8) | b;

                img.setRGB(x, y, col);

            }
        }

        png.save(path);

    }

}
