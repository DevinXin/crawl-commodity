package com.devinxin.crawl.controller;

import com.alibaba.fastjson.JSONObject;
import com.devinxin.crawl.utils.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 爬取天猫商品
 *
 * @author devinxin
 * @date 2019/09/19
 */
@RestController
public class CrawlCommodityContoller {

    @Value("${cookie}")
    private String cookie;

    @RequestMapping(value="/crawlCommodity/{id}",method=RequestMethod.GET)
    public JSONObject crawlCommodity(@PathVariable String id){
        String url = "https://detail.tmall.com/item.htm?id="+id;
        Document document = null;
        String result = null;
        JSONObject jbRes = new JSONObject();
        try {
            document = Jsoup.connect(url).header("cookie",cookie).get();
            Elements elements = document.getElementById("detail").select("script");
            List<Element> elementsList = elements.stream().filter(e->e.toString().contains("TShop.poc")).collect(Collectors.toList());
            String data = elementsList.get(0).data().split("TShop.Setup\\(")[1].trim().split("\\);")[0].trim();
            JSONObject jb = JSONObject.parseObject(data);
            String descUrl = "https:"+jb.getJSONObject("api").getString("httpsDescUrl");
            result = HttpClientUtil.doGetWithCookie(descUrl,cookie);
            jbRes.put("valItemInfo",jb.get("valItemInfo"));
            jbRes.put("detail",jb.get("detail"));
            jbRes.put("itemDO",jb.get("itemDO"));
            jbRes.put("propertyPics",jb.get("propertyPics"));
            jbRes.put("desc",result.substring(10,result.length()-3));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jbRes;
    }

}
