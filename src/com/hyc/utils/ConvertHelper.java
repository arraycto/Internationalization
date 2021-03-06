package com.hyc.utils;

import com.hyc.net.Api;
import com.hyc.bean.Result;
import com.intellij.psi.xml.XmlDocument;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import retrofit2.Response;

import java.io.IOException;

public class ConvertHelper {

    private XmlFile file;

    public ConvertHelper(XmlFile file) {
        this.file = file;
    }

    public XmlDocument convert(String targetCode) {
        XmlDocument document = file.getDocument();
        if (document == null) return null;
        document = (XmlDocument) document.copy();
        if (document == null || document.getFirstChild() == null) {
            return document;
        }
        Api api = RetrofitUtil.getInstance().create(Api.class);
        XmlTag tag = document.getRootTag();
        for (XmlTag subTag : tag.getSubTags()) {
//                String name = subTag.getAttributeValue("name");
            if (subTag.getName().equals("string")) {
                getTranslate(api, subTag, targetCode);
            } else if (subTag.getName().equals("string-array")) {
                for (XmlTag itemTag : subTag.getSubTags()) {
                    getTranslate(api, itemTag, targetCode);
                }
            }

        }
        return document;
    }


    public void getTranslate(Api api, XmlTag subTag, String targetCode) {
        String value = subTag.getValue().getText();
        try {
            Response<Result> response = api.loadResult(CommonUtil.getParams(value, targetCode)).execute();
            if (response != null && response.body() != null && response.body().getTranslation() != null && response.body().getTranslation().size() > 0) {
                String data = response.body().getTranslation().get(0);
                System.out.println("翻译成功: " + value + "  结果:" + data + "\n");
                subTag.getValue().setText(data);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


}
