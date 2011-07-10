/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package task;

import com.itextpdf.text.Section;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import static task.Config.*;

/**
 *
 * @author Alexey
 */
public class BulletinList {
    
    Font NORMAL;
    Font BOLD;
    Font ITALIC;
    Font SMALLITALIC;
    Font SUBJECT;
    Font TITLE;
	//Множество объявлений
    List<Bulletin> bulletins = new ArrayList<Bulletin>();
    //Множество объявлений, написанных одним автором
    Set<Set<Bulletin>> authors = new HashSet<Set<Bulletin>>();
	//Множество одинаковых объявлений
    Set<Set<Bulletin>> doubles = new HashSet<Set<Bulletin>>();
    
	//Загрузка списка из xml
    BulletinList() {
        try {            
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            org.w3c.dom.Document doc = db.parse(CATALOG_PATH); //Document конфликтует с iText

            NodeList nodes = doc.getElementsByTagName("bulletin");
            
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                Bulletin bull = new Bulletin(node);
                bulletins.add(bull);
            }
            
        } catch (SAXException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Unable to read data from xml");
            System.exit(4);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Unable to read data from xml");
            System.exit(4);
        } catch (ParserConfigurationException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Unable to read data from xml");
            System.exit(4);
        }
    }

    //группировка объявлений по пользователям
    public void findAuthors() {
        //Промежуточный список (с повторами)
        List<Set<Bulletin>> auth = new ArrayList<Set<Bulletin>>();
        
        Iterator i = bulletins.iterator();
        while (i.hasNext()) {
            Set newSet = new HashSet();
            newSet.add(i.next());
            auth.add(newSet);
        }
        
        ListIterator<Set<Bulletin>> i1;
        ListIterator<Set<Bulletin>> i2;
        Set set1;
        Set set2;
        
        i1 = auth.listIterator();
        while (i1.hasNext()) {
            set1 = i1.next();
            i2 = auth.listIterator(i1.nextIndex());
            while (i2.hasNext()) {
                set2 = i2.next();
                if (compareAuthorsSet(set1, set2)) {
                    set1.addAll(set2);
                    i2.set(set1);
                }
            }
        }
        
        i1 = auth.listIterator();
        while (i1.hasNext()) {
            set1 = i1.next();
            if (set1.size() > 1) {
                authors.add(set1);
            }
        }
    }

    //Поиск объявлений одного автора во множествах
    boolean compareAuthorsSet(Set<Bulletin> set1, Set<Bulletin> set2) {
        Iterator<Bulletin> i1;
        Iterator<Bulletin> i2;
        Bulletin bull1;
        Bulletin bull2;
        
        i1 = set1.iterator();
        while (i1.hasNext()) {
            bull1 = i1.next();
            i2 = set2.iterator();
            while (i2.hasNext()) {
                bull2 = i2.next();
                if (bull1.compareAuthor(bull2)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Поиск одинаковых объявлений одинакового авторства
    public void findDoubles() {
        Iterator<Set<Bulletin>> i;
        
        i = authors.iterator();
        while (i.hasNext()) {
            findDoublesAuthor(i.next());
        }
    }
    
	
    void findDoublesAuthor(Set<Bulletin> set) {
        List<Set<Bulletin>> doub = new ArrayList<Set<Bulletin>>();
        
        Iterator i = set.iterator();
        while (i.hasNext()) {
            Set newSet = new HashSet();
            newSet.add(i.next());
            doub.add(newSet);
        }
        
        ListIterator<Set<Bulletin>> i1;
        ListIterator<Set<Bulletin>> i2;
        Set set1;
        Set set2;
        
        i1 = doub.listIterator();
        while (i1.hasNext()) {
            set1 = i1.next();
            i2 = doub.listIterator(i1.nextIndex());
            while (i2.hasNext()) {
                set2 = i2.next();
                if (compareDoublesSet(set1, set2)) {
                    set1.addAll(set2);
                    i2.set(set1);
                }
            }
        }
        
        i1 = doub.listIterator();
        while (i1.hasNext()) {
            set1 = i1.next();
            if (set1.size() > 1) {
                doubles.add(set1);
            }
        }
    }
    
    boolean compareDoublesSet(Set<Bulletin> set1, Set<Bulletin> set2) {
        Iterator<Bulletin> i1;
        Iterator<Bulletin> i2;
        Bulletin bull1;
        Bulletin bull2;
        
        i1 = set1.iterator();
        while (i1.hasNext()) {
            bull1 = i1.next();
            i2 = set2.iterator();
            while (i2.hasNext()) {
                bull2 = i2.next();
                if (bull1.compareBulletin(bull2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
	//создание отчета
    void report() {
        try {
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(REPORT_PATH));
			//Загрузка шрифта, поддерживающего кириллицу
            BaseFont bf = BaseFont.createFont(FONT_PATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            
            NORMAL = new Font(bf, 12);
            BOLD = new Font(bf, 12, Font.BOLD);
            ITALIC = new Font(bf, 12, Font.ITALIC);
            SUBJECT = new Font(bf, 14, Font.BOLD);
            SMALLITALIC = new Font(bf, 10, Font.ITALIC);
            TITLE = new Font(bf, 16, Font.BOLD);
            
            document.open();
            
            document.addTitle("Дублирующиеся объявления на farpost.ru");
            document.addAuthor("Алексей Пивоваров");
            document.addCreator("Алексей Пивоваров");
            
            document.add(new Paragraph("Дублирующиеся объявления:", TITLE));
                        
            int chapter = 1;
            Iterator<Set<Bulletin>> i1 = doubles.iterator();
            while (i1.hasNext()) { //серии дублей               
                Set<Bulletin> set = i1.next();
                Chapter ch = new Chapter(chapter);
                Iterator<Bulletin> i2 = set.iterator();
                while (i2.hasNext()) { //дубли
                    printBulletin(i2.next(), ch);
                }
                if (chapter == 1) {
                    ch.setTriggerNewPage(false);
                }
                document.add(ch);
                chapter++;
            }
            
            document.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Unable to create report");
            System.exit(3);
        } catch (DocumentException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Unable to create report");
            System.exit(3);
        }
    }
    
	//Вывод объявления
    public void printBulletin(Bulletin bull, Chapter ch) {
        Section s;
        Paragraph temp;
        Iterator<String> i;
        
        temp = new Paragraph(bull.getSubject(), SUBJECT);
        temp.setSpacingAfter(6);
        temp.setSpacingBefore(24);
        s = ch.addSection(temp);
        
        temp = new Paragraph();
        temp.add(new Phrase(bull.getUser(), BOLD));
        temp.add(new Phrase(", ", ITALIC));
        temp.add(new Phrase(bull.getCity(), ITALIC));
        if (!bull.getDate().isEmpty()) {
            temp.add(new Phrase(" (", SMALLITALIC));
            temp.add(new Phrase(bull.getDate(), SMALLITALIC));
            temp.add(new Phrase(")", SMALLITALIC));
        }
        s.add(temp);
        
        if ((bull.getPhoneN() > 0) || (bull.getMailN() > 0)) {
            temp = new Paragraph();
            temp.add(new Phrase(bull.getPhone(), ITALIC));
            temp.add(new Phrase(bull.getMail(), ITALIC));
            temp.setSpacingAfter(6);
            s.add(temp);
        }
        
        if (bull.getTextN() > 0) {
            temp = new Paragraph();
            i = bull.text.iterator();
            while (i.hasNext()) {
                temp.add(new Paragraph(i.next(), NORMAL));
            }
            temp.setSpacingAfter(6);
            s.add(temp);
        }
        
        if ((bull.getUrlN() > 0)) {
            temp = new Paragraph();
            temp.add(new Phrase(bull.getUrl(), ITALIC));
            temp.setSpacingAfter(6);
            s.add(temp);
        }
    }
}
