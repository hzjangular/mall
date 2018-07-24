package com.imooc.crawler.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.imooc.crawler.entity.ImoocCourse;

@Deprecated
@Slf4j
public class ExcelUtil {

	private final String STORE_PATH;
	private HSSFWorkbook workBook; //Excel工作簿
	private HSSFSheet workSheet; //Excel工作表
	private final String DEFAULT_EXCEL_FILE_NAME = "courses.xls"; //默认Excel文件名
	private final String EXCEL_FILE_NAME;
	private final File EXCEL_FILE;
	
	private ExcelUtil(String storeDir, String excelFileName){
		this.STORE_PATH = storeDir;
		this.EXCEL_FILE_NAME = (StringUtils.isEmpty(excelFileName)) ? DEFAULT_EXCEL_FILE_NAME : 
			FileUtil.parseExcelExt(FileUtil.removeIlleagalCharactersInFileName(excelFileName));
		this.EXCEL_FILE = getExcelFile();
		this.workBook = createWorkBook();
		this.workSheet = createWorkSheetAndAddHeader();
		log.info("Excel文件保存路径为:" + EXCEL_FILE.getAbsolutePath());
	}
	
	public static ExcelUtil getInstance(String storeDir, String excelFileName) {
		return new ExcelUtil(storeDir, excelFileName);
	}
	
	/**
	 * 获取存储的文件夹的路径
	 * @return 文件夹的路径
	 */
	private String getStorePath() {
		return FileUtil.createDir(STORE_PATH);
	}
	
	/**
	 * 获取Excel文件
	 * @return Excel文件
	 */
	private File getExcelFile() {
		String excelFilePath = getStorePath().concat(EXCEL_FILE_NAME);
		return new File(excelFilePath);
	}

	/**
	 * 保存到Excel
	 * @param courseList 要保存的课程列表
	 * @return 保存结果 true 成功, false 失败
	 */
	public boolean writeToExcel(List<ImoocCourse> courseList) {
		return addContentToWorkSheet(courseList);
	}
	
	/**
	 * 创建Excel工作簿
	 * @return
	 */
	private HSSFWorkbook createWorkBook() {
		//如果Excel文件已存在则删除
		if(EXCEL_FILE.exists()) {
			boolean deleteResult = EXCEL_FILE.delete();
			if(!deleteResult) {
				throw new RuntimeException("删除 " + EXCEL_FILE.getAbsolutePath() + " 失败");
			}
		}
		return new HSSFWorkbook();
	}
	
	/**
	 * 创建Excel工作表并添加表头
	 * @return
	 */
	private HSSFSheet createWorkSheetAndAddHeader() {
		workSheet = workBook.createSheet(); //创建工作表
		workBook.setSheetName(0, "慕课网Java课程信息"); //设置工作表的名称
		HSSFRow headerRow = workSheet.createRow(0); //第一列，表头
		HSSFCell cell = headerRow.createCell(0); //创表头建单元格，并填充内容
		cell.setCellValue("课程名称");
		cell = headerRow.createCell(1);
		cell.setCellValue("课程图片URL");
		cell = headerRow.createCell(2);
		cell.setCellValue("课程等级");
		cell = headerRow.createCell(3);
		cell.setCellValue("课程标签");
		cell = headerRow.createCell(4);
		cell.setCellValue("课程简介");
		cell = headerRow.createCell(5);
		cell.setCellValue("学习人数");
		cell = headerRow.createCell(6);
		cell.setCellValue("课程连接");
		return workSheet;
	}
	
	/**
	 * 向Excel工作表中添加数据
	 * @param courseList
	 * @return
	 */
	private boolean addContentToWorkSheet(List<ImoocCourse> courseList) {
		int rowsInserted = 0;
		int courseLength = courseList.size();
		HSSFRow contentRow; //内容行
		//循环添加行
		for(ImoocCourse course : courseList) {
			contentRow = workSheet.createRow(rowsInserted + 1);
			course = courseList.get(rowsInserted);
			contentRow.createCell(0).setCellValue(course.getCourseName());
			contentRow.createCell(1).setCellValue(course.getImgSrc());
			contentRow.createCell(2).setCellValue(course.getCourseLevel());
			contentRow.createCell(3).setCellValue(StringUtils.replaceAll(course.getCourseLabels().toString(), "[\\[\\]]", ""));
			contentRow.createCell(4).setCellValue(course.getCourseDesc());
			contentRow.createCell(5).setCellValue(course.getStudyNum());
			contentRow.createCell(6).setCellValue(course.getCourseURL());
			rowsInserted++;
		}
		try {
			workBook.write(EXCEL_FILE); //将数据写入Excel文件
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} 
		return rowsInserted == courseLength;
	}
	
}
