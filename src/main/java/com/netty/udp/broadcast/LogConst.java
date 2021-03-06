package com.netty.udp.broadcast;

import java.util.Random;

/**
 * 作者：DarkKIng
 * 类说明：日志信息，用String数组代替
 */
public class LogConst {
    public final static int MONITOR_SIDE_PORT = 9998;
    private static final String[] LOG_INFOS = {
            "晨光微好，暖在夕阳。幽幽的巷子里，有着岁月酝酿的酒，愈久愈淳。一笔墨香，一盏明灯，记千帆过浪，数不尽的悲欢离合，待那水莲花开。",
            "未来无期，静在安好。一剪寒梅，纷扰了岁月，抚平了伤痕。摆动的双桨，拨动了心的潭水。陌上花开，落一地秋霜，红枫染了红尘，便许了你十里红装。",
            "离别的风，风干了月的泪。夜里的美",
            "是梦的呢喃低语，挥走一片彩云，段落成珠。拂袖离去，乘鹤而来，古道西风瘦马。斑驳的树影中，眉目如画的眼，轻语告别了往事如烟。",
            "无言的殇，几世沧桑，几生悲凉。一起剪了西窗烛，听了夜来风吹雨。昨日的叹息，今日的迷离，执一伞，存了一世一笔的温情。一曲长歌，唱尽了一世繁华，一世缘……",
            "一世恋书，那便十里花开。一生凄凉，那便霜花十里。" ,
                    "一抹浓烟，便翻页书，展颜一笑，是时间带来遥远的梦。细数树的年轮，感受昨日惆怅，留一半清醒，梦一半叶落。在指尖流过的沙，海边浪花一朵朵，不相遇，才有不约而同。",
            "这世俗，太多牵挂留在心间，一点朱砂泪，一曲相诗歌。岁月朦胧，梦醒了人生，风雨相容，演绎了一段风情。雪亦梦，雨亦梦，万张红纸从天洒来。惊动了山，撼动了天。" +
                    "一纸情愁，一指烟凉。一相思，一思量，水漫岸头，我们都有着自己不同的三生故事。迎一夜秋风，送一世暖阳，一切冰雪里的花开，是我一生的柔情。" +
                    "记忆中的短笛，有着清风须来的气息，那时我们面向大海，海风在耳边述说着大海边缘的温暖故事。安好一轮冷月，静好了一残红日，这便是我的语言，我的情丝。" +
                    "一漫山水，一段情，留在了岁月，拭去了风，晴雨清风，倒是暖阳拂绿草。" +
                    "这便，晨光微好，花开静好……"};

    private final static Random r = new Random();
    public static String getLogInfo(){
        return LOG_INFOS[r.nextInt(LOG_INFOS.length-1)];
    }
}
