package com.bjpowernode;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

/**
 * ClassName:Test
 * Package:com.bjpowernode
 * Description
 *
 * @Date:2020/3/1621:30
 * @author:xyh
 */
public class Test {


    public static void main(String[] args) throws DocumentException {
        String resultXML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
                "\n" +
                "<bookstore>\n" +
                "\n" +
                "<book category=\"COOKING\">\n" +
                "  <title lang=\"en\">Everyday Italian</title>\n" +
                "  <author>Giada De Laurentiis</author>\n" +
                "  <year>2005</year>\n" +
                "  <price>30.00</price>\n" +
                "</book>\n" +
                "\n" +
                "<book category=\"CHILDREN\">\n" +
                "  <title lang=\"en\">Harry Potter</title>\n" +
                "  <author>J K. Rowling</author>\n" +
                "  <year>2005</year>\n" +
                "  <price>29.99</price>\n" +
                "</book>\n" +
                "\n" +
                "<book category=\"WEB\">\n" +
                "  <title lang=\"en\">XQuery Kick Start</title>\n" +
                "  <author>James McGovern</author>\n" +
                "  <author>Per Bothner</author>\n" +
                "  <author>Kurt Cagle</author>\n" +
                "  <author>James Linn</author>\n" +
                "  <author>Vaidyanathan Nagarajan</author>\n" +
                "  <year>2003</year>\n" +
                "  <price>49.99</price>\n" +
                "</book>\n" +
                "\n" +
                "<book category=\"WEB\">\n" +
                "  <title lang=\"en\">Learning XML</title>\n" +
                "  <author>Erik T. Ray</author>\n" +
                "  <year>2003</year>\n" +
                "  <price>39.95</price>\n" +
                "</book>\n" +
                "\n" +
                "</bookstore>";
        //将xml格式的字符串转换为document对象
        Document document = DocumentHelper.parseText(resultXML);
        Node node = document.selectSingleNode("/bookstore/book[3]/title");
        Node node1 = document.selectSingleNode("/bookstore/book[4]/price");
        String text = node.getText();
        String text1 = node1.getText();
        System.out.println(text);
        System.out.println(text1);
    }
}
