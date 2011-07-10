/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package task;

import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import static task.Config.*;

/**
 *
 * @author Alexey
 */
public class Bulletin {

    String user;
    String subject;
    String date;
    String city;
    List<String> text = new ArrayList<String>();
    List<String> url = new ArrayList<String>();
    List<String> phone = new ArrayList<String>();
    List<String> mail = new ArrayList<String>();
    List<String> foto = new ArrayList<String>();

    //Заполнение известных данных из xml
    Bulletin(Node node) {
        NodeList tags = node.getChildNodes();
        for (int i = 0; i < tags.getLength(); i++) {
            Node tag = tags.item(i);
            String tagName = tag.getNodeName();
            String tagContent = tag.getTextContent();
            if ("subject".equals(tagName)) {
                setSubject(tagContent);
            }
            if ("user".equals(tagName)) {
                setUser(tagContent);
            }
            if ("text".equals(tagName)) {
                addText(tagContent);
            }
            if ("date".equals(tagName)) {
                setDate(tagContent);
            }
            if ("phone".equals(tagName)) {
                addPhone(tagContent);
            }
            if ("mail".equals(tagName)) {
                addMail(tagContent);
            }
            if ("foto".equals(tagName)) {
                addFoto(tagContent);
            }
        }
    }

    public String getSubject() {
        return subject;
    }

    public String getCity() {
        return city;
    }

    public String getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        StringBuilder s = new StringBuilder();
        Iterator<String> i = text.iterator();
        while (i.hasNext()) {
            s.append(i.next()).append("\n");
        }
        return s.toString();
    }

    public String getPhone() {
        StringBuilder s = new StringBuilder();
        Iterator<String> i = phone.iterator();
        while (i.hasNext()) {
            s.append(i.next()).append("\n");
        }
        return s.toString();
    }

    public String getMail() {
        StringBuilder s = new StringBuilder();
        Iterator<String> i = mail.iterator();
        while (i.hasNext()) {
            s.append(i.next()).append("\n");
        }
        return s.toString();
    }

    public String getFoto() {
        StringBuilder s = new StringBuilder();
        Iterator<String> i = foto.iterator();
        while (i.hasNext()) {
            s.append(i.next()).append("\n");
        }
        return s.toString();
    }

    public String getUrl() {
        StringBuilder s = new StringBuilder();
        Iterator<String> i = url.iterator();
        while (i.hasNext()) {
            s.append(i.next()).append("\n");
        }
        return s.toString();
    }

    public int getPhoneN() {
        return phone.size();
    }

    public int getMailN() {
        return mail.size();
    }

    public int getFotoN() {
        return foto.size();
    }

    public int getTextN() {
        return text.size();
    }

    public int getUrlN() {
        return url.size();
    }

    //Из конца темы вырезается город
    //"Заголовок объявления (Город)"
    public void setSubject(String s) {
        int first = s.lastIndexOf('(');
        int last = s.lastIndexOf(')');
        subject = s.substring(0, first).trim();
        city = s.substring(first + 1, last);
    }

    void setUser(String s) {
        user = s.trim();
    }

    void setDate(String s) {
        date = s.trim();
    }

    //Исходный текст разбивается на абзацы
    //Отдельно вырезаются строки, содержащие URL 
    //FIX: определение URL
    public void addText(String s) {
        String[] temp = s.split("\n");
        StringBuilder par = new StringBuilder();
        String line;

        for (int i = 0; i < temp.length; i++) {
            line = temp[i].trim();

            if (!line.isEmpty()) {
                try {
                    URL u = new URL(line);
                    addUrl(line);
                } catch (MalformedURLException e) {
                    par.append(line).append("\n");
                }
            } else {
                addParagraph(par.toString());
                par = new StringBuilder();
            }
        }
        addParagraph(par.toString());
    }

    //Добавление абзаца текста, слишком маленькие абзацы выкидываются. 
    public void addParagraph(String s) {
        if (s.length() > MIN_PARAGRAPH_SIZE) {
            text.add(s.toString().trim());
        }
    }

    public void addPhone(String s) {
        phone.add(s.trim());
    }

    public void addMail(String s) {
        mail.add(s.trim());
    }

    public void addFoto(String s) {
        foto.add(s.trim());
    }

    public void addUrl(String s) {
        url.add(s.trim());
    }

    //Сравнение авторов
    //Истинно если совпадает имя пользователя, хотя бы один телефон или email
    public boolean compareAuthor(Bulletin bull) {
        Iterator<String> i1;
        Iterator<String> i2;
        String s;

        if (this.user.equals(bull.user)) {;
            return true;
        }

        i1 = this.phone.iterator();
        while (i1.hasNext()) {
            s = i1.next();
            i2 = bull.phone.iterator();
            while (i2.hasNext()) {
                if (i2.next().equalsIgnoreCase(s)) {
                    return true;
                }
            }
        }

        i1 = this.mail.iterator();
        while (i1.hasNext()) {
            s = i1.next();
            i2 = bull.mail.iterator();
            while (i2.hasNext()) {
                if (i2.next().equalsIgnoreCase(s)) {
                    return true;
                }
            }
        }
        return false;
    }

    //Сравнение текстов объявлений
    boolean compareText(Bulletin bull) {
        int length = Math.min(this.getTextN(), bull.getTextN());

        if (length == 0) {
            return false;
        }

        Iterator<String> i1;
        Iterator<String> i2;
        String s;
        int same = 0;

        i1 = this.text.iterator();
        while (i1.hasNext()) {
            s = i1.next();
            i2 = bull.text.iterator();
            while (i2.hasNext()) {
                if (i2.next().equalsIgnoreCase(s)) {
                    same++;
                }
            }
        }

        double rate = (double) same / (double) length;
        if (rate >= SAME_TEXT) {
            return true;
        } else {
            return false;
        }
    }

    //Сравнение фотографий
    boolean compareFoto(Bulletin bull) {
        int length = Math.min(this.getFotoN(), bull.getFotoN());

        if (length == 0) {
            return false;
        }

        Iterator<String> i1;
        Iterator<String> i2;
        String s;
        int same = 0;

        i1 = this.foto.iterator();
        while (i1.hasNext()) {
            s = i1.next();
            i2 = bull.foto.iterator();
            while (i2.hasNext()) {
                if (i2.next().equalsIgnoreCase(s)) {
                    same++;
                }
            }
        }

        double rate = (double) same / (double) length;
        if (rate > SAME_FOTO) {
            return true;
        } else {
            return false;
        }
    }

    //Сравнение ссылок
    boolean compareUrl(Bulletin bull) {
        int length = Math.min(this.getUrlN(), bull.getUrlN());
        if (length == 0) {
            return false;
        }

        Iterator<String> i1;
        Iterator<String> i2;
        String s;
        int same = 0;

        i1 = this.url.iterator();
        while (i1.hasNext()) {
            s = i1.next();
            i2 = bull.url.iterator();
            while (i2.hasNext()) {
                if (i2.next().equalsIgnoreCase(s)) {
                    same++;
                }
            }
        }

        double rate = (double) same / (double) length;
        if (rate > SAME_URL) {
            return true;
        } else {
            return false;
        }
    }

    //Сравнение заголовков
    boolean compareSubject(Bulletin bull) {
        if (this.subject.equalsIgnoreCase(bull.subject)) {
            return true;
        } else {
            return false;
        }
    }

    //Сравнение объявлений
    //Объявления считаются одинаковыми когда совпадают тексты, заголовки или фотографии и ссылки
    public boolean compareBulletin(Bulletin bull) {
        if (compareText(bull)
                || compareSubject(bull)
                || (compareUrl(bull) && compareFoto(bull))) {
            return true;
        } else {
            return false;
        }
    }
}
