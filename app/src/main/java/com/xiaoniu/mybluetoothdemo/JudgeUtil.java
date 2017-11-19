package com.xiaoniu.mybluetoothdemo;



public class JudgeUtil {
    /**
     * 对浮点数进行保留多少位小数的操作，并对末位进行四舍五入操作
     * @param decimal 需要进行格式化操作的浮点数
     * @param num 保留小数的位数
     * @return
     */
    public static double formatDecimal(double decimal, int num) {
        if (num < 0) {
            throw new IllegalArgumentException("num必须为非负整数");
        }
        double pow = Math.pow(10, num);
        return (Math.round(decimal * pow) / pow);
    }

    /**
     * byte类型转换为无符号整型数
     *
     * @param bt
     * @return
     */
    public static int byteToInt(byte bt) {
        return bt & 0xff;
    }

    /**
     * 根据麻将编码获取对应图片的id号
     *
     * @param majiangCode
     * @return
     */

    public static int getImage(int majiangCode) {
        int imageId = R.drawable.wu;

        switch (majiangCode) {
            // 一条到九条
            case 4:
            case 5:
            case 6:
            case 7:
                imageId = R.drawable.t1;
                break;

            case 8:
            case 9:
            case 10:
            case 11:
                imageId = R.drawable.t2;
                break;

            case 12:
            case 13:
            case 14:
            case 15:
                imageId = R.drawable.t3;
                break;

            case 16:
            case 17:
            case 18:
            case 19:
                imageId = R.drawable.t4;
                break;

            case 20:
            case 21:
            case 22:
            case 23:
                imageId = R.drawable.t5;
                break;

            case 24:
            case 25:
            case 26:
            case 27:
                imageId = R.drawable.t6;
                break;

            case 28:
            case 29:
            case 30:
            case 31:
                imageId = R.drawable.t7;
                break;

            case 32:
            case 33:
            case 34:
            case 35:
                imageId = R.drawable.t8;
                break;

            case 36:
            case 37:
            case 38:
            case 39:
                imageId = R.drawable.t9;
                break;

            // 一饼到九饼
            case 44:
            case 45:
            case 46:
            case 47:
                imageId = R.drawable.b1;
                break;

            case 48:
            case 49:
            case 50:
            case 51:
                imageId = R.drawable.b2;
                break;

            case 52:
            case 53:
            case 54:
            case 55:
                imageId = R.drawable.b3;
                break;

            case 56:
            case 57:
            case 58:
            case 59:
                imageId = R.drawable.b4;
                break;

            case 60:
            case 61:
            case 62:
            case 63:
                imageId = R.drawable.b5;
                break;

            case 64:
            case 65:
            case 66:
            case 67:
                imageId = R.drawable.b6;
                break;

            case 68:
            case 69:
            case 70:
            case 71:
                imageId = R.drawable.b7;
                break;

            case 72:
            case 73:
            case 74:
            case 75:
                imageId = R.drawable.b8;
                break;

            case 76:
            case 77:
            case 78:
            case 79:
                imageId = R.drawable.b9;
                break;

            // 一万到四万
            case 84:
            case 85:
            case 86:
            case 87:
                imageId = R.drawable.w1;
                break;

            case 88:
            case 89:
            case 90:
            case 91:
                imageId = R.drawable.w2;
                break;

            case 92:
            case 93:
            case 94:
            case 95:
                imageId = R.drawable.w3;
                break;

            case 96:
            case 97:
            case 98:
            case 99:
                imageId = R.drawable.w4;
                break;

            case 100:
            case 101:
            case 102:
            case 103:
                imageId = R.drawable.w5;
                break;

            case 104:
            case 105:
            case 106:
            case 107:
                imageId = R.drawable.w6;
                break;

            case 108:
            case 109:
            case 110:
            case 111:
                imageId = R.drawable.w7;
                break;

            case 112:
            case 113:
            case 114:
            case 115:
                imageId = R.drawable.w8;
                break;

            case 116:
            case 117:
            case 118:
            case 119:
                imageId = R.drawable.w9;
                break;

            // 东南西北中发白
            case 124:
            case 125:
            case 126:
            case 127:
                imageId = R.drawable.d;
                break;

            case 128:
            case 129:
            case 130:
            case 131:
                imageId = R.drawable.n;
                break;

            case 132:
            case 133:
            case 134:
            case 135:
                imageId = R.drawable.x;
                break;

            case 136:
            case 137:
            case 138:
            case 139:
                imageId = R.drawable.b;
                break;

            case 140:
            case 141:
            case 142:
            case 143:
                imageId = R.drawable.z;
                break;

            case 144:
            case 145:
            case 146:
            case 147:
                imageId = R.drawable.f;
                break;

            case 148:
            case 149:
            case 150:
            case 151:
                imageId = R.drawable.k;
                break;

            // 春夏秋冬 梅兰菊竹
            case 152:
                imageId = R.drawable.chun;
                break;
            case 153:
                imageId = R.drawable.xia;
                break;
            case 154:
                imageId = R.drawable.qiu;
                break;
            case 155:
                imageId = R.drawable.dong;
                break;
            case 156:
                imageId = R.drawable.mei;
                break;
            case 157:
                imageId = R.drawable.lan;
                break;
            case 158:
                imageId = R.drawable.ju;
                break;
            case 159:
                imageId = R.drawable.zhu;
                break;
        }

        return  imageId;
    }

}

