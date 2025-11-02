package com.isoftstone.compliance.chat.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.coobird.thumbnailator.Thumbnails;

/**
 * 图片压缩工具
 */
public class ImageCompressUtil {
	
	private static Logger logger = LoggerFactory.getLogger(ImageCompressUtil.class);

	public static void main(String[] args) {
		try {
            String inputPath = "D:\\docs\\20250509154206.png";
            int targetSizeKB = 100;
            String path = compressImageToSize(inputPath, targetSizeKB);
            logger.info(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	/**
     * 压缩图片到指定大小（KB）
     * @param inputPath  输入图片路径
     * @param outputPath 输出图片路径
     * @param targetSizeKB 目标大小（KB）
     */
    public static String compressImageToSize(String inputPath, long targetSizeKB) {
    	String outputPath = "";
        try {
        	String path = inputPath.substring(0,inputPath.lastIndexOf("."));
        	outputPath = path+"_"+targetSizeKB+".jpg";
        	File inputFile = new File(inputPath);
            File outputFile = new File(outputPath);
            // 初始质量
            float quality = 0.9f;
            // 最小质量
            float minQuality = 0.3f;
            // 最大质量
            float maxQuality = 1.0f;
        	// 初始压缩
            compressWithQuality(inputFile, outputFile, quality);
            long currentSizeKB = outputFile.length() / 1024;
            // 二分法调整质量
            while (Math.abs(currentSizeKB - targetSizeKB) > 10 && maxQuality - minQuality > 0.01) {
                if (currentSizeKB > targetSizeKB) {
                    maxQuality = quality;
                } else {
                    minQuality = quality;
                }
                quality = (minQuality + maxQuality) / 2;
                compressWithQuality(inputFile, outputFile, quality);
                currentSizeKB = outputFile.length() / 1024;
            }
		} catch (Exception e) {
			outputPath = inputPath;
		}
        if(StringUtils.isEmpty(outputPath)) {
        	outputPath = inputPath;
        }
        return outputPath;
    }
 
    /**
     * 执行压缩
     * @param inputFile
     * @param outputFile
     * @param quality
     * @throws IOException
     */
    private static void compressWithQuality(File inputFile, File outputFile, float quality) throws IOException {
        Thumbnails.of(inputFile)
        		.scale(0.7f)
                .outputQuality(0.5)
                .outputFormat("jpg")
                .toFile(outputFile);
    }

}
