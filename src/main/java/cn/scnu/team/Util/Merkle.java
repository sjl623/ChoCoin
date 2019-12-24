package cn.scnu.team.Util;

import com.alibaba.fastjson.JSON;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.List;

public class Merkle {
    public List<String> info = new ArrayList<String>();
    public List<List<String>> tree = new ArrayList<List<String>>();

    public void add(String str) {
        info.add(str);
    }

    public void build() {//构造默克尔树
        if (info.size() == 0) {
            List<String> nowLayer = new ArrayList<String>();
            nowLayer.add(Hash.sha256("a"));//若内容为空则进行填充
            tree.add(nowLayer);
            return;
        }
        while (tree.size() == 0 || tree.get(tree.size() - 1).size() != 1) {
            List<String> nowLayer = new ArrayList<String>();
            if (tree.size() == 0) {
                for (String s : info) nowLayer.add(Hash.sha256(s));//第一层
            } else {
                int lastLayerID = tree.size() - 1;
                List<String> lastLayer = tree.get(lastLayerID);
                for (int i = 0; i + 1 < lastLayer.size(); i += 2) {
                    nowLayer.add(Hash.sha256(lastLayer.get(i) + lastLayer.get(i + 1)));
                }
                if (lastLayer.size() % 2 == 1) {
                    nowLayer.add(Hash.sha256(lastLayer.get(lastLayer.size() - 1) + lastLayer.get(lastLayer.size() - 1)));//节点数量为偶数时进行填充
                }
            }
            tree.add(nowLayer);
        }
    }

    public void output() {
        System.out.println(JSON.toJSONString(tree));
    }

}
