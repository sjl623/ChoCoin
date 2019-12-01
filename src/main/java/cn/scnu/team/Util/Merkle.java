package cn.scnu.team.Util;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

public class Merkle {
    List<String> info = new ArrayList<String>();
    List<List<String>> tree = new ArrayList<List<String>>();
    int len = 0;

    public void add(String str) {
        info.add(str);
    }

    public void build() {
        int len = 0;
        while (tree.size() == 0 || tree.get(tree.size() - 1).size() != 1) {
            List<String> nowLayer = new ArrayList<String>();
            if (tree.size() == 0) {
                for (int i = 0; i < info.size(); i++) nowLayer.add(Hash.sha256(info.get(i)));
            } else {
                int lastLayerID = tree.size() - 1;
                List<String> lastLayer = tree.get(lastLayerID);
                for (int i = 0; i + 1 < lastLayer.size(); i += 2) {
                    nowLayer.add(Hash.sha256(lastLayer.get(i) + lastLayer.get(i + 1)));
                }
                if (lastLayer.size() % 2 == 1) {
                    nowLayer.add(Hash.sha256(lastLayer.get(lastLayer.size() - 1) + lastLayer.get(lastLayer.size() - 1)));
                }
            }
            tree.add(nowLayer);
            len++;
        }
    }

    public void output(){
        System.out.println(JSON.toJSONString(tree));
    }

}
