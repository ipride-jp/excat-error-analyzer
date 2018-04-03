package jp.co.ipride.excat.analyzer.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jp.co.ipride.ExcatLicenseException;
import jp.co.ipride.License;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

/**
 * this is a static class to process dump file for SAX parse and DOM parse.
 * @author tu-ipride
 * @version 3.0
 * @since 2009/10/4
 */
public class DumpFile {

	static String  CHECK_WORKS = "<?xml version=\"1";

	/**
	 *
	 * @param path
	 * @return
	 */
	public static String getDumpFileContent(String path)throws ExcatLicenseException{
		byte[] total=null;
		String fileType = path.substring(path.lastIndexOf(".")+1);

		if("zip".equals(fileType) || "ZIP".equals(fileType)){
			total = getByteFromZipFile(path);
		}else{
			total = getByteFromNormalFile(path);
		}
		if (total == null){
			return null;
		}else{
			return licenceCheckAndTransfer(total);
		}
	}

	/**
	 * process zip file.
	 * @param fileName
	 * @return
	 */
	private static byte[] getByteFromZipFile(String path){
		byte[] total = null;
		ZipFile zipFile = null;
		InputStream is = null;
		try{
			zipFile = new ZipFile(path);
			Enumeration entries = zipFile.entries();
			int entriesCount = 0;
			while(entries.hasMoreElements()){
				ZipEntry entyr = (ZipEntry) entries.nextElement();
				total = new byte[(int) entyr.getSize()];
				is = zipFile.getInputStream(entyr);
				byte[] buf = new byte[1024];
				int destPos = 0;
				if(!entyr.isDirectory()){
					while(true){
						int len = is.read(buf,0,buf.length);
						if(len == -1){
							break;
						}else{
							System.arraycopy(buf, 0, total,
									destPos, len);
							destPos += len;
						}
					}
				}
				entriesCount++;
				is.close();
			}
			if(entriesCount > 1){
				ExcatMessageUtilty.showMessage(null, Message.get("DumpDocument.zip.countError"));
				return null;
			}
			return total;
		}catch(Exception e){
			String msg = Message.getMsgWithParam("DumpDocument.openFailed", path);
			HelperFunc.getLogger().error(msg, e);
			ExcatMessageUtilty.showMessage(null,msg);
			return null;
		}finally{
			if(is != null){
				try {
					is.close();
				} catch (IOException e) {
					HelperFunc.getLogger().debug(e);
				}
			}
			if(zipFile != null){
				try {
					zipFile.close();
				} catch (IOException e) {
					HelperFunc.getLogger().debug(e);
				}
			}
		}
	}

	/**
	 * process *.dat file.
	 * @param path
	 * @return
	 */
	private static byte[] getByteFromNormalFile(String path){
		byte[] total=null;
		InputStream stream=null;
		try {
			stream = new FileInputStream(path);
			total = new byte[stream.available()];
			stream.read(total);
			return total;
		}catch(Exception e){
			String msg = Message.getMsgWithParam("DumpDocument.openFailed", path);
			HelperFunc.getLogger().debug(msg, e);
			ExcatMessageUtilty.showMessage(null,msg);
			return null;
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				HelperFunc.getLogger().debug(e);
			}
		}
	}

	/**
	 * check licence.
	 * transfer byte file to string
	 * @param total
	 * @return
	 */
	private static String licenceCheckAndTransfer(byte[] total) throws ExcatLicenseException{
		byte[] pKey = null;
		if(License.isHasValidLicense()){
			pKey = License.getPublicKey(License.licenseFile);
		}

		if (pKey == null) {
			throw new ExcatLicenseException();
//			String msg=ApplicationResource.getResource("LicenseDialog.Check.ErrMessgae");
//			ExcatMessageUtilty.showMessage(null,msg);
//			return null;
		}

		byte[] key = new byte[16];
		for (int i = 0; i < key.length; i++) {
			key[i] = pKey[i];
		}

		if (!checkLicense(total, key)){
//			ExcatMessageUtilty.showMessage(null, ApplicationResource.getResource("LicenseDialog.Check.ErrFileMsg"));
//			return null;
			throw new ExcatLicenseException();
		}

		for (int i = 0; i < total.length; i++) {
			total[i] = (byte) (total[i] ^ key[i % key.length]);
		}
		try {
			return new String(total, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			HelperFunc.getLogger().error("DumpFile", e);
			return null;
		}
	}

	/**
	 * ダンプファイルのライセンス・キーは該当ツールが持っているキーと同じものかをチェック
	 * @param stream
	 * @param key
	 * @since 2007/5/18
	 * @return
	 */
	private static boolean checkLicense(byte[] total, byte[] key){
		try {
			byte[] first = new byte[16];
			for (int i = 0; i < 16; i++) {
				first[i] = (byte) (total[i] ^ key[i % key.length]);
			}
			String result = new String(first, "UTF-8");

			if (CHECK_WORKS.equals(result)){
				return true;
			}else{
				return false;
			}

		} catch (UnsupportedEncodingException e) {
			HelperFunc.logException(e);
			return false;
		}
	}

}
