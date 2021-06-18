import java.io.UnsupportedEncodingException;

/**
 * �����ģ����ģ����з���
 * 
 * @author zhz
 */

public class GroupText {

	/**
	 * �Դ�������ģ����ģ����飬��64bitΪһ�飬���һ�鲻��64bit��ȫ���Զ�����int��������ʽ���ط�����
	 * 
	 * @param text_byte ���ģ����ģ�
	 * @return int_group_result ������
	 * @throws UnsupportedEncodingException
	 */
	public static int[][] groupText(byte[] text_byte) throws UnsupportedEncodingException {
		int text_byte_length = text_byte.length;// ���ģ����ģ��ֽ���
		int padding_num = 8 - text_byte_length % 8;// Ҫ������ֽ���

		byte[] text_padding;// ����������ģ����ģ�����
		if (padding_num != 8) {// �����8������
			text_padding = new byte[text_byte_length + padding_num];
			System.arraycopy(text_byte, 0, text_padding, 0, text_byte_length);
			for (int i = 0; i < padding_num; i++) {
				text_padding[text_byte_length + i] = (byte) padding_num;
			}
		} else {
			text_padding = text_byte;
		}

		int group_num = text_padding.length / 8;// ���м������ģ����ģ�
		String text_bit = "";
		String text_bit_tmp = "";
		int[][] group_result_int = new int[group_num][64];
		for (int i = 0; i < group_num; i++) {// ��ÿ���byte��ת��Ϊ������int�����鷵��
			for (int j = 0; j < 8; j++) {// ����8���ֽ�תΪstring�Ͷ���������
				text_bit_tmp = Integer.toBinaryString(text_padding[i * 8 + j] & 0xff);
				while (text_bit_tmp.length() % 8 != 0) {
					text_bit_tmp = "0" + text_bit_tmp;
				}
				text_bit += text_bit_tmp;
			}

			for (int z = 0; z < 64; z++) {// string�Ͷ���������תΪint�Ͷ���������
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

		return group_result_int;
	}

}
