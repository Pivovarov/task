/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package task;

import java.io.File;
import java.io.FileNotFoundException;
import org.webharvest.definition.ScraperConfiguration;
import org.webharvest.runtime.Scraper;
import static task.Config.*;

/**
 *
 * @author Alexey
 */
public class Main {

    //Сбор данных с сайта
    public static void collectData(String pathURL, int loops) {
        File f = new File(WORK_DIR);
        if (!f.exists()) {
            f.mkdirs();
        }

        try {
            ScraperConfiguration conf = new ScraperConfiguration(WEBHARVEST_CONFIG_PATH);
            Scraper scraper = new Scraper(conf, WORK_DIR);

            scraper.addVariableToContext("outputFile", CATALOG_FILE);
            scraper.addVariableToContext("pageURL", pathURL);
            scraper.addVariableToContext("maxLoops", loops);

            scraper.execute();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Unable to collect data from URL");
            System.exit(2);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            collectData("http://baza.farpost.ru/internet/?city=0", 10);
        }
        if (args.length == 1) {
            collectData(args[0], 10);
        }
        if (args.length == 2) {
            collectData(args[0], Integer.parseInt(args[1]));
        }

        BulletinList bulletins = new BulletinList();
        bulletins.findAuthors();
        bulletins.findDoubles();
        bulletins.report();
        System.out.println("Complete!");
    }
}
