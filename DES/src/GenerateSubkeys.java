import java.io.UnsupportedEncodingException;

/**
 * ��������Կ
 * 
 * @author zhz
 */

public class GenerateSubkeys {

	/**
	 * ������Կ�����ر���16��48λ����Կ��int�Ͷ�ά����
	 * 
	 * @param key ��Կ
	 * @return subkeys ����Կ
	 * @throws UnsupportedEncodingException
	 */
	public static int[][] generateSubkeys(String key) throws UnsupportedEncodingException {
		int subkeys[][] = new int[16][48];// ����Կ
		while (key.getBytes("GBK").length < 8) {// ��Կ����8�ֽ���
			key += key;
		}

		byte[] key_byte = key.getBytes("GBK");
		int[] key_bit = new int[64];
		String key_string = "";
		for (int i = 0; i < 8; i++) {
			key_string = Integer.toBinaryString(key_byte[i] & 0xff);// ��λ������������൱�ڽ�����ת��Ϊ��Ӧ�Ķ������ַ���
			if (key_string.length() < 8) {// ����8λ����ǰ�油0
				for (int j = 0; j <= 8 - key_string.length(); j++) {
					key_string = "0" + key_string;
				}
			}

			for (int j = 0; j < 8; j++) {// ����Ӧ�Ķ������ַ���ת��Ϊint�����鴢��
				int b = Integer.valueOf(key_string.charAt(j));
				if (b == 48) {
					b = 0;
				} else {// b==49
					b = 1;
				}
				key_bit[i * 8 + j] = b;
			}
		}

		int[] pc1_key_bit = new int[56];
		for (int i = 0; i < 56; i++) {// ѹ���û���64 --> 56
			pc1_key_bit[i] = key_bit[Table.PC_1[i] - 1];
		}

		int[] l0 = new int[28];
		int[] r0 = new int[28];
		System.arraycopy(pc1_key_bit, 0, l0, 0, 28);
		System.arraycopy(pc1_key_bit, 28, r0, 0, 28);// ��pc1_key_bit��28λ��ʼ�ĺ�28λ���Ƶ�r0����0���꿪ʼ��λ��
		for (int i = 0; i < 16; i++) {// ����16������Կ
			int[] l1 = new int[28];
			int[] r1 = new int[28];
			if (Table.LEFT_SHIFT[i] == 1) {// ���������ֱַ�����1λ
				System.arraycopy(l0, 1, l1, 0, 27);
				l1[27] = l0[0];
				System.arraycopy(r0, 1, r1, 0, 27);
				r1[27] = r0[0];
			} else {// ���������ֱַ�����2λ
				System.arraycopy(l0, 2, l1, 0, 26);
				l1[26] = l0[0];
				l1[27] = l0[1];
				System.arraycopy(r0, 2, r1, 0, 26);
				r1[26] = r0[0];
				r1[27] = r0[1];
			}

			int[] pc2_key_bit = new int[56];
			System.arraycopy(l1, 0, pc2_key_bit, 0, 28);
			System.arraycopy(r1, 0, pc2_key_bit, 28, 28);
			for (int j = 0; j < 48; j++) {// pc2ѹ���û���56 --> 48
				subkeys[i][j] = pc2_key_bit[Table.PC_2[j] - 1];
			}

			l0 = l1;
			r0 = r1;
		}

		return subkeys;
	}

}
