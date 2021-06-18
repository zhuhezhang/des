@[TOC](目录)
# 1.使用说明
本程序使用eclipse Java编写。使用该程序可利用eclipse打开源代码文件夹，然后运行MainBody.java即可根据默认的明文和密钥输出加密、解密结果。
# 2.运行分析
为了得出更真实的运行时间，这里的明文和密钥均为8字节（实际加密解密中多于或者少于8字节都是无关的，明文、密文多了会进行分组，密钥多了会裁切，明文、密文、密钥少于会自动补），经过多次统计平均运行时间，了解到加解密所需时间分别是3毫秒和1毫秒。运行截图如下所示。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210616174220652.png)
# 3.总体设计
## 3.1类及函数
MainBody.java，算法主体部分。构造函数public MainBody(byte[] text, String key, int flag) throws UnsupportedEncodingException，传入明文（密文）、密钥、标识解密或解密。主要用于调用函数产生16个子密钥和对明文（密文）进行分组。main函数用于初始化对象和调用mainbody函数进行加密解密。public byte[] mainBody() throws UnsupportedEncodingException是算法主体部分，主要是对DES算法的各个部分进行实现，返回加密解密后的byte型结果数组。public void eachRound(int[] IP_result, int roundNum)为16轮加密解密中的每一轮的函数，参数分别为IP置换后的结果（也是后一轮的输入）、轮次。
	GenerateSubkeys.java，产生子密钥的类。public static int[][] generateSubkeys(String key) throws UnsupportedEncodingException传入String类型密钥，返回保存16个48位子密钥，用int型二维数组进行保存，形参为密钥。
	GroupText.java，对明文（密文）进行分组的类。public static int[][] groupText(byte[] text_byte) throws UnsupportedEncodingException对传入的明文（密文）分组，以64bit为一组，最后一组不足64bit则补全，以二进制int型数组形式返回分组结果，形参分别是明文（密文）、分组结果。
FFunction.java，F轮函数类。public static int[] fFuction(int[] r_text, int[] subkey) 输入32bit明文（密文）右半部分、48bit子密钥, 返回32bit结果，形参分别是明文（密文）右半部分、子密钥、输出结果。
Table.java，存储DES算法所需要的全部置换、压缩、扩展和S盒表。
## 3.2结构说明
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210616173348285.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzQzNzk0NjMz,size_16,color_FFFFFF,t_70)

如上图所示为DES算法的基本流程，根据此流程图结合本实例来说明一下流程。在MainBody构造函数中调用GroupText.groupText()对明文（密文）进行分组，同时根据GenerateSubkeys.generateSubkeys()产生子密钥。利用MainBody.mainBody函数对整体流程进行包装，先是IP置换，然后利用MainBody.eachRound函数实现16轮加密、解密，其中在这16轮加密、解密中对调用轮函数FFunction.fFuction()，最后在MainBody.mainBody函数中进行IP-1逆置换输出加密解密结果。
# 4.详细设计
Table.java类存储置换、压缩、扩展和S盒表。类中的表均为final型，使其成为常量，除了S盒表采用三维int型数组保存（维度分别是盒号、行号、列号），其他表均是采用一维int型数组保存。类中包含的表有PC-1密钥压缩置换表PC_1（用于将64位密钥压缩置换为56位）、PC-2密钥压缩置换表PC_2（用于将56位密钥压缩置换为48位）、左移位数表LEFT_SHIFT（用于生成每轮所需的子密钥时左移的位数）、IP初始置换表IP（将输入的64位数据块按位重新组合）、E扩展表E（目标是IP置换后获得的右半部分R0，将32位输入扩展为48位）、8个S盒表S_BOX（输入的是压缩后的密钥与扩展分组异或以后得到48位的数据，输出32位数据）、P置换表P（用于置换S盒输出的32位）、IP-1逆初始置换表IP_1（用于DES最后一轮的置换）。
GroupText.java类用于对明文（密文）进行分组。函数groupText通过形参传入byte数组型的明文（密文），先是计算出byte数组的长度，由于DES算法输入的明文（密文）需要是64bit，也就是8byte，因此传入的数据应该是8的整数倍。这里需要先计算要补充字节数，如果需要补充，则新建一个长度为原来的长度加上要补充的字节的长度的byte数组，利用System.arraycopy函数将原来的数据复制到新建的数组，然后利用循环for (int i = 0; i < padding_num; i++) {text_padding[text_byte_length + i] = (byte) padding_num;}在其后面补充数据，补充的数据可任选，这里补充的数据为将补充的字节数转化为byte型进行补充。
补充完成后以8byte为一组计算出共有几组数据，new一个以维度分别是组数和64的二维数组int型数组用于保存每组的二进制数据。如以下代码所示，
```java
for (int i = 0; i < group_num; i++) {// 将每组的byte型转化为二进制int型数组返回
for (int j = 0; j < 8; j++) {// 组内8个字节转为string型二进制数据
		text_bit_tmp = Integer.toBinaryString(text_padding[i * 8 + j] & 0xff);
		while (text_bit_tmp.length() % 8 != 0) {
			text_bit_tmp = "0" + text_bit_tmp;
		}
		text_bit += text_bit_tmp;
	}
	for (int z = 0; z < 64; z++) {// string型二进制数据转为int型二进制数据
		int p_t = Integer.valueOf(text_bit.charAt(z));
		if (p_t == 48) {
			p_t = 0;
		} else {// p_t== 49
			p_t = 1;
		}
		group_result_int[i][z] = p_t;
	}

	text_bit = "";
	text_bit_tmp = "";
}
```
以组数为大循环，逐组进行byte转化为二进制int数据的操作，首先分别将组内的8个byte型数据和0xff（16进制255表示）进行与操作之后利用Integer.toBinaryString()函数将它们的结果转为二进制字符串，也就是byte型的二进制形式，然后检查转化后的长度是否为8位，如果不是则需要在其前面补充0字符串，这样的操作后每组就会生成64长度的string类型的二进制数据。之后再利用64次循环，利用Integer.valueOf检查其中的数据，如果是48则为0，49则为1，然后将0或1保存到之前初始化的int型数组。以同样的操作对每组进行转化后返回结果即可。
	GenerateSubkeys.java类用于产生子密钥。形参传入string类型的密钥，转化为byte数组之后检查其长度是否到8字节，如果不足则上相同的密钥，直到足8字节为止。以类似于明文（密文）分组的步骤，将其转化为二进制int数组存储，这里就不再说明，最后的结果是将这8字节byte数组的密钥转化为64长度的int型二进制数组，接着利用PC_1压缩置换表进行压缩置换，利用System.arraycopy函数将56位的数据平分为左右两部分，在16次循环中逐个产生16个子密钥，根据左移位数表左右两部部分进行移位。
	如果移动1位，则将后27位复制到新的数组，在将原数组的第一个放到新数组的最后一位即可；如果移动两位则类似。然后利用数组复制函数将新的数组组合成新的数组，再对此数组进行PC_2压缩置换成48位数据，保存在subkeys二维数组中。而移位后的左右两部分作为下一个子密钥生成的左右两部分进行同样的16次操作即可生成16个48位的子密钥，最后再返回结果即可。
	FFunction.java类是DES算法对应的F轮函数。形参传入明文（密文）右半部分、子密钥，for (int i = 0; i < 48; i++) {e_output[i] = r_text[Table.E[i] - 1] ^ subkey[i];}利用E扩展之后的数据和子密钥进行异化输出48位二进制型int数据。然后在循环当以6位为一组中分别送入8个S盒，利用左移运算符生成S盒行和列，如int r = (e_output[i * 6] << 1) + e_output[i * 6 + 5];生成行号，之后利用S和表找出对用的数据并生成4位二进制数据，保存到int型数组，经过8个S盒之后数据由48位变成32位后接着进行P置换后就以二进制int一维数组型数据返回结果。
	MainBody.java类为算法主体部分。类中有3个变量，分别是分组后的明文（密文）groupText、16个子密钥subkeys，标识加密还是解密flag（1表示加密，0表示解密）。构造函数传入byte数组型的明文（密文）、String类型的key、int类型的标识flag。构造函数里面如果是加密，则在获取明文byte数组的长度之后再将长度和@符号一起转化为byte数据添加到byte数组前面，方便在解密时确定明文长度从而去掉明文后面的乱码。然后再利用分组函数对此进行分组，若是解密则直接进行分组即可，之后再利用密钥生成16个子密钥。
	MainBody.eachRound函数为16轮加密（解密）的每一轮。形参传入每组加密（解密）IP置换后的int数组保存的二进制数据、加密（解密）轮数。利用数组复制函数将明文（密文）分割为左右两部分，右半部分做为下一轮的左半部分，然后将右半部分和该轮对应的子密钥传入F轮函数返回32位结果后和左半部分进行异或操作并作为下一轮的右半部分。然后需要判断该轮数是否为最后一轮，如果是，则不需要交换左右两边的数据否则交换。
	MainBody.mainBody为算法主体部分。以对明文（密文）分组后的的组数为已循环，逐组8字节进行解密，先是利用IP置换表对其进行置换，如果是加密，则顺者调用eachRound函数利用子密钥进行加密，解密则反之。这个完成之后便对其进行IP-1逆置换，这里得到的还是int数组型的二进制数据，然后利用左移操作符和强制类型将它们转化为byte型数组数据再复制到结果数组返回即可。如果是解密，这里还要进行最后一步，就是找出原先添加到明文前面的数据长度，然后根据明文长度对其进行截取再返回，如果是加密则直接返回。
	主函数利用默认明文（密文）和密钥进行加密（解密），通过构造函数传入，然后调用mainBody函数返回加密（解密）的byte型数组结果，然后利用String的构造函数转化为string类型的数据输出，其中还利用System.currrentTimeMillis时间戳计算加密（解密的时间），平均时间分别是3ms、1ms。
# 5.源码
[https://gitee.com/zhz000/des](https://gitee.com/zhz000/des)
[https://github.com/zhz000/des](https://github.com/zhz000/des)
