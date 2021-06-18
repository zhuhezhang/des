/**
 * F�ֺ���
 * 
 * @author zhz
 */

public class FFunction {

	/**
	 * F�ֺ���������32bit���ģ����ģ��Ұ벿�֡�48bit����Կ, ����32bit���
	 * 
	 * @param r_plaintext ���ģ����ģ��Ұ벿��
	 * @param subkey      ����Կ
	 * @return f_fun_result ������
	 */
	public static int[] fFuction(int[] r_text, int[] subkey) {
		int[] e_output = new int[48];// E���������
		for (int i = 0; i < 48; i++) {// E��չ 32 --> 48������Կ���
			e_output[i] = r_text[Table.E[i] - 1] ^ subkey[i];
		}

		int[] sbox_output = new int[32];// S�����
		for (int i = 0; i < 8; i++) {// S��ѹ���滻��48bit --> 32bit
			int r = (e_output[i * 6] << 1) + e_output[i * 6 + 5];// S�б���
			int c = (e_output[i * 6 + 1] << 3) + (e_output[i * 6 + 2] << 2) + (e_output[i * 6 + 3] << 1)
					+ e_output[i * 6 + 4];// S�б���
			String str = Integer.toBinaryString(Table.S_BOX[i][r][c]);// S�����м�����0��ʼ
			while (str.length() < 4) {// ���Ȳ�������4λ
				str = "0" + str;
			}
			int p;
			for (int j = 0; j < 4; j++) {// ���ַ���ת��Ϊ��Ӧ�Ķ�����int������
				p = Integer.valueOf(str.charAt(j));
				if (p == 48) {
					p = 0;
				} else {
					p = 1;
				}
				sbox_output[4 * i + j] = p;
			}
		}

		int[] f_fun_result = new int[32];// F�ֺ���������
		for (int i = 0; i < 32; i++) {// P�û�
			f_fun_result[i] = sbox_output[Table.P[i] - 1];
		}
		
		return f_fun_result;
	}

}
