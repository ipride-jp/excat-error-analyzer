/*
 * @(#)License.java
 * $Id$
 */

package jp.co.ipride;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public class License {

	private final static String licenseDir;

	public final static String licenseFile;

	//modified by Qiu Song on 20091029 for Ver3.0のライセンス対応
	public final static String verStr = "Ccat=3.0";

	public static boolean hasValidLicense = false;

	static {
		try {
			System.loadLibrary("libLicense");
			licenseDir = System.getProperty("user.dir")
					+ System.getProperty("file.separator") + "license"
					+ System.getProperty("file.separator");
			licenseFile = licenseDir + "license.pem";
			if(isValidFile(licenseFile,verStr)){
				hasValidLicense = true;
				Logger.getLogger("viewerLogger").info("license is valid");
			}else{
				hasValidLicense = false;
				Logger.getLogger("viewerLogger").info("license is invalid");
			}
		} catch (Throwable t) {
			throw new ExceptionInInitializerError(t);
		}
	}

	public static boolean licenseFileExist() {
		File l = new File(licenseFile);
		return l.exists();
	}

	/**
	 * 指定されたライセンスファイルの有効性をチェックする。
	 *
	 * @param file
	 *            ライセンスファイルのパス
	 * @param vers
	 *            「Excat=1.0」様なバージョン情報
	 * @return ライセンスファイルが有効か否か
	 */
	public native static boolean isValidFile(String file, String vers);

	/**
	 * 指定されたライセンスファイルからサブジェクト情報などを取得する。 サブジェクト情報には会社の住所や名前など情報がある。
	 *
	 * @param file
	 *            ライセンスファイルのパス
	 * @return 「/C=JP/ST=Hokkaido/L=Sapporo/O=Another Root CA...」様なサブジェクト情報
	 */
	public native static String getSubject(String file);

	/**
	 * 指定されたライセンスファイルから使用可能なツールのバージョン情報などを取得する。
	 *
	 * @param file
	 *            ライセンスファイルのパス
	 * @return 「Excat<=1.0」様なバージョン情報
	 */
	public native static String getAppVersion(String file);

	/**
	 * 指定されたライセンスファイルから有効期間の開始日付を取得する。
	 *
	 * @param file
	 *            ライセンスファイルのパス
	 * @return 「yymmdd」様な有効期間の開始日付
	 */
	public native static String getStartDate(String file);

	/**
	 * 指定されたライセンスファイルから有効期間の終了日付を取得する。
	 *
	 * @param file
	 *            ライセンスファイルのパス
	 * @return 「yymmdd」様な有効期間の終了日付
	 */
	public native static String getEndDate(String file);

	/**
	 * 指定されたライセンスファイルからパブリックキーを取得する。
	 *
	 * @param file
	 *            ライセンスファイルのパス
	 * @return 1024bit（128byte）のパブリックキー
	 */
	public native static byte[] getPublicKey(String file);

	public static void copyLicenseFile(String filePath) throws IOException {
		File lDir = new File(licenseDir);
		if (!lDir.exists() || lDir.isFile()) {
			if (lDir.mkdirs() != true) {
				//throw new IOException("ライセンスフォルダ(" + licenseDir + ")作成失敗！");
				throw new IOException("\u30E9\u30A4\u30BB\u30F3\u30B9\u30D5\u30A9\u30EB\u30C0\u0028" + licenseDir + "\u0029\u4F5C\u6210\u5931\u6557\uFF01");
			}
		}

		File inputFile = new File(filePath);
		File outputFile = new File(licenseDir + "license.pem");

		FileReader in = new FileReader(inputFile);
		FileWriter out = new FileWriter(outputFile);
		int c;

		while ((c = in.read()) != -1)
			out.write(c);

		in.close();
		out.close();
	}

	public static boolean validate() {
		String licenseFilePath = licenseDir + "license.pem";
		return isValidFile(licenseFilePath, verStr);
	}


	public static void delLicenseFile() {
		File l = new File(licenseFile);
		l.delete();
		hasValidLicense = false;
	}

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out
					.println("usage: License license_file application_version\n");
			System.out
					.println("\tlicense_file: name of PEM encoded license file\n");
			System.out
					.println("\tapplication_version: name and application version in which the license file contains.\n");
			System.exit(-1);
		}

		String f = args[0];
		String v = args[1];
		boolean r = isValidFile(f, v);
		System.out.println(f + " is" + (r ? "" : " NOT")
				+ " valid license for " + v + ".");
		System.out.println("The subject of " + f + ": " + getSubject(f));
		System.out.println("The application version of " + f + ": "
				+ getAppVersion(f));
		System.out.println("The start date of " + f + ": " + getStartDate(f));
		System.out.println("The end date of " + f + ": " + getEndDate(f));
		System.out.println("The public key's size of " + f + ": "
				+ getPublicKey(f).length);
	}

	public static boolean isHasValidLicense() {
		return hasValidLicense;
	}

	public static void setHasValidLicense(boolean hasValidLicense) {
		License.hasValidLicense = hasValidLicense;
	}
}

/* the end of file */
