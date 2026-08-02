package demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

/**
 * 决策控制器 - 按类别随机返回一个推荐结果
 */
@RestController
@CrossOrigin(origins = "http://localhost:5173") // 仅允许本地 Vue 开发页面跨域访问
public class CardController {

    // 预设的决策数据列表
    private static final List<DecisionCard> CARDS = List.of(
        // ===== 火锅/川菜 =====
        new DecisionCard("美食", "麻辣火锅", "今天来点热辣的体验，刺激味蕾！"),
        new DecisionCard("美食", "番茄牛腩锅", "酸甜浓郁，暖胃又暖心。"),
        new DecisionCard("美食", "串串香", "撸串的快乐，一根接一根停不下来。"),
        new DecisionCard("美食", "酸菜鱼", "酸爽开胃，鱼肉嫩滑，下饭神器。"),
        new DecisionCard("美食", "毛血旺", "麻辣鲜香，料多实在，过瘾！"),
        new DecisionCard("美食", "水煮牛肉", "嫩滑牛肉，麻辣汤汁，米饭杀手。"),
        new DecisionCard("美食", "麻辣烫", "想吃什么夹什么，自由搭配！"),

        // ===== 日韩料理 =====
        new DecisionCard("美食", "日式拉面", "浓郁骨汤，暖心暖胃的选择。"),
        new DecisionCard("美食", "寿司拼盘", "精致日料，清爽健康之选。"),
        new DecisionCard("美食", "鳗鱼饭", "香嫩鳗鱼，酱汁渗透每一粒米饭。"),
        new DecisionCard("美食", "天妇罗定食", "外酥里嫩，搭配酱汁完美。"),
        new DecisionCard("美食", "韩式炸鸡", "酥脆外皮，甜辣酱汁，配啤酒绝了！"),
        new DecisionCard("美食", "石锅拌饭", "滋滋作响，锅巴焦香，拌出幸福感。"),
        new DecisionCard("美食", "寿喜烧", "甜咸酱汁涮牛肉，蘸生蛋液超满足。"),

        // ===== 西餐/快餐 =====
        new DecisionCard("美食", "披萨", "芝士就是力量，快乐加倍！"),
        new DecisionCard("美食", "意大利肉酱面", "经典番茄肉酱，每一口都浓郁。"),
        new DecisionCard("美食", "汉堡套餐", "多汁牛肉饼，搭配薯条冰可乐，爽！"),
        new DecisionCard("美食", "牛排", "五分熟刚刚好，肉汁四溢。"),
        new DecisionCard("美食", "奶油蘑菇汤", "细腻顺滑，配烤面包片超赞。"),

        // ===== 中式快餐/小吃 =====
        new DecisionCard("美食", "黄焖鸡米饭", "国民快餐，简单又满足。"),
        new DecisionCard("美食", "兰州拉面", "一清二白三红四绿五黄，汤清面筋道。"),
        new DecisionCard("美食", "沙县小吃", "扁食拌面炖罐，经济实惠又好吃。"),
        new DecisionCard("美食", "煎饼果子", "薄脆香酥，早餐首选！"),
        new DecisionCard("美食", "生煎包", "底部焦脆，肉馅多汁，小心烫嘴。"),
        new DecisionCard("美食", "小笼包", "轻轻提慢慢移，先开窗后喝汤。"),

        // ===== 烧烤/夜宵 =====
        new DecisionCard("美食", "羊肉串", "炭火炙烤，孜然飘香，撸串的快乐！"),
        new DecisionCard("美食", "烤鱼", "焦香鱼皮，嫩滑鱼肉，越煮越入味。"),
        new DecisionCard("美食", "蒜蓉生蚝", "鲜嫩肥美，蒜香四溢。"),
        new DecisionCard("美食", "小龙虾", "麻辣十三香，剥壳吮指停不下来！"),

        // ===== 甜品/饮品 =====
        new DecisionCard("美食", "珍珠奶茶", "Q弹珍珠，香浓奶茶，快乐源泉！"),
        new DecisionCard("美食", "提拉米苏", "咖啡与奶油的完美邂逅，带我走吧。"),
        new DecisionCard("美食", "芒果糯米饭", "椰香糯米配甜芒果，热带风情。"),
        new DecisionCard("美食", "冰淇淋", "炎炎夏日，来一球冰凉甜蜜。"),
        new DecisionCard("美食", "杨枝甘露", "芒果西柚西米露，清甜解暑。"),

        // ===== 家常菜 =====
        new DecisionCard("美食", "红烧肉", "肥而不腻，入口即化，下饭神器。"),
        new DecisionCard("美食", "糖醋排骨", "酸甜可口，外酥里嫩。"),
        new DecisionCard("美食", "宫保鸡丁", "花生脆鸡丁嫩，甜辣交织。"),
        new DecisionCard("美食", "回锅肉", "肥瘦相间，焦香十足，川菜经典。"),
        new DecisionCard("美食", "地三鲜", "土豆茄子青椒，素菜也惊艳。"),

        // ===== 娱乐 =====
        new DecisionCard("娱乐", "看一部电影", "挑一部收藏很久的电影，给自己两个小时放松一下。"),
        new DecisionCard("娱乐", "玩合作游戏", "叫上朋友开一局，输赢不重要，快乐最重要。"),
        new DecisionCard("娱乐", "来一局桌游", "面对面动动脑筋，也顺便增进一下感情。"),
        new DecisionCard("娱乐", "去唱 KTV", "选几首熟悉的歌，把今天的压力唱出去。"),
        new DecisionCard("娱乐", "散步听歌", "戴上耳机走一走，让大脑暂时放空。"),
        new DecisionCard("娱乐", "骑行兜风", "选一条安全的路线，感受风和沿途风景。"),
        new DecisionCard("娱乐", "打羽毛球", "约个搭档活动一下，轻松出汗也很解压。"),
        new DecisionCard("娱乐", "探索一家新店", "去没尝试过的小店坐坐，也许会有意外惊喜。"),
        new DecisionCard("娱乐", "逛博物馆", "慢慢看看展览，换一种方式度过闲暇时间。"),
        new DecisionCard("娱乐", "看一期综艺", "找一期轻松有趣的节目，放心笑一会儿。"),
        new DecisionCard("娱乐", "读一会儿书", "选一本喜欢的书，安静阅读三十分钟。"),
        new DecisionCard("娱乐", "拼图或做手工", "专注完成一个小作品，享受慢下来的过程。")
    );

    private final Random random = new Random();

    /**
     * 按类别随机获取一个推荐
     * 接口地址：GET /api/decision/random
     *
     * @param type 可选参数，支持"美食"和"娱乐"，为空时从全部选项中随机
     */
    @GetMapping("/api/decision/random")
    public DecisionCard randomDecision(@RequestParam(required = false) String type) {
        List<DecisionCard> candidates;
        if (type != null && !type.isEmpty()) {
            // 按类别过滤
            final String filterType = type;
            candidates = CARDS.stream()
                    .filter(card -> card.type().equals(filterType))
                    .toList();
            // 如果没有匹配的类别，则从全部选项中随机
            if (candidates.isEmpty()) {
                candidates = CARDS;
            }
        } else {
            candidates = CARDS;
        }
        int index = random.nextInt(candidates.size());
        return candidates.get(index);
    }

    /**
     * 内部静态类 - 决策卡片数据结构
     */
    public record DecisionCard(String type, String name, String description) {}
}
