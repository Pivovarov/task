/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package task;

/**
 *
 * @author Alexey
 */
public class Config {
    final static String WORK_DIR = "work/";
    final static String CONFIG_DIR = "config/";
    final static String WEBHARVEST_CONFIG_FILE = "farpost.xml";
    final static String CATALOG_FILE = "catalog.xml";
    final static String REPORT_FILE = "report.pdf";
    final static String FONT_FILE = "tahoma.ttf";
    final static String WEBHARVEST_CONFIG_PATH = CONFIG_DIR + WEBHARVEST_CONFIG_FILE;
    final static String CATALOG_PATH = WORK_DIR + CATALOG_FILE;
    final static String REPORT_PATH = WORK_DIR + REPORT_FILE;
    final static String FONT_PATH = CONFIG_DIR + FONT_FILE;
    //Bulletins
    final static int MIN_PARAGRAPH_SIZE = 5;
    //Коэф. совпадения, после которого объявления считаются совпавшими
    final static double SAME_TEXT = 0.6;
    final static double SAME_FOTO = 0.6;
    final static double SAME_URL = 0.6;
}
