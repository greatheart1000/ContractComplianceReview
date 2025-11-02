package com.isoftstone.compliance.chat.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * Word文件解析
 */
public class WordContentAnalysisUtil {
	
	public static void main(String[] args) {
        String filePath = "D:\\docs\\111.docx";
        try {
        	byte[] bytes = Files.readAllBytes(Paths.get(filePath));
            String content = extractContent(bytes);
            System.out.println(content);
		} catch (Exception e) {
			// TODO: handle exception
		}
    }
	
	/**
	 * 解析文件内容
	 * @param bytes
	 * @return
	 */
	public static String extractContent(byte[] bytes) {
		String contentText = null;
		InputStream inputStream = null;
		try {
			inputStream = new ByteArrayInputStream(bytes);
			XWPFDocument document = new XWPFDocument(inputStream);
			XWPFWordExtractor extractor = new XWPFWordExtractor(document);
			contentText = extractor.getText();
		} catch (Exception e) {
			contentText = null;
		} finally {
			try {
				if(inputStream!=null) {
					inputStream.close();
				}
			} catch (Exception e2) {}
		}
		return contentText;
    } 
    

}
