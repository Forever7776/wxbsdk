package com.wxb.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

/*
 * 图片添加水印
 * liuyj
 */
public class WaterMarkUtils {

	/**
	 * 给图片添加水印
	 * 
	 * @param srcImgPath
	 *            需要添加水印的图片的路径
	 * @param watermarkStr
	 *            水印的文字
	 * @param markContentColor
	 *            水印文字的颜色
	 * @param qualNum
	 *            图片质量
	 * @return
	 */
	public void mark(String srcImgPath, String outImgPath, String watermarkStr) {
		try {
			// 读取原图片信息
			File srcImgFile = new File(srcImgPath);
			Image srcImg = ImageIO.read(srcImgFile);
			int srcImgWidth = srcImg.getWidth(null);
			int srcImgHeight = srcImg.getHeight(null);
			// 加水印
			BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bufImg.createGraphics();

			g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);

//			Font font = new Font("微软雅黑", Font.PLAIN, 18);
			Font font = new Font("Algerian", Font.ITALIC, 100);
			g.setColor(Color.white); // 根据图片的背景设置水印颜色
			
			g.setFont(font);
			int x = (srcImgWidth - getWatermarkLength(watermarkStr, g)) / 1;
			int y = srcImgHeight / 1;
			
	
			//------------------------------------------------------------------
			FontMetrics fm = g.getFontMetrics(font);
			 //设置换行操作
            int fontHeight = fm.getHeight(); //字符的高度
			int offsetLeft = 30;  
            int rowIndex = 12; 
			 for(int i=0;i<watermarkStr.length();i++){  
	                char c = watermarkStr.charAt(i);  
	                int charWidth = fm.charWidth(c); //字符的宽度  
	                //另起一行  
	                if(Character.isISOControl(c) || offsetLeft >= (srcImgWidth-charWidth)){  
	                    rowIndex++;  
	                    offsetLeft = 16;  
	                }  
	                g.drawString(String.valueOf(c), offsetLeft, rowIndex * fontHeight);   //把一个个写到图片上 
	                offsetLeft += charWidth;   //设置下字符的间距
	            }
			//------------------------------------------------------------------

            
		//	g.drawString(watermarkStr, x+10, y-5);
			
			
			g.dispose();
			// 输出图片
			FileOutputStream outImgStream = new FileOutputStream(outImgPath);
			ImageIO.write(bufImg, "jpg", outImgStream);
			outImgStream.flush();
			outImgStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获取水印文字总长度
	public int getWatermarkLength(String str, Graphics2D g) {
		return g.getFontMetrics(g.getFont()).charsWidth(str.toCharArray(), 0,
				str.length());
	}
}
