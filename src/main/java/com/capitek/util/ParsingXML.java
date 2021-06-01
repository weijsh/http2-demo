package com.capitek.util;

import com.capitek.entity.Vendor;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.util.*;

public class ParsingXML {

    public static void main(String[] args) throws IOException, DocumentException {
        getFormatLineDOM();
    }
    public static void getFormatLineDOM(){
        //1.创建DocumentBuilderFactory对象
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //2.创建DocumentBuilder对象
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File("F:\\svn\\专网\\trunk\\services\\prinetsys\\src\\main\\resources\\RadiusAttribute.xml");
            BufferedReader br= new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            InputSource is = new InputSource(br);
            org.w3c.dom.Document d = builder.parse(is);
            NodeList sList = d.getElementsByTagName("Vendor");
            node(sList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void node(NodeList list){
        for (int i = 0; i <list.getLength() ; i++) {
            Node node = list.item(i);
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j <childNodes.getLength() ; j++) {
                if (childNodes.item(j).getNodeType()==Node.ELEMENT_NODE) {
                    System.out.print(childNodes.item(j).getNodeName() + ":");
                    System.out.println(childNodes.item(j).getFirstChild().getNodeValue());
                }
            }
        }
    }





    public static void getFormatLineSAX() throws IOException, DocumentException {
        String file = "F:\\svn\\专网\\trunk\\services\\prinetsys\\src\\main\\resources\\RadiusAttribute.xml";
        List<Vendor> list = new ArrayList<>();
        Document document = getXmlDocument(file);
        Element element = document.getRootElement();//RadiusAttribute
        int count =1;
        for(Iterator iterator = element.elementIterator(); iterator.hasNext();){
            Element element2 = (Element)iterator.next();//Vendor
            if(count==10){
                for(Iterator iterator2 = element2.elementIterator(); iterator2.hasNext();){
                    Element element3 = (Element)iterator2.next();//Remark,ID,Attributes
                    if(element3.getName().equals("Attributes")){
                        for(Iterator iterator3 = element3.elementIterator(); iterator3.hasNext();){
                            Element element4 = (Element)iterator3.next();//Attribute
                            Vendor vendor = new Vendor();
                            for(Iterator iterator4 = element4.elementIterator(); iterator4.hasNext();){
                                Element element5 = (Element)iterator4.next();//Remark,Type,Encode,Kind,Values
                                if(element5.getName().equals("Remark")){
                                    vendor.setRemark(element5.getText());
                                }else if(element5.getName().equals("Encode")){
                                    vendor.setEncode(element5.getText());
                                }else if(element5.getName().equals("Type")){
                                    vendor.setType(element5.getText());
                                }
                            }
                            list.add(vendor);
                        }
                    }
                }
            }
            count++;
        }
        Collections.sort(list, new Comparator<Vendor>() {
            @Override
            public int compare(Vendor o1, Vendor o2) {
                return Integer.parseInt(o1.getEncode())-Integer.parseInt(o2.getEncode());
            }
        });
        for(Vendor vendor:list){
            System.out.println("VENDORATTR\t10415\t"+vendor.getRemark()+"\t"+ vendor.getEncode()+"\t"+vendor.getType());
        }
    }


    public static Document getXmlDocument(String xmlPath) throws DocumentException, IOException {
        File file = null;
        if((file = loadFile(xmlPath,false))!=null){
            SAXReader reader = new SAXReader();
            Document document = reader.read(file);
            return document;
        }else {
            throw new FileNotFoundException();
        }
    }
    public static File loadFile(String path, boolean flag) throws IOException{
        if(null == path || "".equals(path)){
            return null;
        }
        File file = new File(path);
        if(file.exists()){
            return file;
        }else {
            if(flag){
                //如果父文件夹不存在，创建父文件夹
                if(!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                return file;
            }else {
                throw new FileNotFoundException();
            }
        }
    }
}
