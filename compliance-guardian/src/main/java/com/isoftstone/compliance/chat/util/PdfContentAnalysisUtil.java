package com.isoftstone.compliance.chat.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PdfContentAnalysisUtil {
	
	private static Logger logger = LoggerFactory.getLogger(PdfContentAnalysisUtil.class);
	
	public static void main(String[] args) {
		String filePath = "D:\\docs\\111.pdf";
		try {
			byte[] bytes = Files.readAllBytes(Paths.get(filePath));
			String info = parsingText(bytes);
			logger.info(info);
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * 获取订单基本信息
	 * @param input
	 * @return
	 */
	private static String parsingText(byte[] bytes) {
		String contentText = null;
		PDDocument document = null;
		try {
			//加载一个pdf对象
			document = Loader.loadPDF(bytes);
            //获取一个PDFTextStripper文本剥离对象  
            PDFTextStripper textStripper = new PDFTextStripper();
            contentText = textStripper.getText(document);
            
            PDPageTree pages = document.getPages();
            for (PDPage pdPage : pages) {
            	PDResources resources = pdPage.getResources();
            	System.out.println(resources.getXObjectNames());
            	
			}
            
            
            if(StringUtils.isEmpty(contentText)) {
            	return null;
            }
		} catch (IOException e) {
			logger.error("读取PDF错误:{}",e.getMessage());
		} finally {
        	if(document!=null) {
        		//关闭文档
                try {
                	document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
        	}
		}
		return contentText;
	}
	
}
